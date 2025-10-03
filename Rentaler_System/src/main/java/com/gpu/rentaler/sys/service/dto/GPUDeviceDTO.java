package com.gpu.rentaler.sys.service.dto;

public record GPUDeviceDTO(
    String deviceId, Integer deviceIndex, String brand, String model,
    Long memoryTotal,
    String architecture,
    String memoryType,
    String status, Boolean isRentable, String hourlyRate, String totalRuntimeHours, String totalRevenue) {
}
