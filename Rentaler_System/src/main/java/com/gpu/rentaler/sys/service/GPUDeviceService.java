package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.sys.constant.DeviceStatus;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.repository.GPUDeviceRepository;
import com.gpu.rentaler.sys.repository.GPURantalsRepository;
import com.gpu.rentaler.sys.service.dto.GPUDeviceDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.RentableGPUDeviceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GPUDeviceService {
    private final GPUDeviceRepository gpuDeviceRepository;

    private final GPURantalsRepository gpuRantalsRepository;

    public GPUDeviceService(GPUDeviceRepository gpuDeviceRepository, GPURantalsRepository gpuRantalsRepository) {
        this.gpuDeviceRepository = gpuDeviceRepository;
        this.gpuRantalsRepository = gpuRantalsRepository;
    }

    @Transactional
    public void saveOrUpdateGPUDeviceInfo(Long serverId, List<GPUDeviceInfo> gpuDeviceInfos) {
        List<GPUDevice> existingDevices = gpuDeviceRepository.findAllByServerId(serverId);
        Map<String, GPUDevice> existingDeviceMap = existingDevices.stream()
            .collect(Collectors.toMap(GPUDevice::getDeviceId, device -> device));
        List<GPUDeviceInfo> updateDevices = new ArrayList<>();
        List<GPUDeviceInfo> saveDevices = new ArrayList<>();
        for (GPUDeviceInfo gpuDeviceInfo : gpuDeviceInfos) {
            if (existingDeviceMap.containsKey(gpuDeviceInfo.getDeviceId())) {
                updateDevices.add(gpuDeviceInfo);
            } else {
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
            existingDevice.setStatus(DeviceStatus.ONLINE);
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
            newDevice.setStatus(DeviceStatus.ONLINE);
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

    public GPUDevice getByDeviceId(String deviceId) {
        return gpuDeviceRepository.findByDeviceId(deviceId).getFirst();
    }


    public Optional<GPUDevice> lease(String deviceId){
        synchronized (this){
            GPUDevice device = getByDeviceId(deviceId);
            if(device.getIsRentable()){
                device.setIsRentable(false);
                //gpuDeviceRepository.save(device);
                return Optional.of(device);
            }
        }
        return Optional.empty();
    }

    public List<GPUDevice> getById(List<Long> ids) {
        return gpuDeviceRepository.findAllById(ids);
    }

    public void deleteByDeviceId(String deviceId) {
        gpuDeviceRepository.deleteByDeviceId(deviceId);
    }

    public PageDTO<GPUDeviceDTO> findGPUDevices(Pageable pageable, String status, Long serverId) {
        Page<GPUDevice> gpuDevices = gpuDeviceRepository.findGPUDevices(serverId, status, pageable);
        List<GPUDeviceDTO> dtos = gpuDevices.get().map(gp -> new GPUDeviceDTO(
            gp.getDeviceId(), gp.getDeviceIndex(), gp.getBrand(), gp.getModel(), gp.getMemoryTotal(),
            gp.getArchitecture(), gp.getMemoryType(), gp.getStatus(), gp.getIsRentable(), gp.getHourlyRate().toPlainString(),
            gp.getTotalRuntimeHours().toPlainString(), gp.getTotalRevenue().toPlainString()
        )).toList();
        return new PageDTO<>(dtos, gpuDevices.getTotalElements());
    }

    public PageDTO<RentableGPUDeviceDTO> findRentableGPUCanDevice(Pageable pageable) {
        Page<GPUDevice> rentable = gpuDeviceRepository.findRentableGPUCanDevice(DeviceStatus.ONLINE, true, pageable);
        List<RentableGPUDeviceDTO> dtos = rentable.get().map(item -> new RentableGPUDeviceDTO(
            item.getDeviceId(), item.getBrand(), item.getModel(), item.getMemoryTotal(), item.getArchitecture(), item.getMemoryType(),
            item.getIsRentable(), item.getHourlyRate().toPlainString()
        )).toList();
        return new PageDTO<>(dtos, rentable.getTotalElements());
    }

    public void updateGPUDeviceByDeviceId(String deviceId, Integer deviceIndex, String brand, String model,
                                          Long memoryTotal,
                                          String architecture,
                                          String memoryType,
                                          String status, Boolean isRentable, String hourlyRate, String totalRuntimeHours, String totalRevenue) {
        List<GPUDevice> devices = gpuDeviceRepository.findByDeviceId(deviceId);
        if (!devices.isEmpty()) {
            GPUDevice gpuDevice = devices.getFirst();

            gpuDevice.setDeviceIndex(deviceIndex);
            gpuDevice.setBrand(brand);
            gpuDevice.setModel(model);
            gpuDevice.setMemoryTotal(memoryTotal);
            gpuDevice.setArchitecture(architecture);
            gpuDevice.setMemoryType(memoryType);
            gpuDevice.setStatus(status);
            gpuDevice.setIsRentable(isRentable);
            gpuDevice.setHourlyRate(new BigDecimal(hourlyRate));
            gpuDevice.setTotalRuntimeHours(new BigDecimal(totalRuntimeHours));
            gpuDevice.setTotalRevenue(new BigDecimal(totalRevenue));

            gpuDeviceRepository.save(gpuDevice);
        }
    }

    public void leaseDevice(String deviceId){
        gpuDeviceRepository.updateIsRentableByDeviceId(false ,deviceId);
    }

    public void returnDevice(String deviceId) {
        gpuDeviceRepository.updateIsRentableByDeviceId(true ,deviceId);
    }

    public List<GPUDevice> getByDeviceIds(List<String> deviceIds) {
        return gpuDeviceRepository.findByDeviceIdIn(deviceIds);
    }
}
