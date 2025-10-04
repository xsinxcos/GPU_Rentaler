package com.gpu.rentaler.sys.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record GPUTaskDTO (
    Long id ,Long userId,
                          String deviceId,
                          Instant startTime,
                          Instant endTime,
                          BigDecimal actualDurationHours,
                          BigDecimal hourlyRate,
                          BigDecimal totalCost,
                          String status){
}
