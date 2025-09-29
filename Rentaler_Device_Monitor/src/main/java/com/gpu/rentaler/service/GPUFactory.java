package com.gpu.rentaler.service;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.service.Intel.IntelActivityFetcher;
import com.gpu.rentaler.service.Intel.InterGPUInfoCollector;
import com.gpu.rentaler.service.amd.AMDActivityFetcher;
import com.gpu.rentaler.service.amd.AMDGPUInfoCollector;
import com.gpu.rentaler.service.nvidia.NvidiaActivityFetcher;
import com.gpu.rentaler.service.nvidia.NvidiaGPUInfoCollector;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GPUFactory {

    @Resource
    private NvidiaGPUInfoCollector nvidiaGPUInfoCollector;

    @Resource
    private AMDGPUInfoCollector amdgpuInfoCollector;

    @Resource
    private InterGPUInfoCollector interGPUInfoCollector;

    @Resource
    private NvidiaActivityFetcher nvidiaActivityFetcher;

    @Resource
    private AMDActivityFetcher amdActivityFetcher;

    @Resource
    private IntelActivityFetcher intelActivityFetcher;

    public List<GPUDeviceInfo> getAllGPUInfo() {
        List<GPUDeviceInfo> all = new ArrayList<>();
        all.addAll(nvidiaGPUInfoCollector.getAllGPUInfo());
        all.addAll(amdgpuInfoCollector.getAllGPUInfo());
        all.addAll(interGPUInfoCollector.getAllGPUInfo());
        return all;
    }

    public List<ProcessInfo> getAllGPUActivityInfo() {
        List<ProcessInfo> all = new ArrayList<>();
        all.addAll(nvidiaActivityFetcher.getGpuProcessList());
        all.addAll(amdActivityFetcher.getGpuProcessList());
        all.addAll(intelActivityFetcher.getGpuProcessList());
        return all;
    }
}
