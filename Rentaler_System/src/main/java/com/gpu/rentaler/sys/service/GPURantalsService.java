package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.model.GPURantals;
import com.gpu.rentaler.sys.repository.GPURantalsRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class GPURantalsService {
    @Resource
    private GPURantalsRepository gpuRantalsRepository;


    // 方法定义处修改参数名
    public GPURantals saveGPURental(String deviceId, Long userId, Instant rentalStartTime,
                              BigDecimal hourlyRate, String rentalStatus, String containerId,
                              String sshHost, String sshUsername, String sshPassword) {
        GPURantals gpuRental = new GPURantals();
        gpuRental.setDeviceId(deviceId);
        gpuRental.setUserId(userId);
        gpuRental.setStartTime(rentalStartTime);
        gpuRental.setHourlyRate(hourlyRate);
        gpuRental.setStatus(rentalStatus);
        gpuRental.setContainerId(containerId);
        gpuRental.setSshHost(sshHost);
        gpuRental.setSshUsername(sshUsername);
        gpuRental.setSshPassword(sshPassword);

        // 持久化操作
        return gpuRantalsRepository.save(gpuRental);
    }

}
