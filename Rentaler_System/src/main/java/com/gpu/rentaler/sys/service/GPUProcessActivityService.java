package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.model.GPUProcessActivity;
import com.gpu.rentaler.sys.repository.GPUProcessActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class GPUProcessActivityService {

    private GPUProcessActivityRepository gpuProcessActivityRepository;

    public GPUProcessActivityService(GPUProcessActivityRepository gpuProcessActivityRepository) {
        this.gpuProcessActivityRepository = gpuProcessActivityRepository;
    }

    public void saveActivity(String pid , String processName, String deviceId, Instant time) {
        GPUProcessActivity activity = new GPUProcessActivity(deviceId, Long.valueOf(pid), processName, time);
        gpuProcessActivityRepository.save(activity);
    }
}
