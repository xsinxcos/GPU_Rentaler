package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.common.StringUtils;
import com.gpu.rentaler.sys.constant.DeviceStatus;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.GPURealDevices;
import com.gpu.rentaler.sys.repository.GPUDeviceRepository;
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

@Service
public class GPUDeviceService {
    private final GPUDeviceRepository gpuDeviceRepository;

    public GPUDeviceService(GPUDeviceRepository gpuDeviceRepository) {
        this.gpuDeviceRepository = gpuDeviceRepository;
    }

    @Transactional
    public void saveOrUpdateGPUDeviceType(List<GPURealDevices> gPURealDevices) {
        List<GPURealDevices> saveDevices = new ArrayList<>();
        for (GPURealDevices realDevice : gPURealDevices) {
            if (!gpuDeviceRepository.existsByDeviceIdOrModel(realDevice.getModel(), realDevice.getModel())) {
                saveDevices.add(realDevice);
            }
        }

        for (GPURealDevices saveDevice : saveDevices) {
            GPUDevice newDevice = new GPUDevice();
            newDevice.setDeviceId(saveDevice.getModel());
            newDevice.setMemoryTotal(saveDevice.getMemoryTotal());
            newDevice.setBrand(saveDevice.getBrand());
            newDevice.setModel(saveDevice.getModel());
            gpuDeviceRepository.save(newDevice);
        }
    }

    public GPUDevice getByDeviceId(String deviceId) {
        return gpuDeviceRepository.findByDeviceId(deviceId).getFirst();
    }

    @Transactional
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
            if (!StringUtils.isBlank(hourlyRate)) {
                gpuDevice.setHourlyRate(new BigDecimal(hourlyRate));
            }
            if (!StringUtils.isBlank(totalRuntimeHours)) {
                gpuDevice.setTotalRuntimeHours(new BigDecimal(totalRuntimeHours));
            }
            if (!StringUtils.isBlank(totalRevenue)) {
                gpuDevice.setTotalRevenue(new BigDecimal(totalRevenue));
            }
            gpuDeviceRepository.save(gpuDevice);
        }
    }

    public void updateCanRentable(List<String> deviceIds) {
        gpuDeviceRepository.updateIsRentableByDeviceIdIn(true, deviceIds);
    }

    public void updateNotRentable(List<String> deviceIds) {
        gpuDeviceRepository.updateIsRentableByDeviceIdIn(false, deviceIds);
    }

    public void refresh(List<String> canRentable, List<String> cantRentable) {
        if(!canRentable.isEmpty()){
            updateCanRentable(canRentable);
            cantRentable.removeAll(canRentable);
        }
        updateNotRentable(cantRentable);
    }
}
