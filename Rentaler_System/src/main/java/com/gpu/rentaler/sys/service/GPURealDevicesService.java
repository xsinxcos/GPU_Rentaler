package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.constant.DeviceStatus;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.GPURealDevices;
import com.gpu.rentaler.sys.repository.GPURealDevicesRepository;
import com.gpu.rentaler.sys.service.dto.BasicGPUDeviceDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GPURealDevicesService {

    @Resource
    private GPURealDevicesRepository gpuRealDevicesRepository;

    @Transactional
    public List<GPURealDevices> saveOrUpdateGPUDeviceInfo(Long serverId, List<BasicGPUDeviceDTO> gpuDeviceInfos) {
        List<GPURealDevices> existingDevices = gpuRealDevicesRepository.findByServerId(serverId);
        Map<String, GPURealDevices> existingDeviceMap = existingDevices.stream()
            .collect(Collectors.toMap(GPURealDevices::getRealDeviceId, device -> device));
        List<BasicGPUDeviceDTO> updateDevices = new ArrayList<>();
        List<BasicGPUDeviceDTO> saveDevices = new ArrayList<>();
        for (BasicGPUDeviceDTO gpuDeviceInfo : gpuDeviceInfos) {
            if (existingDeviceMap.containsKey(gpuDeviceInfo.deviceId())) {
                updateDevices.add(gpuDeviceInfo);
            } else {
                saveDevices.add(gpuDeviceInfo);
            }
        }

        List<GPURealDevices> res = new ArrayList<>();

        for (BasicGPUDeviceDTO updateDevice : updateDevices) {
            GPURealDevices existingDevice = existingDeviceMap.get(updateDevice.deviceId());
            existingDevice.setServerId(serverId);
            existingDevice.setRealDeviceId(updateDevice.deviceId());
            existingDevice.setMemoryTotal(updateDevice.memoryTotal());
            existingDevice.setDeviceIndex(updateDevice.deviceIndex());
            existingDevice.setBrand(updateDevice.brand());
            existingDevice.setMemoryTotal(updateDevice.memoryTotal());
            existingDevice.setModel(updateDevice.model());
            existingDevice.setStatus(DeviceStatus.ONLINE);
            res.add(gpuRealDevicesRepository.save(existingDevice));
        }

        for (BasicGPUDeviceDTO saveDevice : saveDevices) {
            GPURealDevices newDevice = new GPURealDevices();
            newDevice.setServerId(serverId);
            newDevice.setRealDeviceId(saveDevice.deviceId());
            newDevice.setMemoryTotal(saveDevice.memoryTotal());
            newDevice.setDeviceIndex(saveDevice.deviceIndex());
            newDevice.setBrand(saveDevice.brand());
            newDevice.setMemoryTotal(saveDevice.memoryTotal());
            newDevice.setModel(saveDevice.model());
            newDevice.setStatus(DeviceStatus.ONLINE);
            newDevice.setStatus(DeviceStatus.ONLINE);
            res.add(gpuRealDevicesRepository.save(newDevice));
        }
        return res;
    }

    public List<GPURealDevices> getByDeviceIds(List<String> deviceIds) {
        return gpuRealDevicesRepository.findByDeviceIdIn(deviceIds);
    }

    public void notRentable(List<String> deviceIds) {
        gpuRealDevicesRepository.updateIsRentableByDeviceIdIn(false ,deviceIds);
    }

    public void canRentable(List<String> canRDeviceIds) {
        gpuRealDevicesRepository.updateIsRentableByDeviceIdIn(true ,canRDeviceIds);
    }

    public List<String> findCanRentable() {
        return gpuRealDevicesRepository
            .findByStatusAndIsRentable(DeviceStatus.ONLINE ,true)
            .stream()
            .map(GPURealDevices::getModel)
            .toList();
    }

    public List<String> findCantRentable() {
        return gpuRealDevicesRepository.findByStatusOrIsRentable(DeviceStatus.OFFLINE ,false)
            .stream()
            .map(GPURealDevices::getModel)
            .toList();
    }

    public Optional<GPURealDevices> lease(String model) {
        List<GPURealDevices> canRentable = gpuRealDevicesRepository.findByModelAndStatusAndIsRentable(model ,DeviceStatus.ONLINE ,true);
        if(canRentable.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(canRentable.getFirst());
    }

    public GPURealDevices getByDeviceId(String deviceId) {
        return gpuRealDevicesRepository.findByRealDeviceId(deviceId);
    }

    public void changeStatusByServerId(Long serverId, String status) {
        gpuRealDevicesRepository.updateStatusByServerId(status ,serverId);
    }
}
