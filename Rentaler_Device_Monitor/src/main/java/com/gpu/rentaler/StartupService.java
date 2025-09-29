package com.gpu.rentaler;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.entity.ServerInfo;
import com.gpu.rentaler.service.GPUFactory;
import com.gpu.rentaler.service.ServerIDManager;
import com.gpu.rentaler.service.ServerInfoCollector;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class StartupService implements CommandLineRunner {
    // 原子布尔值确保线程安全的执行标记
    private static final AtomicBoolean HAS_EXECUTED = new AtomicBoolean(false);

    @DubboReference(version = "1.0.0")
    MonitorService monitorService;  // 注意：确保此类已正确定义

    @Resource
    private GPUFactory gpuFactory;

    @Resource
    private ServerInfoCollector serverInfoCollector;

    @Resource
    private ServerIDManager serverIDManager;

    @Override
    public void run(String... args) {
        // 仅当未执行过时才执行，使用原子操作保证线程安全
        if (HAS_EXECUTED.compareAndSet(false, true)) {
            executeOnStartup();
        }
    }

    private void executeOnStartup() {
        ServerInfo serverInfo = serverInfoCollector.getServerInfo();
        List<GPUDeviceInfo> allGPUInfo = gpuFactory.getAllGPUInfo();
        serverInfo.setGpuDeviceInfos(allGPUInfo);

        if (serverInfo.getServerId() == null) {
            Long serverId = monitorService.reportServerInfo(serverInfo);
            serverIDManager.saveServerID(serverId);
        } else {
            monitorService.updateServerInfo(serverInfo);
        }
    }
}
