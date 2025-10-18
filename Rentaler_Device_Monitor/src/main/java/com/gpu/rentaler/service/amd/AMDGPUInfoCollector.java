package com.gpu.rentaler.service.amd;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.service.IGPUInfoCollector;
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
public class AMDGPUInfoCollector implements IGPUInfoCollector {
    private static final Logger log = LogManager.getLogger(AMDGPUInfoCollector.class);

    @Override
    public List<GPUDeviceInfo> getAllGPUInfo() {
        return getAMDGPUInfo();
    }

    /**
     * 获取AMD GPU信息
     */
    private List<GPUDeviceInfo> getAMDGPUInfo() {
        List<GPUDeviceInfo> gpuList = new ArrayList<>();

        try {
            // 尝试使用rocm-smi命令
            String rocmOutput = executeCommand("rocm-smi --showid --showproductname --showmeminfo vram");
            if (rocmOutput != null && !rocmOutput.trim().isEmpty()) {
                parseROCmOutput(rocmOutput, gpuList);
            }

            // 尝试其他方法
            if (gpuList.isEmpty()) {
                // Windows wmic
                String wmicOutput = executeCommand("wmic path win32_VideoController get name,AdapterRAM");
                if (wmicOutput != null && wmicOutput.contains("AMD")) {
                    parseWmicOutput(wmicOutput, gpuList, "AMD");
                }

                // Linux lspci
                String lspciOutput = executeCommand("lspci | grep -i amd");
                if (lspciOutput != null && !lspciOutput.trim().isEmpty()) {
                    parseLspciOutput(lspciOutput, gpuList, "AMD");
                }
            }

        } catch (Exception e) {
           log.info("获取AMD GPU信息失败: {}", e.getMessage());
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
                    gpu.setDeviceId(getAMDDeviceId());
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
                gpu.setDeviceId(getAMDDeviceId());
                gpu.setStatus("active");

                gpuList.add(gpu);
            }
        }
    }

    /**
     * 解析ROCm输出
     */
    private void parseROCmOutput(String output, List<GPUDeviceInfo> gpuList) {
        String[] lines = output.split("\n");
        int deviceIndex = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.contains("GPU") && line.contains("Name")) {
                GPUDeviceInfo gpu = new GPUDeviceInfo();
                gpu.setDeviceIndex(deviceIndex++);
                gpu.setBrand("AMD");

                // 提取GPU名称
                Pattern pattern = Pattern.compile("Name:\\s*(.+)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    gpu.setModel(matcher.group(1).trim());
                }

                try {
                    gpu.setDeviceId(getAMDDeviceId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                gpu.setStatus("active");

                gpuList.add(gpu);
            }
        }
    }

    public static String getAMDDeviceId() throws IOException {
        CommandLine cmdLine = CommandLine.parse("rocm-smi --showid");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);

        String output = outputStream.toString();

        // 匹配 GPU ID: 0xXXXX
        Pattern pattern = Pattern.compile("GPU ID:\\s*0x([0-9a-fA-F]+)");
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            return matcher.group(1);
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
