package com.gpu.rentaler.utils;

import com.gpu.rentaler.entity.DContainerInfo;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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

    /**
     * 执行结果封装类
     */
    public static class ExecuteResult {
        private final int exitCode;
        private final String output;
        private final String error;

        public ExecuteResult(int exitCode, String output, String error) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getOutput() {
            return output;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return exitCode == 0;
        }

        @Override
        public String toString() {
            return String.format("ExitCode: %d, Output: %s, Error: %s",
                exitCode, output, error);
        }
    }

    /**
     * 执行Docker命令
     */
    public static ExecuteResult execute(String command, int timeoutSeconds)
        throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        return executeCommand(cmdLine, timeoutSeconds);
    }

    /**
     * 执行Docker命令（使用默认超时时间）
     */
    public static ExecuteResult execute(String command) throws IOException {
        return execute(command, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 列出所有容器
     */
    public static ExecuteResult listContainers(boolean all) throws IOException {
        String cmd = all ? "docker ps -a" : "docker ps";
        return execute(cmd);
    }

    /**
     * 获取所有容器ID
     *
     * @param all true-包含已停止的容器, false-仅运行中的容器
     * @return 容器ID列表
     */
    public static java.util.List<String> getAllContainerIds(boolean all)
        throws IOException {
        String cmd = all ?
            "docker ps -a -q" : "docker ps -q";
        ExecuteResult result = execute(cmd);

        java.util.List<String> containerIds = new java.util.ArrayList<>();
        if (result.isSuccess() && result.getOutput() != null &&
            !result.getOutput().isEmpty()) {
            String[] ids = result.getOutput().split("\\n");
            for (String id : ids) {
                String trimmedId = id.trim();
                if (!trimmedId.isEmpty()) {
                    containerIds.add(trimmedId);
                }
            }
        }
        return containerIds;
    }

    /**
     * 启动容器
     */
    public static ExecuteResult startContainer(String containerId) throws IOException {
        return execute("docker start " + containerId);
    }

    /**
     * 停止容器
     */
    public static ExecuteResult stopContainer(String containerId, int timeout)
        throws IOException {
        return execute(String.format("docker stop -t %d %s", timeout, containerId));
    }

    /**
     * 删除容器
     */
    public static ExecuteResult removeContainer(String containerId, boolean force)
        throws IOException {
        String cmd = force ?
            "docker rm -f " + containerId : "docker rm " + containerId;
        return execute(cmd);
    }

    /**
     * 运行新容器
     */
    public static ExecuteResult runContainer(String image, String containerName,
                                             Map<String, String> portMappings, Map<String, String> envVars)
        throws IOException {
        StringBuilder cmd = new StringBuilder("docker run -d");

        if (containerName != null && !containerName.isEmpty()) {
            cmd.append(" --name ").append(containerName);
        }

        if (portMappings != null) {
            for (Map.Entry<String, String> entry : portMappings.entrySet()) {
                cmd.append(" -p ").append(entry.getKey())
                    .append(":").append(entry.getValue());
            }
        }

        if (envVars != null) {
            for (Map.Entry<String, String> entry : envVars.entrySet()) {
                cmd.append(" -e ").append(entry.getKey())
                    .append("=").append(entry.getValue());
            }
        }

        cmd.append(" ").append(image);
        return execute(cmd.toString());
    }

    /**
     * 在容器中执行命令
     */
    public static ExecuteResult execInContainer(String containerId, String command)
        throws IOException {
        return execute(String.format("docker exec %s %s", containerId, command));
    }

    /**
     * 查看容器日志
     */
    public static ExecuteResult getLogs(String containerId, int tailLines)
        throws IOException {
        String cmd = tailLines > 0 ?
            String.format("docker logs --tail %d %s", tailLines, containerId) :
            "docker logs " + containerId;
        return execute(cmd);
    }

    /**
     * 拉取镜像
     */
    public static ExecuteResult pullImage(String image) throws IOException {
        return execute("docker pull " + image, 300); // 5分钟超时
    }

    /**
     * 列出镜像
     */
    public static ExecuteResult listImages() throws IOException {
        return execute("docker images");
    }

    /**
     * 删除镜像
     */
    public static ExecuteResult removeImage(String imageId, boolean force)
        throws IOException {
        String cmd = force ?
            "docker rmi -f " + imageId : "docker rmi " + imageId;
        return execute(cmd);
    }

    /**
     * 构建镜像
     */
    public static ExecuteResult buildImage(String dockerfilePath, String tag)
        throws IOException {
        String cmd = String.format("docker build -t %s %s", tag, dockerfilePath);
        return execute(cmd, 600); // 10分钟超时
    }

    /**
     * 检查Docker是否可用
     */
    public static boolean isDockerAvailable() {
        try {
            ExecuteResult result = execute("docker version", 5);
            return result.isSuccess();
        } catch (IOException e) {
            logger.error("Docker不可用", e);
            return false;
        }
    }

    /**
     * 获取容器详细信息
     */
    public static ExecuteResult inspectContainer(String containerId)
        throws IOException {
        return execute("docker inspect " + containerId);
    }

    /**
     * 核心执行方法
     */
    private static ExecuteResult executeCommand(CommandLine cmdLine, int timeoutSeconds)
        throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler(outputStream, errorStream));

        // 设置超时
        ExecuteWatchdog watchdog = new ExecuteWatchdog(
            TimeUnit.SECONDS.toMillis(timeoutSeconds));
        executor.setWatchdog(watchdog);

        // 设置退出码处理（允许非0退出码）
        executor.setExitValues(null);

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

    /**
     * 使用示例
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        File file = new File("D:/Project/GPU_Rentaler_0/files/6g676_2048.tar");
        InputStream inputStream = new FileInputStream(file);

        loadImageFromInputStream(inputStream);

        inputStream.close();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        CommandLine cmdLine = CommandLine.parse("docker load -i " + );
//        DefaultExecutor executor = new DefaultExecutor();
//        executor.setStreamHandler(new PumpStreamHandler(outputStream));
//        executor.setExitValues(null);
//        executor.execute(cmdLine);
//
//        String output = outputStream.toString().trim();
//        System.out.println(output);
    }

    /**
     * 根据PID获取进程名称
     *
     * @param containerId 容器ID
     * @param pid         进程ID
     * @return 进程名称，如果未找到返回null
     */
    public static String getProcessNameByPid(String containerId, int pid)
        throws IOException {
        // 使用ps命令查询进程名称
        String cmd = String.format("docker exec %s ps -p %d -o comm=",
            containerId, pid);
        ExecuteResult result = execute(cmd, 5);

        if (result.isSuccess() && result.getOutput() != null &&
            !result.getOutput().isEmpty()) {
            return result.getOutput().trim();
        }

        logger.warn("未找到容器 {} 中PID为 {} 的进程", containerId, pid);
        return null;
    }

    /**
     * 从InputStream导入Docker镜像（docker load）
     * 通常用于将镜像.tar文件加载到本地镜像列表中
     *
     * @param inputStream    镜像文件输入流（通常是tar文件）
     * @return 执行结果
     * @throws IOException 加载失败时抛出
     */
    public static String loadImageFromInputStream(InputStream inputStream) throws IOException {
        File file = writeToTempFile(inputStream, "temp-", ".tar");
        try {
            // 2. 创建临时文件
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            CommandLine cmdLine = CommandLine.parse("docker load -i " + file.getAbsolutePath());

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(outputStream, errorStream, inputStream));

            executor.setExitValues(null);

            int exitCode;
            try {
                logger.debug("开始导入Docker镜像...");
                executor.execute(cmdLine);
            } catch (ExecuteException e) {
                exitCode = e.getExitValue();
                logger.warn("docker load 执行失败，退出码: {}", exitCode);
            }

            String output = outputStream.toString().trim();

            // 解析镜像名
            Pattern pattern = Pattern.compile("Loaded image:\\s*(.+)");
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                return matcher.group(1).trim(); // ✅ 提取镜像名
            }

            logger.warn("未能从输出中提取镜像名，原始输出: {}", output);
            return null;
        }finally {
            file.delete();
        }
    }

    public static File writeToTempFile(InputStream inputStream, String prefix, String suffix) throws IOException {
        // 创建临时文件
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

    public static String getLatestLogs(String containerId, int tailLines) throws IOException {
        String cmd;
        if (tailLines > 0) {
            cmd = String.format("docker logs --tail %d %s", tailLines, containerId);
        } else {
            cmd = "docker logs " + containerId;
        }
        ExecuteResult result = execute(cmd);
        if (!result.isSuccess()) {
            throw new IOException("获取日志失败: " + result.getError());
        }
        return result.getOutput();
    }

    public static DContainerInfo runContainerAndGetInfo(String imageName, List<Integer> gpuIndexes) throws IOException {
        if (imageName == null || imageName.trim().isEmpty()) {
            throw new IllegalArgumentException("镜像名不能为空");
        }

        // 自动生成唯一容器名称
        String containerName = "container-" + UUID.randomUUID().toString().substring(0, 8);

        // 构建 docker run 命令
        StringBuilder cmdBuilder = new StringBuilder("docker run -d --name ");
        cmdBuilder.append(containerName).append(" ");

        // 处理 GPU 参数
        if (gpuIndexes != null && !gpuIndexes.isEmpty()) {
            for (Integer index : gpuIndexes) {
                if (index == null || index < 0) {
                    throw new IllegalArgumentException("GPU索引不能为 null 或负数");
                }
            }
            String joinedIndexes = gpuIndexes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            cmdBuilder.append("--gpus \"device=").append(joinedIndexes).append("\" ");
        }

        cmdBuilder.append(imageName);

        String cmd = cmdBuilder.toString();
        ExecuteResult result = execute(cmd);

        if (!result.isSuccess()) {
            throw new IOException("容器启动失败: " + result.getError());
        }

        String containerId = result.getOutput().trim();

        // 校验返回的容器ID是否合理（一般是12~64位十六进制字符串）
        if (!containerId.matches("^[a-f0-9]{12,64}$")) {
            throw new IOException("容器启动成功但无法解析容器ID: " + containerId);
        }

        logger.info("成功启动容器: ID={}, Name={}, GPUs={}", containerId, containerName, gpuIndexes);
        return new DContainerInfo(containerName, containerId);
    }


}
