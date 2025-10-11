package com.gpu.rentaler.utils;

import com.gpu.rentaler.entity.DContainerInfo;
import com.gpu.rentaler.entity.ProcessInfo;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Docker命令执行工具类
 * 基于Apache Commons Exec实现
 */
public class DockerExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DockerExecutor.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 60;

    // ===================== 执行结果封装类 =====================
    public static class ExecuteResult {
        private final int exitCode;
        private final String output;
        private final String error;

        public ExecuteResult(int exitCode, String output, String error) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }

        public int getExitCode() { return exitCode; }
        public String getOutput() { return output; }
        public String getError() { return error; }
        public boolean isSuccess() { return exitCode == 0; }

        @Override
        public String toString() {
            return String.format("ExitCode: %d, Output: %s, Error: %s",
                exitCode, output, error);
        }
    }

    // ===================== 基础命令执行 =====================
    public static ExecuteResult execute(String command, int timeoutSeconds) throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        return executeCommand(cmdLine, timeoutSeconds);
    }

    public static ExecuteResult execute(String command) throws IOException {
        return execute(command, DEFAULT_TIMEOUT_SECONDS);
    }

    private static ExecuteResult executeCommand(CommandLine cmdLine, int timeoutSeconds) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler(outputStream, errorStream));
        executor.setExitValues(null); // 允许非0退出码

        ExecuteWatchdog watchdog = new ExecuteWatchdog(TimeUnit.SECONDS.toMillis(timeoutSeconds));
        executor.setWatchdog(watchdog);

        int exitCode;
        try {
            logger.debug("执行命令: {}", cmdLine);
            exitCode = executor.execute(cmdLine);
        } catch (ExecuteException e) {
            exitCode = e.getExitValue();
            logger.warn("命令执行返回非0退出码: {}", exitCode);
        }

        String output = outputStream.toString().trim();
        String error = errorStream.toString().trim();

        if (watchdog.killedProcess()) {
            error = "命令执行超时 (" + timeoutSeconds + "秒)";
            logger.error(error);
        }

        return new ExecuteResult(exitCode, output, error);
    }

    public static boolean isDockerAvailable() {
        try {
            ExecuteResult result = execute("docker version", 5);
            return result.isSuccess();
        } catch (IOException e) {
            logger.error("Docker不可用", e);
            return false;
        }
    }

    // ===================== 容器管理 =====================
    public static ExecuteResult listContainers(boolean all) throws IOException {
        return execute(all ? "docker ps -a" : "docker ps");
    }

    public static List<String> getAllContainerIds(boolean all) throws IOException {
        String cmd = all ? "docker ps -a -q" : "docker ps -q";
        ExecuteResult result = execute(cmd);

        List<String> containerIds = new ArrayList<>();
        if (result.isSuccess() && result.getOutput() != null && !result.getOutput().isEmpty()) {
            for (String id : result.getOutput().split("\\n")) {
                if (!id.trim().isEmpty()) containerIds.add(id.trim());
            }
        }
        return containerIds;
    }

    public static ExecuteResult stopContainer(String containerId, int timeout) throws IOException {
        return execute(String.format("docker stop -t %d %s", timeout, containerId));
    }

    public static ExecuteResult removeContainer(String containerId, boolean force) throws IOException {
        return execute(force ? "docker rm -f " + containerId : "docker rm " + containerId);
    }

    public static ExecuteResult deleteContainerById(String containerId, boolean force) throws IOException {
        if (containerId == null || containerId.trim().isEmpty()) throw new IllegalArgumentException("容器ID不能为空");
        return removeContainer(containerId, force);
    }

    public static DContainerInfo runContainerAndGetInfo(String imageName, List<Integer> gpuIndexes) throws IOException {
        if (imageName == null || imageName.trim().isEmpty()) throw new IllegalArgumentException("镜像名不能为空");

        String containerName = "container-" + UUID.randomUUID().toString().substring(0, 8);
        StringBuilder cmdBuilder = new StringBuilder("docker run -d --name ").append(containerName).append(" ");

        if (gpuIndexes != null && !gpuIndexes.isEmpty()) {
            for (Integer index : gpuIndexes) {
                if (index == null || index < 0) throw new IllegalArgumentException("GPU索引不能为 null 或负数");
            }
            String joinedIndexes = gpuIndexes.stream().map(String::valueOf).collect(Collectors.joining(","));
            cmdBuilder.append("--gpus \"device=").append(joinedIndexes).append("\" ");
        }
        cmdBuilder.append("  --pid=host ");

        String cmd = cmdBuilder.append(imageName).toString();
        ExecuteResult result = execute(cmd);

        if (!result.isSuccess()) throw new IOException("容器启动失败: " + result.getError());
        String containerId = result.getOutput().trim();
        if (!containerId.matches("^[a-f0-9]{12,64}$")) throw new IOException("容器ID解析失败: " + containerId);

        logger.info("成功启动容器: ID={}, Name={}, GPUs={}", containerId, containerName, gpuIndexes);
        return new DContainerInfo(containerName, containerId);
    }
    public record PidContainerRecord(
        String pid,
        String containerId,
        String processName
    ) {}

    public static List<PidContainerRecord> getPidContainerInfo(List<String> pids) throws IOException {
        if (pids == null || pids.isEmpty()) return List.of();

        List<PidContainerRecord> resultList = new ArrayList<>();

        // 获取所有运行中的容器
        List<String> containerIds = DockerExecutor.getAllContainerIds(false);
        if (containerIds.isEmpty()) return resultList;

        for (String containerId : containerIds) {
            ExecuteResult result = DockerExecutor.execute("docker top " + containerId, 5);
            if (!result.isSuccess()) {
                System.err.println("docker top 执行失败: " + result.getError());
                continue;
            }

            String output = result.getOutput();
            if (output == null || output.isEmpty()) continue;

            String[] lines = output.split("\\r?\\n");
            if (lines.length < 2) continue; // 没有进程数据

            // 解析每一行进程信息
            for (int i = 1; i < lines.length; i++) { // 跳过表头
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                // 保留 CMD 中空格，所以 split 只拆前7列，剩余是 CMD
                String[] cols = line.split("\\s+", 8);
                if (cols.length < 2) continue;

                String pid = cols[1]; // PID 列
                if (!pids.contains(pid)) continue;

                String cmd = cols.length == 8 ? cols[7] : "";

                resultList.add(new PidContainerRecord(pid, containerId, cmd));
            }
        }

        return resultList;
    }

    public static ExecuteResult inspectContainer(String containerId) throws IOException {
        return execute("docker inspect " + containerId);
    }

    public static String getImageIdByContainerId(String containerId) throws IOException {
        if (containerId == null || containerId.trim().isEmpty()) throw new IllegalArgumentException("容器ID不能为空");

        ExecuteResult inspectResult = inspectContainer(containerId);
        if (!inspectResult.isSuccess()) throw new IOException("无法获取容器信息: " + inspectResult.getError());

        Matcher matcher = Pattern.compile("\"Image\"\\s*:\\s*\"([^\"]+)\"").matcher(inspectResult.getOutput());
        if (matcher.find()) return matcher.group(1).trim();

        throw new IOException("无法解析容器对应的镜像ID");
    }

    // ===================== 镜像管理 =====================
    public static ExecuteResult pullImage(String image) throws IOException {
        return execute("docker pull " + image, 300);
    }

    public static ExecuteResult listImages() throws IOException {
        return execute("docker images");
    }

    public static ExecuteResult removeImage(String imageId, boolean force) throws IOException {
        return execute(force ? "docker rmi -f " + imageId : "docker rmi " + imageId);
    }

    public static ExecuteResult deleteImageByImageId(String imageId, boolean force) throws IOException {
        if (imageId == null || imageId.trim().isEmpty()) throw new IllegalArgumentException("镜像ID不能为空");
        return removeImage(imageId, force);
    }

    public static ExecuteResult buildImage(String dockerfilePath, String tag) throws IOException {
        return execute(String.format("docker build -t %s %s", tag, dockerfilePath), 600);
    }

    public static String loadImageFromInputStream(InputStream inputStream) throws IOException {
        File file = writeToTempFile(inputStream, "temp-", ".tar");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CommandLine cmdLine = CommandLine.parse("docker load -i " + file.getAbsolutePath());

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(outputStream, new ByteArrayOutputStream(), inputStream));
            executor.setExitValues(null);

            try {
                logger.debug("开始导入Docker镜像...");
                executor.execute(cmdLine);
            } catch (ExecuteException e) {
                logger.warn("docker load 执行失败，退出码: {}", e.getExitValue());
            }

            String output = outputStream.toString().trim();
            logger.debug("docker load 输出: \n{}", output);

            Matcher nameMatcher = Pattern.compile("Loaded image:\\s*(.+)").matcher(output);
            if (nameMatcher.find()) return nameMatcher.group(1).trim();

            Matcher idMatcher = Pattern.compile("Loaded image ID:\\s*(sha256:[a-f0-9]{64})").matcher(output);
            if (idMatcher.find()) return idMatcher.group(1).trim();

            logger.warn("未能从输出中提取镜像名或ID，原始输出: {}", output);
            return null;
        } finally {
            file.delete();
        }
    }

    public static File writeToTempFile(InputStream inputStream, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    // ===================== 日志与导出 =====================
    public static ExecuteResult getLogs(String containerId, int tailLines) throws IOException {
        String cmd = tailLines > 0 ? String.format("docker logs --tail %d %s", tailLines, containerId) : "docker logs " + containerId;
        return execute(cmd);
    }

    public static String getLatestLogs(String containerId, int tailLines) throws IOException {
        ExecuteResult result = getLogs(containerId, tailLines);
        if (!result.isSuccess()) throw new IOException("获取日志失败: " + result.getError());
        return result.getOutput();
    }

    public static InputStream exportContainerDirContentAsTarGz(String containerId, String containerDirPath) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File extractRoot = new File(tempDir, "docker-export-" + UUID.randomUUID());
        if (!extractRoot.mkdirs()) throw new IOException("创建临时目录失败: " + extractRoot.getAbsolutePath());

        File exportTarget = new File(extractRoot, "data");
        if (!exportTarget.mkdirs()) throw new IOException("创建导出数据目录失败: " + exportTarget.getAbsolutePath());

        String dockerCpCmd = String.format("docker cp %s:%s/. %s", containerId, containerDirPath, exportTarget.getAbsolutePath());
        ExecuteResult cpResult = execute(dockerCpCmd, 30);
        if (!cpResult.isSuccess()) throw new IOException("docker cp 执行失败: " + cpResult.getError());

        File tarGzFile = File.createTempFile("docker-dir-content-", ".tar.gz");
        tarGzFile.deleteOnExit();

        String tarCmd = String.format("tar -czf %s -C %s .", tarGzFile.getAbsolutePath(), exportTarget.getAbsolutePath());
        ExecuteResult tarResult = execute(tarCmd, 30);
        if (!tarResult.isSuccess()) throw new IOException("压缩失败: " + tarResult.getError());

        return new FileInputStream(tarGzFile);
    }

}
