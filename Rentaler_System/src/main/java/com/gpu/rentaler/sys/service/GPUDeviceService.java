package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.repository.GPUDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GPUDeviceService {
    private final GPUDeviceRepository gpuDeviceRepository;

    public GPUDeviceService(GPUDeviceRepository gpuDeviceRepository) {
        this.gpuDeviceRepository = gpuDeviceRepository;
    }

    @Transactional
    public void saveOrUpdateGPUDeviceInfo(Long serverId, List<GPUDeviceInfo> gpuDeviceInfos) {
        List<GPUDevice> existingDevices = gpuDeviceRepository.findAllByServerId(serverId);
        Map<String , GPUDevice> existingDeviceMap = existingDevices.stream()
            .collect(Collectors.toMap(GPUDevice::getDeviceId, device -> device));
        List<GPUDeviceInfo> updateDevices = new ArrayList<>();
        List<GPUDeviceInfo> saveDevices = new ArrayList<>();
        for (GPUDeviceInfo gpuDeviceInfo : gpuDeviceInfos) {
            if(existingDeviceMap.containsKey(gpuDeviceInfo.getDeviceId())){
                updateDevices.add(gpuDeviceInfo);
            }else {
                saveDevices.add(gpuDeviceInfo);
            }
        }

        for (GPUDeviceInfo updateDevice : updateDevices) {
            GPUDevice existingDevice = existingDeviceMap.get(updateDevice.getDeviceId());
            existingDevice.setArchitecture(updateDevice.getArchitecture());
            existingDevice.setMemoryTotal(updateDevice.getMemoryTotal());
            existingDevice.setDeviceIndex(updateDevice.getDeviceIndex());
            existingDevice.setBrand(updateDevice.getBrand());
            existingDevice.setMemoryType(updateDevice.getMemoryType());
            existingDevice.setModel(updateDevice.getModel());
            existingDevice.setStatus(updateDevice.getStatus());
            gpuDeviceRepository.save(existingDevice);
        }

        for (GPUDeviceInfo saveDevice : saveDevices) {
            GPUDevice newDevice = new GPUDevice();
            newDevice.setServerId(serverId);
            newDevice.setDeviceId(saveDevice.getDeviceId());
            newDevice.setArchitecture(saveDevice.getArchitecture());
            newDevice.setMemoryTotal(saveDevice.getMemoryTotal());
            newDevice.setDeviceIndex(saveDevice.getDeviceIndex());
            newDevice.setBrand(saveDevice.getBrand());
            newDevice.setMemoryType(saveDevice.getMemoryType());
            newDevice.setModel(saveDevice.getModel());
            newDevice.setStatus(saveDevice.getStatus());
            gpuDeviceRepository.save(newDevice);
        }
    }

    public void changeStatusByServerId(Long serverId, String status) {
        List<GPUDevice> devices = gpuDeviceRepository.findAllByServerId(serverId);
        for (GPUDevice device : devices) {
            device.setStatus(status);
            gpuDeviceRepository.save(device);
        }
    }
}
