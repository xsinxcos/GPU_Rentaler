package com.gpu.rentaler.service.nvidia;

import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.service.GPUActivityFetcher;
import org.apache.commons.exec.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class NvidiaActivityFetcher implements GPUActivityFetcher {

    private static final Logger log = LogManager.getLogger(NvidiaActivityFetcher.class);

    public List<ProcessInfo> getGpuProcessList() {
        CommandLine cmdLine = CommandLine.parse(
            "nvidia-smi --query-compute-apps=pid,process_name,gpu_uuid,used_memory --format=csv,noheader,nounits"
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            log.info("获取NVIDIA GPU进程信息失败: {}", e.getMessage());
            return List.of();
        }
        String result = outputStream.toString().trim();
        return parseProcessList(result);
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
                info.setGpuUuid(parts[2].trim());
                info.setUsedMemoryMB(parts[3].trim());
                list.add(info);
            }
        }
        return list;
    }
}
