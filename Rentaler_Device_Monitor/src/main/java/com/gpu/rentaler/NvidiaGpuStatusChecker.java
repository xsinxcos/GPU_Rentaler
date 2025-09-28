package com.gpu.rentaler;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class NvidiaGpuStatusChecker {

    public static void main(String[] args) {
        NvidiaGpuStatusChecker checker = new NvidiaGpuStatusChecker();

        System.out.println("=== 开始查询NVIDIA显卡状态 ===");
        checker.checkBasicGpuStatus();

        System.out.println("\n=== 开始查询NVIDIA显卡详细状态 ===");
        checker.checkDetailedGpuStatus();
    }

    /**
     * 检查基本的显卡状态信息
     */
    public void checkBasicGpuStatus() {
        String command = getCommand("nvidia-smi");

        try {
            CommandLine cmdLine = CommandLine.parse(command);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(streamHandler);

            System.out.println("正在查询基本显卡状态...");
            int exitCode = executor.execute(cmdLine);

            // 处理输出结果
            String output = outputStream.toString(getCharset());
            String error = errorStream.toString(getCharset());

            System.out.println("=== 基本显卡状态信息 ===");
            System.out.println(output);

            if (!error.isEmpty()) {
                System.err.println("=== 错误信息 ===");
                System.err.println(error);
            }

            System.out.println("基本状态查询完成，退出码: " + exitCode);

        } catch (Exception e) {
            System.err.println("基本状态查询失败: " + e.getMessage());
        }
    }

    /**
     * 检查详细的显卡状态信息
     */
    public void checkDetailedGpuStatus() {
        String detailedQuery = "nvidia-smi --query-gpu=name,memory.total,memory.used,temperature.gpu,power.draw --format=csv,noheader,nounits";
        String command = getCommand(detailedQuery);

        try {
            CommandLine cmdLine = CommandLine.parse(command);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(streamHandler);

            System.out.println("正在查询详细显卡状态...");
            int exitCode = executor.execute(cmdLine);

            String output = outputStream.toString(getCharset());
            String error = errorStream.toString(getCharset());

            System.out.println("=== 详细显卡信息 ===");
            System.out.println("名称, 总内存(MB), 已用内存(MB), 温度(°C), 功耗(W)");
            System.out.println(output);

            if (!error.isEmpty()) {
                System.err.println("=== 错误信息 ===");
                System.err.println(error);
            }

            System.out.println("详细状态查询完成，退出码: " + exitCode);

        } catch (Exception e) {
            System.err.println("详细状态查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据操作系统生成对应的命令
     */
    private String getCommand(String baseCommand) {
        if (isWindows()) {
            return "cmd.exe /c " + baseCommand;
        } else {
            return "sh -c '" + baseCommand + "'";
        }
    }

    /**
     * 根据操作系统获取对应的字符集
     */
    private String getCharset() {
        return isWindows() ? "GBK" : StandardCharsets.UTF_8.name();
    }

    /**
     * 判断当前操作系统是否为Windows
     */
    private boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }
}
