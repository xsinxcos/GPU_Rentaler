package com.gpu.rentaler;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.entity.ServerInfo;
import com.gpu.rentaler.grpc.MonitorServiceGrpc;
import com.gpu.rentaler.grpc.MonitorServiceProto;
import com.gpu.rentaler.service.GPUFactory;
import com.gpu.rentaler.service.ServerIDManager;
import com.gpu.rentaler.service.ServerInfoCollector;
import jakarta.annotation.Resource;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GrpcStartupService implements CommandLineRunner {

    @GrpcClient("backend")
    private MonitorServiceGrpc.MonitorServiceBlockingStub monitorServiceBlockingStub;

    // 原子布尔值确保线程安全的执行标记
    private static final AtomicBoolean HAS_EXECUTED = new AtomicBoolean(false);

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
        MonitorServiceProto.ServerInfo serverInfoProto = toServerInfoProto(serverInfo, allGPUInfo);

        if (serverInfo.getServerId() == null) {
            MonitorServiceProto.Int64Value resp = monitorServiceBlockingStub.reportServerInfo(serverInfoProto);
            long serverId = resp.getValue();
            serverIDManager.saveServerID(serverId);
        } else {
            monitorServiceBlockingStub.updateServerInfo(serverInfoProto);
        }
    }

    public MonitorServiceProto.ServerInfo toServerInfoProto(ServerInfo serverInfo, List<GPUDeviceInfo> allGPUInfos) {
        List<MonitorServiceProto.GPUDeviceInfo> deviceInfosProtos = new ArrayList<>();
        for (GPUDeviceInfo gpuInfo : allGPUInfos) {
            MonitorServiceProto.GPUDeviceInfo deviceInfoProto = MonitorServiceProto.GPUDeviceInfo.newBuilder()
                .setBrand(gpuInfo.getBrand())
                .setDeviceId(gpuInfo.getDeviceId())
                .setModel(gpuInfo.getModel())
                .setDeviceIndex(gpuInfo.getDeviceIndex())
                .setMemoryTotal(gpuInfo.getMemoryTotal())
                .build();

            deviceInfosProtos.add(deviceInfoProto);
        }

        return MonitorServiceProto.ServerInfo.newBuilder()
            .setServerId(serverInfo.getServerId())
            .setHostname(serverInfo.getHostname())
            .setIpAddress(serverInfo.getIpAddress())
            .setCpuModel(serverInfo.getCpuModel())
            .setCpuCores(serverInfo.getCpuCores())
            .setRamTotalGb(serverInfo.getRamTotalGb())
            .setStorageTotalGb(serverInfo.getStorageTotalGb())
            .setGpuSlots(serverInfo.getGpuSlots())
            .addAllGpuDeviceInfos(deviceInfosProtos)
            .build();
    }
}
