package com.gpu.rentaler.service.amd;

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
            log.info("获取AMD GPU进程信息失败: " + e.getMessage());
            return List.of();
        }
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
                p.setGpuUuid(parts[0]);
                p.setPid(parts[1]);
                p.setName(parts[2]);
                list.add(p);
            }
        }
        return list;
    }
}
