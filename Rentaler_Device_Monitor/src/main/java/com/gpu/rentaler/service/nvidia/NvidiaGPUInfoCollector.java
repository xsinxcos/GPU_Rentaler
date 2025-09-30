package com.gpu.rentaler.service.nvidia;


import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.service.IGPUInfoCollector;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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
            gpu.setDeviceId(generateDeviceId(index));

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
     * 获取所有 GPU 的 UUID 列表
     */
    private List<String> getAllGpuUuids() {
        List<String> uuids = new ArrayList<>();

        try {
            String uuidOutput = executeCommand("nvidia-smi --query-gpu=uuid --format=csv,noheader,nounits");
            if (uuidOutput != null && !uuidOutput.trim().isEmpty()) {
                String[] lines = uuidOutput.split("\n");
                for (String line : lines) {
                    String uuid = line.trim();
                    if (!uuid.isEmpty()) {
                        uuids.add(uuid);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取 GPU UUID 列表失败: {}", e.getMessage());
        }

        return uuids;
    }

    /**
     * 获取指定索引 GPU 的设备唯一标识码
     */
    private String generateDeviceId(int gpuIndex) {
        try {
            // 尝试获取指定 GPU 的 UUID
            String command = String.format("nvidia-smi -i %d --query-gpu=uuid --format=csv,noheader,nounits", gpuIndex);
            String uuidOutput = executeCommand(command);

            if (uuidOutput != null && !uuidOutput.trim().isEmpty()) {
                return uuidOutput.trim();
            }

        } catch (Exception e) {
            log.warn("获取 GPU {} 的 UUID 失败: {}", gpuIndex, e.getMessage());
        }

        // 如果无法获取真实 UUID，生成一个基于索引的 UUID
        return "GPU-" + gpuIndex + "-" + java.util.UUID.randomUUID();
    }
}
