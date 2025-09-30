package com.gpu.rentaler.sys.service.dto;

public record ServerDTO(String hostname,

                        String ipAddress,

                        String location,

                        String cpuModel,

                        Integer cpuCores,

                        Integer ramTotalGb,

                        Integer storageTotalGb,

                        Integer gpuSlots,

                        String status,

                        String loadAverage,

                        String cpuUsage,


                        String memoryUsage,


                        String diskUsage,

                        String bandwidthMbps,

                        String atacenter,
                        String region) {
}
