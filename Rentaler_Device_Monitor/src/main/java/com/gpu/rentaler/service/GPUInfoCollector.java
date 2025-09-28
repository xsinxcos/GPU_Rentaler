package com.gpu.rentaler.service;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import jakarta.annotation.PostConstruct;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GPUInfoCollector {

    private static final Long DEFAULT_SERVER_ID = 1L;

    private static List<GPUDeviceInfo> gpuList = null;

    @PostConstruct
    void init(){
        gpuList = new ArrayList<>();
        try {
            // 首先尝试NVIDIA GPU
            List<GPUDeviceInfo> nvidiaGpus = getNVIDIAGPUInfo();
            gpuList.addAll(nvidiaGpus);

            // 然后尝试AMD GPU
            List<GPUDeviceInfo> amdGpus = getAMDGPUInfo();
            gpuList.addAll(amdGpus);

            // 最后尝试Intel GPU
            List<GPUDeviceInfo> intelGpus = getIntelGPUInfo();
            gpuList.addAll(intelGpus);

        } catch (Exception e) {
            System.err.println("获取GPU信息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取所有GPU设备信息
     */
    public List<GPUDeviceInfo> getAllGPUInfo() {
       return gpuList;
    }

    /**
     * 获取NVIDIA GPU信息
     */
    private List<GPUDeviceInfo> getNVIDIAGPUInfo() {
        List<GPUDeviceInfo> gpuList = new ArrayList<>();

        try {
            // 使用nvidia-smi命令获取GPU信息
            String nvidiaOutput = executeCommand("nvidia-smi --query-gpu=index,name,memory.total,driver_version,temperature.gpu,power.draw,clocks.gr,clocks.mem,memory.used,memory.free,utilization.gpu --format=csv,noheader,nounits");

            if (nvidiaOutput != null && !nvidiaOutput.trim().isEmpty()) {
                String[] lines = nvidiaOutput.split("\n");

                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (!line.isEmpty()) {
                        GPUDeviceInfo gpu = parseNVIDIAGPULine(line, i);
                        if (gpu != null) {
                            gpuList.add(gpu);
                        }
                    }
                }
            }

            // 如果nvidia-smi不可用，尝试其他方法
            if (gpuList.isEmpty()) {
                gpuList.addAll(getNVIDIAGPUInfoAlternative());
            }

        } catch (Exception e) {
            System.err.println("获取NVIDIA GPU信息失败: " + e.getMessage());
        }

        return gpuList;
    }

    /**
     * 解析NVIDIA GPU信息行
     */
    private GPUDeviceInfo parseNVIDIAGPULine(String line, int index) {
        try {
            String[] parts = line.split(",");
            if (parts.length < 4) return null;

            GPUDeviceInfo gpu = new GPUDeviceInfo();
            gpu.setServerId(DEFAULT_SERVER_ID);
            gpu.setDeviceIndex(index);
            gpu.setBrand("NVIDIA");

            // 解析基本信息
            gpu.setModel(parts[1].trim());
            gpu.setMemoryTotal(Long.parseLong(parts[2].trim()));

            // 生成设备ID
            gpu.setDeviceId(generateDeviceId("NVIDIA", gpu.getModel(), index));

            // 根据型号推断架构和其他信息
            inferNVIDIASpecs(gpu);

            // 设置状态为active（假设检测到的GPU都是活跃的）
            gpu.setStatus("active");

            return gpu;

        } catch (Exception e) {
            System.err.println("解析NVIDIA GPU信息失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 推断NVIDIA GPU规格信息
     */
    private void inferNVIDIASpecs(GPUDeviceInfo gpu) {
        String model = gpu.getModel().toUpperCase();

        // RTX 40系列
        if (model.contains("RTX 4090")) {
            gpu.setArchitecture("Ada Lovelace");
            gpu.setCudaCores(16384);
            gpu.setTensorCores(512);
            gpu.setMemoryType("GDDR6X");
            gpu.setBaseClock(2230);
            gpu.setBoostClock(2520);
            gpu.setPowerLimit(450);
        } else if (model.contains("RTX 4080")) {
            gpu.setArchitecture("Ada Lovelace");
            gpu.setCudaCores(9728);
            gpu.setTensorCores(304);
            gpu.setMemoryType("GDDR6X");
            gpu.setBaseClock(2205);
            gpu.setBoostClock(2505);
            gpu.setPowerLimit(320);
        } else if (model.contains("RTX 4070")) {
            gpu.setArchitecture("Ada Lovelace");
            gpu.setCudaCores(5888);
            gpu.setTensorCores(184);
            gpu.setMemoryType("GDDR6X");
            gpu.setBaseClock(1920);
            gpu.setBoostClock(2475);
            gpu.setPowerLimit(200);
        }
        // RTX 30系列
        else if (model.contains("RTX 3090")) {
            gpu.setArchitecture("Ampere");
            gpu.setCudaCores(10496);
            gpu.setTensorCores(328);
            gpu.setMemoryType("GDDR6X");
            gpu.setBaseClock(1395);
            gpu.setBoostClock(1695);
            gpu.setPowerLimit(350);
        } else if (model.contains("RTX 3080")) {
            gpu.setArchitecture("Ampere");
            gpu.setCudaCores(8704);
            gpu.setTensorCores(272);
            gpu.setMemoryType("GDDR6X");
            gpu.setBaseClock(1440);
            gpu.setBoostClock(1710);
            gpu.setPowerLimit(320);
        }
        // 专业卡系列
        else if (model.contains("A100")) {
            gpu.setArchitecture("Ampere");
            gpu.setCudaCores(6912);
            gpu.setTensorCores(432);
            gpu.setMemoryType("HBM2e");
            gpu.setBaseClock(765);
            gpu.setBoostClock(1410);
            gpu.setPowerLimit(400);
        } else if (model.contains("V100")) {
            gpu.setArchitecture("Volta");
            gpu.setCudaCores(5120);
            gpu.setTensorCores(640);
            gpu.setMemoryType("HBM2");
            gpu.setBaseClock(1245);
            gpu.setBoostClock(1380);
            gpu.setPowerLimit(300);
        }
        // 默认值
        else {
            gpu.setArchitecture("Unknown");
            gpu.setMemoryType("GDDR6");
        }
    }

    /**
     * 备用NVIDIA GPU信息获取方法
     */
    private List<GPUDeviceInfo> getNVIDIAGPUInfoAlternative() {
        List<GPUDeviceInfo> gpuList = new ArrayList<>();

        try {
            // 尝试使用wmic命令（Windows）
            String wmicOutput = executeCommand("wmic path win32_VideoController get name,AdapterRAM");
            if (wmicOutput != null && wmicOutput.contains("NVIDIA")) {
                // 解析wmic输出
                parseWmicOutput(wmicOutput, gpuList, "NVIDIA");
            }

            // 尝试lspci命令（Linux）
            String lspciOutput = executeCommand("lspci | grep -i nvidia");
            if (lspciOutput != null && !lspciOutput.trim().isEmpty()) {
                parseLspciOutput(lspciOutput, gpuList, "NVIDIA");
            }

        } catch (Exception e) {
            System.err.println("备用NVIDIA GPU信息获取失败: " + e.getMessage());
        }

        return gpuList;
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
            System.err.println("获取AMD GPU信息失败: " + e.getMessage());
        }

        return gpuList;
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
            System.err.println("获取Intel GPU信息失败: " + e.getMessage());
        }

        return gpuList;
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
                gpu.setServerId(DEFAULT_SERVER_ID);
                gpu.setDeviceIndex(deviceIndex++);
                gpu.setBrand("AMD");

                // 提取GPU名称
                Pattern pattern = Pattern.compile("Name:\\s*(.+)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    gpu.setModel(matcher.group(1).trim());
                }

                gpu.setDeviceId(generateDeviceId("AMD", gpu.getModel(), gpu.getDeviceIndex()));
                inferAMDSpecs(gpu);
                gpu.setStatus("active");

                gpuList.add(gpu);
            }
        }
    }

    /**
     * 推断AMD GPU规格信息
     */
    private void inferAMDSpecs(GPUDeviceInfo gpu) {
        String model = gpu.getModel().toUpperCase();

        if (model.contains("RX 7900")) {
            gpu.setArchitecture("RDNA 3");
            gpu.setMemoryType("GDDR6");
            gpu.setPowerLimit(355);
        } else if (model.contains("RX 6900")) {
            gpu.setArchitecture("RDNA 2");
            gpu.setMemoryType("GDDR6");
            gpu.setPowerLimit(300);
        } else if (model.contains("RX 6800")) {
            gpu.setArchitecture("RDNA 2");
            gpu.setMemoryType("GDDR6");
            gpu.setPowerLimit(250);
        } else {
            gpu.setArchitecture("Unknown");
            gpu.setMemoryType("GDDR6");
        }
    }

    /**
     * 解析wmic输出
     */
    private void parseWmicOutput(String output, List<GPUDeviceInfo> gpuList, String brand) {
        String[] lines = output.split("\n");
        int deviceIndex = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.contains(brand) && !line.equals("Name")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    GPUDeviceInfo gpu = new GPUDeviceInfo();
                    gpu.setServerId(DEFAULT_SERVER_ID);
                    gpu.setDeviceIndex(deviceIndex++);
                    gpu.setBrand(brand);
                    gpu.setModel(extractGPUName(line));
                    gpu.setDeviceId(generateDeviceId(brand, gpu.getModel(), gpu.getDeviceIndex()));
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
     * 解析lspci输出
     */
    private void parseLspciOutput(String output, List<GPUDeviceInfo> gpuList, String brand) {
        String[] lines = output.split("\n");
        int deviceIndex = 0;

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                GPUDeviceInfo gpu = new GPUDeviceInfo();
                gpu.setServerId(DEFAULT_SERVER_ID);
                gpu.setDeviceIndex(deviceIndex++);
                gpu.setBrand(brand);
                gpu.setModel(extractGPUNameFromLspci(line));
                gpu.setDeviceId(generateDeviceId(brand, gpu.getModel(), gpu.getDeviceIndex()));
                gpu.setStatus("active");

                gpuList.add(gpu);
            }
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
     * 生成设备唯一标识符
     */
    private String generateDeviceId(String brand, String model, int deviceIndex) {
        try {
            String input = brand + "-" + model + "-" + deviceIndex + "-" + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.substring(0, 32); // 返回前32个字符
        } catch (NoSuchAlgorithmException e) {
            // 如果SHA-256不可用，使用简单的ID生成
            return brand.toLowerCase() + "_" + model.replaceAll("\\s+", "_").toLowerCase() + "_" + deviceIndex;
        }
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
            System.err.println("执行命令失败: " + command + " - " + e.getMessage());
            return null;
        }
    }
}
