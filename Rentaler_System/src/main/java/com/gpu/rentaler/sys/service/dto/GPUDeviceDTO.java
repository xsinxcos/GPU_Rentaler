package com.gpu.rentaler.sys.service.dto;

import java.math.BigDecimal;

public record GPUDeviceDTO(
    String deviceId, Integer deviceIndex, String brand, String model,
    Long memoryTotal,
    String architecture,
    String memoryType,
    String status, Boolean isRentable, String hourlyRate, String totalRuntimeHours, BigDecimal totalRevenue) {
}
