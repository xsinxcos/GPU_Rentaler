package com.gpu.rentaler.service.nvidia;

import com.gpu.rentaler.entity.GPUUsage;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.service.GPUActivityFetcher;
import com.gpu.rentaler.utils.DockerExecutor;
import com.gpu.rentaler.utils.OSUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class NvidiaActivityFetcher implements GPUActivityFetcher {

    private static final Logger log = LogManager.getLogger(NvidiaActivityFetcher.class);
    private static OSUtils.OSType os = OSUtils.getOSType();

    public List<ProcessInfo> getGpuProcessList() {
        String command = "nvidia-smi --query-compute-apps=pid,process_name,gpu_uuid,used_memory --format=csv,noheader,nounits";
        if (OSUtils.OSType.WINDOWS.equals(os)) {
            command = "wsl " + command;
        }
        CommandLine cmdLine = CommandLine.parse(
            command
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            //log.info("获取NVIDIA GPU进程信息失败: {}", e.getMessage());
            return List.of();
        }
        String result = outputStream.toString().trim();
        return parseProcessList(result);
    }

    @Override
    public List<ProcessInfo> getGpuProcessInDockerContainer(String containerId) {
        CommandLine cmdLine = CommandLine.parse(
            "docker exec " + containerId + " nvidia-smi --query-compute-apps=pid,process_name,gpu_uuid,used_memory --format=csv,noheader,nounits"
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            //log.info("获取NVIDIA GPU进程信息失败: {} docker containerId :{}", e.getMessage() ,containerId);
            return List.of();
        }
        String result = outputStream.toString().trim();
        List<ProcessInfo> processInfos = parseProcessList(result);
        processInfos.forEach(item -> item.setContainerId(containerId));
        return processInfos;
    }

    @Override
    public List<GPUUsage> getGPUUSage() {
        String command = "nvidia-smi --query-gpu=index,uuid,utilization.gpu,memory.total,memory.used --format=csv,noheader,nounits";
        if (OSUtils.OSType.WINDOWS.equals(os)) {
            command = "wsl " + command;
        }

        CommandLine cmdLine = CommandLine.parse(command);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            log.warn("获取NVIDIA GPU使用信息失败: {}", e.getMessage());
            return List.of();
        }

        String result = outputStream.toString().trim();
        return parseGPUUsage(result);
    }

    private List<GPUUsage> parseGPUUsage(String rawOutput) {
        List<GPUUsage> list = new ArrayList<>();
        String[] lines = rawOutput.split("\\r?\\n");

        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 5) {
                try {
                    String deviceIndex = parts[0].trim();
                    String deviceId = parts[1].trim();
                    double gpuUtil = Double.parseDouble(parts[2].trim());
                    double memoryTotal = Double.parseDouble(parts[3].trim());
                    double memoryUsed = Double.parseDouble(parts[4].trim());
                    double memoryUsedPercent = memoryTotal == 0 ? 0 : (memoryUsed / memoryTotal) * 100;

                    GPUUsage usage = new GPUUsage(deviceId ,gpuUtil ,memoryUsedPercent , Instant.now());
                    list.add(usage);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return list;
    }

    private List<ProcessInfo> parseProcessList(String rawOutput) {
        List<ProcessInfo> list = new ArrayList<>();
        String[] lines = rawOutput.split("\\r?\\n");
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 4) {
                ProcessInfo info = new ProcessInfo();
                info.setPid(parts[0].trim());
                info.setName(parts[1].trim());
                info.setDeviceId(parts[2].trim());
                info.setUsedMemoryMB(parts[3].trim());
                list.add(info);
            }
        }
        return list;
    }

}
