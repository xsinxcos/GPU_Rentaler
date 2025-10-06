package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.model.GPUProcessActivity;
import com.gpu.rentaler.sys.repository.GPUProcessActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GPUProcessActivityService {

    private GPUProcessActivityRepository gpuProcessActivityRepository;

    public GPUProcessActivityService(GPUProcessActivityRepository gpuProcessActivityRepository) {
        this.gpuProcessActivityRepository = gpuProcessActivityRepository;
    }

    public void saveActivity(String pid , String processName, String deviceId, Instant time ,Long duration ,String recordId) {
        GPUProcessActivity activity = new GPUProcessActivity(deviceId, Long.valueOf(pid), processName, time ,duration ,recordId);
        gpuProcessActivityRepository.save(activity);
    }

    public List<GPUProcessActivity> getAllAfterTime(String deviceId ,Instant time){
        return gpuProcessActivityRepository.findByDeviceIdAndTimeAfter(deviceId ,time);
    }
}
