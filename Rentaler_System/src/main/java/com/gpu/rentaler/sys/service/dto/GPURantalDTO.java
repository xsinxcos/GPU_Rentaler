package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.GPUDevice;

import java.time.Instant;

public record GPURantalDTO(
    GPUDevice gpuDevice,
    Long userId,
    String gpuDeviceId,
    Instant startTime,
    Instant endTime,
    String plannedDurationHours,
    String actualDurationHours,
    String hourlyRate,
    String status,
    String sshIp,
    String sshPort,
    String sshUsername,
    String sshPassword
) {
}
