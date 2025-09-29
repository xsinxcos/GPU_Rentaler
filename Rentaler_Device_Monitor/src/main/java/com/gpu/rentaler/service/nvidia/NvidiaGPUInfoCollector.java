package com.gpu.rentaler.service.nvidia;


import com.gpu.rentaler.service.IGPUInfoCollector;
import com.gpu.rentaler.entity.GPUDeviceInfo;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class NvidiaGPUInfoCollector implements IGPUInfoCollector {
    private static final Logger log = LogManager.getLogger(NvidiaGPUInfoCollector.class);

    @Override
    public List<GPUDeviceInfo> getAllGPUInfo() {
        return getNVIDIAGPUInfo();
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
            log.info("获取NVIDIA GPU信息失败: {}", e.getMessage());
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
            gpu.setDeviceIndex(index);
            gpu.setBrand("NVIDIA");

            // 解析基本信息
            gpu.setModel(parts[1].trim());
            gpu.setMemoryTotal(Long.parseLong(parts[2].trim()));

            // 生成设备ID
            gpu.setDeviceId(generateDeviceId());

            // 根据型号推断架构和其他信息
            //inferNVIDIASpecs(gpu);

            // 设置状态为active（假设检测到的GPU都是活跃的）
            gpu.setStatus("active");

            return gpu;

        } catch (Exception e) {
            log.info("解析NVIDIA GPU信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 推断NVIDIA GPU规格信息
     */
    private void inferNVIDIASpecs(GPUDeviceInfo gpu) {
        // todo 后续完善，通过查表或其他方式获取更详细的规格信息
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
            log.info("备用NVIDIA GPU信息获取失败: {}", e.getMessage());
        }

        return gpuList;
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
     * 生成设备唯一标识符
     */
    private String generateDeviceId() {
        try {
            String xmlOutput = getNvidiaSmiXmlOutput();
            String deviceId = parseDeviceIdFromXml(xmlOutput);
            return deviceId;
        } catch (Exception e) {
            log.error("获取NVIDIA设备ID失败: {}", e.getMessage());
            return UUID.randomUUID().toString();
        }
    }

    private String getNvidiaSmiXmlOutput() throws IOException {
        CommandLine cmdLine = CommandLine.parse("nvidia-smi -q -x");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);

        return outputStream.toString();
    }

    private String parseDeviceIdFromXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // 禁用外部 DTD 校验，防止 FileNotFound 错误
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setValidating(false);
        factory.setNamespaceAware(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

        NodeList pciDeviceIdNodes = doc.getElementsByTagName("pci_device_id");
        if (pciDeviceIdNodes.getLength() > 0) {
            String raw = pciDeviceIdNodes.item(0).getTextContent();
            return raw;
        } else {
            return "Not Found";
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
                    gpu.setDeviceIndex(deviceIndex++);
                    gpu.setBrand(brand);
                    gpu.setModel(extractGPUName(line));
                    gpu.setDeviceId(generateDeviceId());
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
                gpu.setDeviceIndex(deviceIndex++);
                gpu.setBrand(brand);
                gpu.setModel(extractGPUNameFromLspci(line));
                gpu.setDeviceId(generateDeviceId());
                gpu.setStatus("active");

                gpuList.add(gpu);
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
