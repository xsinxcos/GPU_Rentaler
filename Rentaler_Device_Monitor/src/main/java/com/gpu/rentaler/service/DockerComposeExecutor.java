package com.gpu.rentaler.service;

import com.gpu.rentaler.entity.ContainerInfo;
import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerComposeExecutor {
    private final String composeFilePath;
    private final Map<String, String> environmentVariables;

    /**
     * 构造函数
     * @param composeFilePath docker-compose.yml文件路径
     */
    public DockerComposeExecutor(String composeFilePath) {
        this.composeFilePath = composeFilePath;
        this.environmentVariables = new HashMap<>();
        // 添加系统环境变量
        this.environmentVariables.putAll(System.getenv());
    }

    /**
     * 添加环境变量
     * @param key 环境变量键
     * @param value 环境变量值
     */
    public void addEnvironmentVariable(String key, String value) {
        environmentVariables.put(key, value);
    }

    public ContainerInfo getContainerInfo() throws IOException {
        ExecutionResult psResult = ps();
        if (!psResult.isSuccess()) {
            throw new RuntimeException("Failed to get container info: " + psResult.getError());
        }

        String output = psResult.getOutput();
        String containerId = null;
        String port = null;

        // 简单解析 ps 输出，假设格式类似：
        // Name                    Command               State               Ports
        // my_container   "/bin/bash"            Up                  0.0.0.0:2222->22/tcp
        String[] lines = output.split("\n");
        if (lines.length >= 2) {
            String[] parts = lines[1].split("\\s+");
            containerId = parts[0]; // 容器名
            for (String part : parts) {
                if (part.contains("->")) {
                    port = part.split("->")[0].replaceAll("0.0.0.0:", "");
                    break;
                }
            }
        }

        // SSH 默认信息（可从环境变量获取）
        String sshName = environmentVariables.getOrDefault("SSH_USER", "root");
        String sshPassword = environmentVariables.getOrDefault("SSH_PASSWORD", "root");

        return new ContainerInfo(containerId, port, sshName, sshPassword);
    }

    /**
     * 获取所有容器的ID列表
     * @return 容器ID列表
     * @throws IOException IO异常
     */
    public List<String> getAllContainerIds() throws IOException {
        ExecutionResult psResult = ps();
        if (!psResult.isSuccess()) {
            throw new RuntimeException("Failed to get container IDs: " + psResult.getError());
        }

        List<String> containerIds = new ArrayList<>();
        String output = psResult.getOutput();
        String[] lines = output.split("\n");

        // 跳过表头（第一行），从第二行开始解析
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            // 解析每一行，获取容器名称（第一列）
            String[] parts = line.split("\\s+");
            if (parts.length > 0) {
                String containerId = parts[0];
                containerIds.add(containerId);
            }
        }

        return containerIds;
    }

    /**
     * 获取所有容器的详细信息列表
     * @return 容器信息列表
     * @throws IOException IO异常
     */
    public List<ContainerInfo> getAllContainerInfos() throws IOException {
        ExecutionResult psResult = ps();
        if (!psResult.isSuccess()) {
            throw new RuntimeException("Failed to get container infos: " + psResult.getError());
        }

        List<ContainerInfo> containerInfos = new ArrayList<>();
        String output = psResult.getOutput();
        String[] lines = output.split("\n");

        // SSH 默认信息
        String sshName = environmentVariables.getOrDefault("SSH_USER", "root");
        String sshPassword = environmentVariables.getOrDefault("SSH_PASSWORD", "root");

        // 跳过表头，从第二行开始解析
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            if (parts.length > 0) {
                String containerId = parts[0];
                String port = null;

                // 查找端口映射信息
                for (String part : parts) {
                    if (part.contains("->")) {
                        port = part.split("->")[0].replaceAll("0.0.0.0:", "");
                        break;
                    }
                }

                containerInfos.add(new ContainerInfo(containerId, port, sshName, sshPassword));
            }
        }

        return containerInfos;
    }

    /**
     * 执行docker-compose命令
     * @param commands 命令参数列表，如["up", "-d"]
     * @return 命令执行结果
     * @throws ExecuteException 执行异常
     * @throws IOException IO异常
     */
    public ExecutionResult executeCommand(String... commands) throws ExecuteException, IOException {
        // 构建命令行
        CommandLine cmdLine = new CommandLine("docker-compose");
        // 指定compose文件
        cmdLine.addArgument("-f");
        cmdLine.addArgument("docker-compose.yml");
        // 添加命令参数
        cmdLine.addArguments(commands);

        // 捕获输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);

        // 配置执行器
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        // 设置工作目录为compose文件所在目录
        File composeFile = new File(composeFilePath);
        executor.setWorkingDirectory(composeFile.getParentFile());

        // 执行命令
        int exitValue  =0 ;
        try {
            exitValue = executor.execute(cmdLine, environmentVariables);
        } catch (Exception e) {
            System.out.println("Exit code: " + ((org.apache.commons.exec.ExecuteException) e).getExitValue());
            System.out.println("标准输出: " + outputStream.toString());
            System.out.println("错误输出: " + errorStream.toString());
            e.printStackTrace();
        }

        // 返回执行结果
        return new ExecutionResult(
            exitValue,
            outputStream.toString(),
            errorStream.toString()
        );
    }

    /**
     * 启动服务
     */
    public ExecutionResult up() throws ExecuteException, IOException {
        return executeCommand("up", "-d");
    }

    /**
     * 停止服务
     */
    public ExecutionResult down() throws ExecuteException, IOException {
        return executeCommand("down");
    }

    /**
     * 查看服务状态
     */
    public ExecutionResult ps() throws ExecuteException, IOException {
        return executeCommand("ps");
    }

    /**
     * 查看日志
     */
    public ExecutionResult logs() throws ExecuteException, IOException {
        return executeCommand("logs");
    }

    /**
     * 重启服务
     */
    public ExecutionResult restart() throws ExecuteException, IOException {
        return executeCommand("restart");
    }

    /**
     * 命令执行结果封装类
     */
    public static class ExecutionResult {
        private final int exitValue;
        private final String output;
        private final String error;

        public ExecutionResult(int exitValue, String output, String error) {
            this.exitValue = exitValue;
            this.output = output;
            this.error = error;
        }

        public int getExitValue() {
            return exitValue;
        }

        public String getOutput() {
            return output;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return exitValue == 0;
        }

        @Override
        public String toString() {
            return "Exit Value: " + exitValue + "\n" +
                "Output: " + output + "\n" +
                "Error: " + error;
        }
    }

    // 使用示例
    public static void main(String[] args) {
        try {
            // 指定docker-compose.yml文件路径
            String composePath = "docker/test2/docker-compose.yml";
            DockerComposeExecutor executor = new DockerComposeExecutor(composePath);

            // 添加环境变量（如SSH密码）
            executor.addEnvironmentVariable("SSH_PASSWORD", "your_secure_password");

            // 启动服务
            System.out.println("Starting services...");
            ExecutionResult result = executor.up();
            if (result.isSuccess()) {
                System.out.println("Services started successfully:");
                System.out.println(result.getOutput());
            } else {
                System.err.println("Failed to start services:");
                System.err.println(result.getError());
            }

            // 查看状态
            System.out.println("\nService status:");
            ExecutionResult statusResult = executor.ps();
            System.out.println(statusResult.getOutput());

            // 获取所有容器ID
            System.out.println("\nAll container IDs:");
            List<String> containerIds = executor.getAllContainerIds();
            for (String id : containerIds) {
                System.out.println("- " + id);
            }

            // 获取所有容器详细信息
            System.out.println("\nAll container infos:");
            List<ContainerInfo> containerInfos = executor.getAllContainerInfos();
            for (ContainerInfo info : containerInfos) {
                System.out.println("- Container: " + info.getContainerId() +
                    ", Port: " + info.getPort());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
