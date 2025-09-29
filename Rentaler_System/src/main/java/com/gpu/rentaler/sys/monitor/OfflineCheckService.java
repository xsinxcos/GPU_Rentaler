package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.sys.service.GPUDeviceService;
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


    @Scheduled(fixedRate = 5000) // 5秒
    public void executeTask() {
        List<Long> deadServers = serverHeartBeatRecord.getDeadServers();
        for (Long deadServer : deadServers) {
            serverService.changeStatus(deadServer, DeviceStatus.OFFLINE);
            gpuDeviceService.changeStatusByServerId(deadServer, DeviceStatus.OFFLINE);
        }
    }
}
