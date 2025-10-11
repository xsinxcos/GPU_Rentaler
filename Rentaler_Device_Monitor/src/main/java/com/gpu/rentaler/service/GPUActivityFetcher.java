package com.gpu.rentaler.service;

import com.gpu.rentaler.entity.GPUUsage;
import com.gpu.rentaler.entity.ProcessInfo;

import java.util.List;

public interface GPUActivityFetcher {

    List<ProcessInfo> getGpuProcessList();

    List<ProcessInfo> getGpuProcessInDockerContainer(String containerId);

    List<GPUUsage> getGPUUSage();
}
