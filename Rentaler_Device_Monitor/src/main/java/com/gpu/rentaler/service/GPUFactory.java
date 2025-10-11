package com.gpu.rentaler.service;

import com.gpu.rentaler.entity.GPUDeviceInfo;
import com.gpu.rentaler.entity.GPUUsage;
import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.service.Intel.IntelActivityFetcher;
import com.gpu.rentaler.service.Intel.InterGPUInfoCollector;
import com.gpu.rentaler.service.amd.AMDActivityFetcher;
import com.gpu.rentaler.service.amd.AMDGPUInfoCollector;
import com.gpu.rentaler.service.nvidia.NvidiaActivityFetcher;
import com.gpu.rentaler.service.nvidia.NvidiaGPUInfoCollector;
import com.gpu.rentaler.utils.DockerExecutor;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GPUFactory {

    @Resource
    private NvidiaGPUInfoCollector nvidiaGPUInfoCollector;

    @Resource
    private AMDGPUInfoCollector amdgpuInfoCollector;

    @Resource
    private InterGPUInfoCollector interGPUInfoCollector;

    @Resource
    private NvidiaActivityFetcher nvidiaActivityFetcher;

    @Resource
    private AMDActivityFetcher amdActivityFetcher;

    @Resource
    private IntelActivityFetcher intelActivityFetcher;


    public List<GPUDeviceInfo> getAllGPUInfo() {
        List<GPUDeviceInfo> all = new ArrayList<>();
        all.addAll(nvidiaGPUInfoCollector.getAllGPUInfo());
        all.addAll(amdgpuInfoCollector.getAllGPUInfo());
        all.addAll(interGPUInfoCollector.getAllGPUInfo());
        return all;
    }

    public List<ProcessInfo> getAllGPUActivityInfo() {
        List<ProcessInfo> all = new ArrayList<>();
        all.addAll(nvidiaActivityFetcher.getGpuProcessList());
        all.addAll(amdActivityFetcher.getGpuProcessList());
        all.addAll(intelActivityFetcher.getGpuProcessList());
        return all;
    }

    public List<ProcessInfo> getAllDockerContainerGPUActivityInfo() throws IOException {

        List<ProcessInfo> all = new ArrayList<>();
        all.addAll(nvidiaActivityFetcher.getGpuProcessList());
        all.addAll(amdActivityFetcher.getGpuProcessList());
        all.addAll(intelActivityFetcher.getGpuProcessList());

        // 提取 PID 列表
        List<String> pids = all.stream()
            .map(ProcessInfo::getPid)
            .toList();

        // 获取 PID 对应的容器信息
        List<DockerExecutor.PidContainerRecord> records = DockerExecutor.getPidContainerInfo(pids);

        // 构建 PID -> PidContainerRecord 映射，方便快速匹配
        Map<String, DockerExecutor.PidContainerRecord> pidRecordMap = records.stream()
            .collect(Collectors.toMap(DockerExecutor.PidContainerRecord::pid, r -> r));

        // 填充 ProcessInfo
        for (ProcessInfo pi : all) {
            if (pi == null || pi.getPid() == null) continue;

            DockerExecutor.PidContainerRecord record = pidRecordMap.get(pi.getPid());
            if (record != null) {
                pi.setContainerId(record.containerId());
                pi.setName(record.processName());
            }
        }
        return all;
    }

    public List<GPUUsage> getAllGPUUsage(){
        List<GPUUsage> gpuUsages = new ArrayList<>();
        gpuUsages.addAll(nvidiaActivityFetcher.getGPUUSage());
        gpuUsages.addAll(amdActivityFetcher.getGPUUSage());
        gpuUsages.addAll(intelActivityFetcher.getGPUUSage());
        return gpuUsages;
    }

}
