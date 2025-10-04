package com.gpu.rentaler.sys.service.dto;

public record BasicGPUDeviceDTO(
    int deviceIndex,
    String deviceId,
    String brand,
    String model,
    long memoryTotal
) {
}
