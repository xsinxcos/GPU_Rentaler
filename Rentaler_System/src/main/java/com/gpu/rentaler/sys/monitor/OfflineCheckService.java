package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.sys.constant.DeviceStatus;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.GPURealDevicesService;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfflineCheckService {

    @Resource
    private ServerHeartBeatRecord serverHeartBeatRecord;

    @Resource
    private ServerService serverService;

    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private GPURealDevicesService gpuRealDevicesService;


    @Scheduled(fixedRate = 5000) // 5秒
    public void executeTask() {
        List<Long> deadServers = serverHeartBeatRecord.getDeadServersAndRemove();
        for (Long deadServer : deadServers) {
            serverService.changeStatus(deadServer, DeviceStatus.OFFLINE);
            gpuRealDevicesService.changeStatusByServerId(deadServer, DeviceStatus.OFFLINE);
        }
    }

    @Scheduled(fixedRate = 10000) // 10秒
    public void updateRentable() {
        List<String> canRentable = gpuRealDevicesService.findCanRentable();
        List<String> cantRentable = gpuRealDevicesService.findCantRentable();

        gpuDeviceService.updateCanRentable(canRentable);
        gpuDeviceService.updateNotRentable(cantRentable);
    }
}
