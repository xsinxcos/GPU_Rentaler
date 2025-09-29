package com.gpu.rentaler;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DockerLauncher {

    public static void main(String[] args) {
        String sshPassword = "123456";

        // 1. 检测宿主机 NVIDIA 驱动版本
        String driverVersion = getNvidiaDriverVersion();
        if (driverVersion == null) {
            System.err.println("未检测到 NVIDIA 驱动，请检查 GPU 驱动安装。");
            return;
        }
        System.out.println("检测到 NVIDIA 驱动版本：" + driverVersion);

        // 2. 根据驱动版本选择 CUDA 镜像
        String cudaImage = selectCudaImage(driverVersion);
        System.out.println("选择 CUDA 镜像：" + cudaImage);

        // 3. 设置环境变量，让 docker-compose 使用
        Map<String, String> env = new HashMap<>(System.getenv());
        env.put("SSH_PASSWORD", sshPassword);
        env.put("NVIDIA_IMAGE", cudaImage);

        // 4. 启动 N卡容器
        boolean nvidiaStarted = runDockerCompose("docker/nvidia/docker-compose.yml", env);
        boolean amdStarted = true; // 如果需要也可以动态启动 A卡

        if (nvidiaStarted && amdStarted) {
            System.out.println("GPU 容器启动成功！");
        } else {
            System.out.println("启动失败，请检查日志！");
        }
    }

    // 获取 NVIDIA 驱动版本
    private static String getNvidiaDriverVersion() {
        try {
            Process process = Runtime.getRuntime().exec(
                new String[]{"nvidia-smi", "--query-gpu=driver_version", "--format=csv,noheader"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();
            process.waitFor();
            return version != null ? version.trim() : null;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 根据驱动版本选择 CUDA 镜像
    private static String selectCudaImage(String driverVersion) {
        try {
            int major = Integer.parseInt(driverVersion.split("\\.")[0]);
            if (major >= 525) {
                return "nvidia/cuda:12.0.0-base-ubuntu22.04"; // 新驱动
            } else {
                return "nvidia/cuda:11.8.0-base-ubuntu22.04"; // 旧驱动
            }
        } catch (Exception e) {
            return "my/nvidia:cuda11.8";
        }
    }

    // 执行 docker-compose
    private static boolean runDockerCompose(String composeFile, Map<String, String> env) {
        try {
            String command = String.format("docker-compose -f %s up -d", composeFile);
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine, env);
            return exitValue == 0;
        } catch (ExecuteException e) {
            System.err.println("命令执行失败: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("IO异常: " + e.getMessage());
            return false;
        }
    }
}
