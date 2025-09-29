package com.gpu.rentaler.service;

import com.gpu.rentaler.entity.GPUDeviceInfo;

import java.util.List;

public interface IGPUInfoCollector {
    List<GPUDeviceInfo> getAllGPUInfo();
}
