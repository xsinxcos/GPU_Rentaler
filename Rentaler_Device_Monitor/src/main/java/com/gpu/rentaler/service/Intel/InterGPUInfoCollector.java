package com.gpu.rentaler.service.Intel;

import com.gpu.rentaler.service.IGPUInfoCollector;
import com.gpu.rentaler.entity.GPUDeviceInfo;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InterGPUInfoCollector implements IGPUInfoCollector {

    private static final Logger log = LogManager.getLogger(InterGPUInfoCollector.class);

    @Override
    public List<GPUDeviceInfo> getAllGPUInfo() {
        return getIntelGPUInfo();
    }

    /**
     * 获取Intel GPU信息
     */
    private List<GPUDeviceInfo> getIntelGPUInfo() {
        List<GPUDeviceInfo> gpuList = new ArrayList<>();

        try {
            // Windows wmic
            String wmicOutput = executeCommand("wmic path win32_VideoController get name,AdapterRAM");
            if (wmicOutput != null && wmicOutput.contains("Intel")) {
                parseWmicOutput(wmicOutput, gpuList, "INTEL");
            }

            // Linux lspci
            String lspciOutput = executeCommand("lspci | grep -i intel.*graphics");
            if (lspciOutput != null && !lspciOutput.trim().isEmpty()) {
                parseLspciOutput(lspciOutput, gpuList, "INTEL");
            }

        } catch (Exception e) {
            log.info("获取Intel GPU信息失败: {}", e.getMessage());
        }

        return gpuList;
    }


    /**
     * 解析wmic输出
     */
    private void parseWmicOutput(String output, List<GPUDeviceInfo> gpuList, String brand) throws IOException {
        String[] lines = output.split("\n");
        int deviceIndex = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.contains(brand) && !line.equals("Name")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    GPUDeviceInfo gpu = new GPUDeviceInfo();
                    gpu.setDeviceIndex(deviceIndex++);
                    gpu.setBrand(brand);
                    gpu.setModel(extractGPUName(line));
                    gpu.setDeviceId(getIntelDeviceId());
                    gpu.setStatus("active");

                    // 尝试解析显存大小
                    try {
                        String memStr = parts[parts.length - 1];
                        long memory = Long.parseLong(memStr) / (1024 * 1024); // 转换为MB
                        gpu.setMemoryTotal(memory);
                    } catch (NumberFormatException e) {
                        // 忽略解析错误
                    }

                    gpuList.add(gpu);
                }
            }
        }
    }

    /**
     * 从lspci输出中提取GPU名称
     */
    private String extractGPUNameFromLspci(String line) {
        // 提取冒号后的部分作为GPU名称
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1 && colonIndex < line.length() - 1) {
            return line.substring(colonIndex + 1).trim();
        }
        return "Unknown GPU";
    }

    /**
     * 解析lspci输出
     */
    private void parseLspciOutput(String output, List<GPUDeviceInfo> gpuList, String brand) throws IOException {
        String[] lines = output.split("\n");
        int deviceIndex = 0;

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                GPUDeviceInfo gpu = new GPUDeviceInfo();
                gpu.setDeviceIndex(deviceIndex++);
                gpu.setBrand(brand);
                gpu.setModel(extractGPUNameFromLspci(line));
                gpu.setDeviceId(getIntelDeviceId());
                gpu.setStatus("active");

                gpuList.add(gpu);
            }
        }
    }

    public static String getIntelDeviceId() throws IOException {
        CommandLine cmdLine = CommandLine.parse("lspci -nn");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);

        String output = outputStream.toString();

        // 查找 Intel 显卡的 Device ID
        Pattern pattern = Pattern.compile("Intel.*\\[(8086:[0-9a-fA-F]{4})\\]");
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            return matcher.group(1); // e.g. "8086:3e92"
        }

        return "Not Found";
    }

    /**
     * 执行系统命令
     */
    private String executeCommand(String command) {
        try {
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            executor.setStreamHandler(streamHandler);
            executor.setExitValue(0);

            int exitValue = executor.execute(cmdLine);
            return outputStream.toString();

        } catch (Exception e) {
            log.info("执行命令失败: {} - {}", command, e.getMessage());
            return null;
        }
    }

    /**
     * 从字符串中提取GPU名称
     */
    private String extractGPUName(String line) {
        // 简单的名称提取逻辑
        String[] parts = line.split("\\s+");
        StringBuilder name = new StringBuilder();

        for (String part : parts) {
            if (part.matches(".*[0-9].*") || part.toUpperCase().contains("RTX") ||
                part.toUpperCase().contains("GTX") || part.toUpperCase().contains("RX")) {
                name.append(part).append(" ");
            }
        }

        return name.toString().trim();
    }
}
