package com.gpu.rentaler.sys.service.dto;

public record RentableGPUDeviceDTO (String deviceId, String brand, String model,
                                    Long memoryTotal,
                                    String architecture,
                                    String memoryType,
                                    Boolean isRentable, String hourlyRate){
}
