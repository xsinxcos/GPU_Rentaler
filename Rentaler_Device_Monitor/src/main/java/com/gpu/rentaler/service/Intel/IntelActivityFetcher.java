package com.gpu.rentaler.service.Intel;

import com.gpu.rentaler.entity.GPUUsage;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.service.GPUActivityFetcher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IntelActivityFetcher implements GPUActivityFetcher {
    @Override
    public List<ProcessInfo> getGpuProcessList() {
        // 不支持
        return List.of();
    }

    @Override
    public List<ProcessInfo> getGpuProcessInDockerContainer(String containerId) {
        return List.of();
    }

    @Override
    public List<GPUUsage> getGPUUSage() {
        return List.of();
    }
}
