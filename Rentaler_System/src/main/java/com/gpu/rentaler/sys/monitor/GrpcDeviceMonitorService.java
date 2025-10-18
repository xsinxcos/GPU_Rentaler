package com.gpu.rentaler.sys.monitor;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.gpu.rentaler.grpc.MonitorServiceGrpc;
import com.gpu.rentaler.grpc.MonitorServiceProto;
import com.gpu.rentaler.sys.model.GPURealDevices;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.GPUProcessActivityService;
import com.gpu.rentaler.sys.service.GPURealDevicesService;
import com.gpu.rentaler.sys.service.ServerService;
import com.gpu.rentaler.sys.service.dto.BasicGPUDeviceDTO;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GrpcService
@Service
public class GrpcDeviceMonitorService extends MonitorServiceGrpc.MonitorServiceImplBase {
    private static final Logger log = LogManager.getLogger(GrpcDeviceMonitorService.class);

    @Resource
    private ServerService serverService;

    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private ServerHeartBeatRecord serverHeartBeatRecord;

    @Resource
    private GPUProcessActivityService gpuProcessActivityService;

    @Resource
    private GPURealDevicesService gpuRealDevicesService;

    @Override
    public void reportServerInfo(MonitorServiceProto.ServerInfo request, StreamObserver<MonitorServiceProto.Int64Value> responseObserver) {
        Server server = serverService.saveServerInfo(
            request.getHostname(),
            request.getIpAddress(),
            request.getCpuModel(),
            request.getCpuCores(),
            request.getRamTotalGb(),
            request.getStorageTotalGb(),
            request.getGpuSlots()
        );
        List<MonitorServiceProto.GPUDeviceInfo> gpuDeviceInfosList = request.getGpuDeviceInfosList();
        List<BasicGPUDeviceDTO> dtos = new ArrayList<>();
        for (MonitorServiceProto.GPUDeviceInfo info : gpuDeviceInfosList) {
            dtos.add(new BasicGPUDeviceDTO(info.getDeviceIndex(), info.getDeviceId(), info.getBrand(), info.getModel(), info.getMemoryTotal()));
        }
        List<GPURealDevices> gpuRealDevices = gpuRealDevicesService.saveOrUpdateGPUDeviceInfo(server.getId(), dtos);

        gpuDeviceService.saveOrUpdateGPUDeviceType(gpuRealDevices);
        MonitorServiceProto.Int64Value resp = MonitorServiceProto.Int64Value.newBuilder().setValue(server.getId()).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void updateServerInfo(MonitorServiceProto.ServerInfo request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

        serverService.updateServerInfo(
            request.getServerId(),
            request.getHostname(),
            request.getIpAddress(),
            request.getCpuModel(),
            request.getCpuCores(),
            request.getRamTotalGb(),
            request.getStorageTotalGb(),
            request.getGpuSlots()
        );
        List<MonitorServiceProto.GPUDeviceInfo> gpuDeviceInfosList = request.getGpuDeviceInfosList();
        List<BasicGPUDeviceDTO> dtos = new ArrayList<>();
        for (MonitorServiceProto.GPUDeviceInfo info : gpuDeviceInfosList) {
            dtos.add(new BasicGPUDeviceDTO(info.getDeviceIndex(), info.getDeviceId(), info.getBrand(), info.getModel(), info.getMemoryTotal()));
        }
        List<GPURealDevices> gpuRealDevices = gpuRealDevicesService.saveOrUpdateGPUDeviceInfo(request.getServerId(), dtos);

        gpuDeviceService.saveOrUpdateGPUDeviceType(gpuRealDevices);
    }

    @Override
    public void reportProcessMsg(MonitorServiceProto.ReportProcessMsgRequest request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

        long serverId = request.getServerId();
        serverHeartBeatRecord.recordHeartBeat(serverId);

        List<MonitorServiceProto.ProcessInfo> processInfos = request.getProcessInfosList();
        String recordId = UUID.randomUUID().toString();
        processInfos.forEach(item ->
            {
                Timestamp time = item.getTime();
                Instant instant = Instant.ofEpochSecond(time.getSeconds(), time.getNanos());
                gpuProcessActivityService.saveActivity(item.getPid(), item.getName(),
                    item.getDeviceId(), instant, item.getDurationSeconds(), recordId ,item.getContainerId());
            }
        );
    }

    @Override
    public void reportGPUUsage(MonitorServiceProto.ReportGPUUsageRequest request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

        List<MonitorServiceProto.GPUUsageInfo> gpuUsagesList = request.getGpuUsagesList();

        List<String> notRDeviceIds = gpuUsagesList.stream().filter(item -> item.getGpuUtilizationPercent() >= 80 || item.getMemoryUsedPercent() >= 80)
            .map(MonitorServiceProto.GPUUsageInfo::getDeviceId).toList();

        List<String> canRDeviceIds = gpuUsagesList.stream().filter(item -> item.getGpuUtilizationPercent() < 80 && item.getMemoryUsedPercent() < 80)
            .map(MonitorServiceProto.GPUUsageInfo::getDeviceId).toList();

        gpuRealDevicesService.notRentable(notRDeviceIds);
        gpuRealDevicesService.canRentable(canRDeviceIds);
    }
}
