package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.MonitorService;
import com.gpu.rentaler.entity.ServerInfo;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class GPUMonitorService implements MonitorService {
    private static final Logger log = LogManager.getLogger(GPUMonitorService.class);

    @Resource
    private ServerService serverService;

    @Override
    public void reportServerInfo(ServerInfo serverInfo) {
        String stringify = JsonUtils.stringify(serverInfo);
        serverService.saveOrUpdateServerInfo(
            serverInfo.getServerId(),
            serverInfo.getHostname(),
            serverInfo.getIpAddress(),
            serverInfo.getCpuModel(),
            serverInfo.getCpuCores(),
            serverInfo.getRamTotalGb(),
            serverInfo.getStorageTotalGb(),
            serverInfo.getGpuSlots()
        );
        log.debug(stringify);
    }
}
