package com.gpu.rentaler.entity;

import java.time.Instant;

public record GPUUsage(String deviceId,             // GPU 唯一标识
                       double memoryUsedPercent,    // 显存占用百分比
                       double gpuUtilizationPercent,// GPU 利用率百分比
                       Instant time                 // 上报时间
) {
}
