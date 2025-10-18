package com.gpu.rentaler.service;

import com.google.protobuf.Timestamp;
import com.gpu.rentaler.entity.GPUUsage;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.grpc.MonitorServiceGrpc;
import com.gpu.rentaler.grpc.MonitorServiceProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrpcProcessTaskService {

    private static final Logger log = LogManager.getLogger(GrpcProcessTaskService.class);

    @GrpcClient("backend")
    private MonitorServiceGrpc.MonitorServiceBlockingStub monitorServiceBlockingStub;

    // 定义时间格式化器
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Resource
    private GPUFactory gpuFactory;

    @Resource
    private ServerIDManager serverIDManager;

    /**
     * 每10秒执行一次的定时任务
     * fixedRate: 以固定速率执行，即从上一次任务开始时间算起，间隔指定时间后再次执行
     */
    @Scheduled(fixedRate = 10000) // 10秒
    public void reportProcessMsg() {
        if(serverIDManager.getServerId() == null) return;
        try {
            List<ProcessInfo> allGPUActivityInfo = gpuFactory.getAllDockerContainerGPUActivityInfo();

            List<MonitorServiceProto.ProcessInfo> grpcProInfo = new ArrayList<>();
            for (ProcessInfo processInfo : allGPUActivityInfo) {
                Instant instant = processInfo.getTime();
                // 转为 Protobuf Timestamp
                Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build();
                MonitorServiceProto.ProcessInfo info = MonitorServiceProto.ProcessInfo.newBuilder()
                    .setPid(processInfo.getPid())
                    .setContainerId(processInfo.getContainerId())
                    .setDeviceId(processInfo.getDeviceId())
                    .setName(processInfo.getName())
                    .setTime(timestamp)
                    .setDurationSeconds(10)
                    .build();
                grpcProInfo.add(info);
            }

            Long serverId = serverIDManager.getServerId();
            MonitorServiceProto.ReportProcessMsgRequest request = MonitorServiceProto.ReportProcessMsgRequest.newBuilder()
                .setServerId(serverId)
                .addAllProcessInfos(grpcProInfo)
                .build();
            monitorServiceBlockingStub.reportProcessMsg(request);
        } catch (Exception e) {
            log.info("{} - Error in reportProcessMsg: {}", LocalDateTime.now().format(formatter), e.getMessage());
        }
    }


    /**
     * 每10秒执行一次的定时任务
     * fixedRate: 以固定速率执行，即从上一次任务开始时间算起，间隔指定时间后再次执行
     */
    @Scheduled(fixedRate = 10000) // 10秒
    public void reportGPUUsage() {
        if(serverIDManager.getServerId() == null) return;
        try {
            // 获取 GPU 使用情况
            List<GPUUsage> gpuUsages = gpuFactory.getAllGPUUsage();

            // 构建 gRPC 请求
            MonitorServiceProto.ReportGPUUsageRequest.Builder requestBuilder =
                MonitorServiceProto.ReportGPUUsageRequest.newBuilder()
                    .setServerId(serverIDManager.getServerId());

            for (GPUUsage usage : gpuUsages) {
                MonitorServiceProto.GPUUsageInfo info = MonitorServiceProto.GPUUsageInfo.newBuilder()
                    .setDeviceId(usage.deviceId())
                    .setGpuUtilizationPercent(usage.gpuUtilizationPercent())
                    .setMemoryUsedPercent(usage.memoryUsedPercent())
                    .setTime(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build())
                    .build();
                requestBuilder.addGpuUsages(info);
            }

            MonitorServiceProto.ReportGPUUsageRequest request = requestBuilder.build();

            // 调用 gRPC 上报
            monitorServiceBlockingStub.reportGPUUsage(request);

        } catch (Exception e) {
            log.error("{} - Error in reportGPUUsage: {}", LocalDateTime.now().format(formatter), e.getMessage(), e);
        }
    }

}
