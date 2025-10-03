package com.gpu.rentaler.utils;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    public static void main(String[] args) {
        try {
            // 检查Docker是否可用
            if (!isDockerAvailable()) {
                System.out.println("Docker不可用，请检查Docker是否已安装并启动");
                return;
            }

            // 列出所有容器
            ExecuteResult result = listContainers(true);
            System.out.println("所有容器列表:");
            System.out.println(result.getOutput());

            // 获取所有容器ID
            java.util.List<String> containerIds = getAllContainerIds(true);
            System.out.println("\n所有容器ID:");
            for (String id : containerIds) {
                System.out.println("  - " + id);
            }

            // 运行一个Nginx容器
            Map<String, String> ports = new HashMap<>();
            ports.put("8080", "80");

            Map<String, String> env = new HashMap<>();
            env.put("ENV", "production");

            result = runContainer("nginx:latest", "my-nginx", ports, env);
            if (result.isSuccess()) {
                System.out.println("容器创建成功: " + result.getOutput());
            } else {
                System.out.println("容器创建失败: " + result.getError());
            }

        } catch (IOException e) {
            logger.error("执行Docker命令失败", e);
        }
    }

    /**
     * 根据PID获取进程名称
     * @param containerId 容器ID
     * @param pid 进程ID
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
     * @param inputStream 镜像文件输入流（通常是tar文件）
     * @param timeoutSeconds 超时时间（秒）
     * @return 执行结果
     * @throws IOException 加载失败时抛出
     */
    public static ExecuteResult loadImageFromInputStream(InputStream inputStream, int timeoutSeconds)
        throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        CommandLine cmdLine = CommandLine.parse("docker load");

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler(outputStream, errorStream, inputStream));

        // 设置超时
        ExecuteWatchdog watchdog = new ExecuteWatchdog(TimeUnit.SECONDS.toMillis(timeoutSeconds));
        executor.setWatchdog(watchdog);

        // 允许非0退出码
        executor.setExitValues(null);

        int exitCode;
        try {
            logger.debug("开始导入Docker镜像...");
            exitCode = executor.execute(cmdLine);
        } catch (ExecuteException e) {
            exitCode = e.getExitValue();
            logger.warn("docker load 执行失败，退出码: {}", exitCode);
        }

        String output = outputStream.toString().trim();
        String error = errorStream.toString().trim();

        if (watchdog.killedProcess()) {
            error = "导入超时 (" + timeoutSeconds + "秒)";
            logger.error(error);
        }

        return new ExecuteResult(exitCode, output, error);
    }
}
