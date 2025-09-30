package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.MonitorService;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.entity.ServerInfo;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.GPUProcessActivityService;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService(version = "1.0.0")
@Service
public class GPUMonitorService implements MonitorService {
    private static final Logger log = LogManager.getLogger(GPUMonitorService.class);

    @Resource
    private ServerService serverService;

    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private ServerHeartBeatRecord serverHeartBeatRecord;

    @Resource
    private GPUProcessActivityService gpuProcessActivityService;

    @Override
    public Long reportServerInfo(ServerInfo serverInfo) {
        Server server = serverService.saveServerInfo(
            serverInfo.getHostname(),
            serverInfo.getIpAddress(),
            serverInfo.getCpuModel(),
            serverInfo.getCpuCores(),
            serverInfo.getRamTotalGb(),
            serverInfo.getStorageTotalGb(),
            serverInfo.getGpuSlots()
        );
        List<GPUDeviceInfo> gpuDeviceInfos = serverInfo.getGpuDeviceInfos();
        gpuDeviceService.saveOrUpdateGPUDeviceInfo(server.getId(), gpuDeviceInfos);
        return server.getId();
    }

    @Override
    public void updateServerInfo(ServerInfo serverInfo) {
        asyncExecute(() -> {
            serverService.updateServerInfo(
                serverInfo.getServerId(),
                serverInfo.getHostname(),
                serverInfo.getIpAddress(),
                serverInfo.getCpuModel(),
                serverInfo.getCpuCores(),
                serverInfo.getRamTotalGb(),
                serverInfo.getStorageTotalGb(),
                serverInfo.getGpuSlots()
            );
            List<GPUDeviceInfo> gpuDeviceInfos = serverInfo.getGpuDeviceInfos();
            gpuDeviceService.saveOrUpdateGPUDeviceInfo(serverInfo.getServerId(), gpuDeviceInfos);
        });
    }

    @Override
    public void reportProcessMsg(Long serverId, List<ProcessInfo> processInfos) {
        asyncExecute(() -> {
            serverHeartBeatRecord.recordHeartBeat(serverId);
            processInfos.forEach(item ->
                gpuProcessActivityService.saveActivity(item.getPid() ,item.getName() ,item.getDeviceId() ,item.getTime()));
        });
    }

    @Async("monitorTaskExecutor")
    protected void asyncExecute(Runnable task) {
        task.run();
    }
}
