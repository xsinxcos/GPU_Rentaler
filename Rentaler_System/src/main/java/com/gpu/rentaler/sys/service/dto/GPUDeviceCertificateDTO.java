package com.gpu.rentaler.sys.service.dto;

public record GPUDeviceCertificateDTO (
    String deviceId,
    String sshUsername,
    String sshPassword,
    String sshHost
){
}
