package com.gpu.rentaler;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.entity.ServerInfo;
import com.gpu.rentaler.service.GPUInfoCollector;
import com.gpu.rentaler.service.ServerInfoCollector;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupService implements CommandLineRunner {
    @DubboReference
    MonitorService monitorService;

    @Resource
    private GPUInfoCollector gpuInfoCollector;

    @Resource
    private ServerInfoCollector serverInfoCollector;
    /**
     * 程序启动完成后执行的方法
     */
    @Override
    public void run(String... args) throws Exception {
        // 调用需要在启动后执行的方法
        executeOnStartup();
    }

    /**
     * 启动后需要执行的业务方法
     */
    private void executeOnStartup() {
        System.out.println("程序已完整启动，正在执行初始化操作...");
        ServerInfo serverInfo = serverInfoCollector.getServerInfo();
        List<GPUDeviceInfo> allGPUInfo = gpuInfoCollector.getAllGPUInfo();
        serverInfo.setGpuDeviceInfos(allGPUInfo);

        monitorService.reportServerInfo(serverInfo);
    }
}
