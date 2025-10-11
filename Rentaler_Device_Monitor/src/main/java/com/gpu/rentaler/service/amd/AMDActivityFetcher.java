package com.gpu.rentaler.service.amd;

import com.gpu.rentaler.entity.GPUUsage;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.service.GPUActivityFetcher;
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
public class AMDActivityFetcher implements GPUActivityFetcher {
    private static final Logger log = LogManager.getLogger(AMDActivityFetcher.class);

    @Override
    public List<ProcessInfo> getGpuProcessList() {
        try {
            return getAMDProcesses();
        } catch (IOException e) {
            //log.info("获取AMD GPU进程信息失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ProcessInfo> getGpuProcessInDockerContainer(String containerId) {
        CommandLine cmdLine = CommandLine.parse("docker exec " + containerId + " rocm-smi --showprocs");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            return List.of();
        }

        String result = outputStream.toString().trim();
        List<ProcessInfo> processInfos = parseAMDProcessOutput(result);
        processInfos.forEach(item -> item.setContainerId(containerId));
        return processInfos;
    }

    @Override
    public List<GPUUsage> getGPUUSage() {
        CommandLine cmdLine = CommandLine.parse("rocm-smi --showuse"); // 显示利用率和显存占用
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            log.warn("获取AMD GPU使用信息失败: {}", e.getMessage());
            return List.of();
        }

        String result = outputStream.toString().trim();
        return parseAMDUsage(result);
    }

    private List<GPUUsage> parseAMDUsage(String rawOutput) {
        List<GPUUsage> list = new ArrayList<>();
        String[] lines = rawOutput.split("\\r?\\n");

        for (String line : lines) {
            // 样例行: "GPU[0]     45%    2048 MB / 8192 MB"
            line = line.trim();
            if (line.startsWith("GPU[")) {
                try {
                    String[] parts = line.split("\\s+");
                    String deviceId = parts[0].replace("GPU[", "").replace("]", "");
                    String gpuUtilStr = parts[1].replace("%", "");
                    String memUsedStr = parts[2]; // "2048"
                    String memTotalStr = parts[4]; // "8192"

                    double gpuUtil = Double.parseDouble(gpuUtilStr);
                    double memUsed = Double.parseDouble(memUsedStr);
                    double memTotal = Double.parseDouble(memTotalStr);
                    double memUsedPercent = memTotal == 0 ? 0 : (memUsed / memTotal) * 100;

                    GPUUsage usage = new GPUUsage(deviceId, gpuUtil, memUsedPercent, Instant.now());

                    list.add(usage);
                } catch (Exception ignored) {
                }
            }
        }
        return list;
    }

    public static List<ProcessInfo> getAMDProcesses() throws IOException {
        CommandLine cmdLine = CommandLine.parse("rocm-smi --showprocs");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);

        String result = outputStream.toString().trim();
        return parseAMDProcessOutput(result);
    }

    private static List<ProcessInfo> parseAMDProcessOutput(String output) {
        List<ProcessInfo> list = new ArrayList<>();
        String[] lines = output.split("\\r?\\n");
        for (String line : lines) {
            if (line.matches("\\s*\\d+\\s+\\d+\\s+.*")) {
                String[] parts = line.trim().split("\\s+", 3);
                ProcessInfo p = new ProcessInfo();
                p.setDeviceId(parts[0]);
                p.setPid(parts[1]);
                p.setName(parts[2]);
                list.add(p);
            }
        }
        return list;
    }
}
