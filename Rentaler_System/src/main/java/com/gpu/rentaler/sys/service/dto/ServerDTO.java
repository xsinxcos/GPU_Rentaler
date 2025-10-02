package com.gpu.rentaler.sys.service.dto;

public record ServerDTO(
    Long id,
                        String hostname,

                        String ipAddress,

                        String location,

                        String cpuModel,

                        Integer cpuCores,

                        Integer ramTotalGb,

                        Integer storageTotalGb,

                        Integer gpuSlots,

                        String status,String datacenter, String region) {
}
