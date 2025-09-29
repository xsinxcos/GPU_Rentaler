package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.TaskAssignService;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.constant.GPUType;
import com.gpu.rentaler.entity.DockerCreateResInfo;
import com.gpu.rentaler.infra.service.DubboDynamicInvoker;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceTaskService {
    @Resource
    private ServerService serverService;

    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private DubboDynamicInvoker dubboDynamicInvoker;

    public DockerCreateResInfo createDockerContainer(Long serverId, List<Long> gpuIds) {
        Server server = serverService.getById(serverId);
        List<GPUDevice> devices = gpuDeviceService.getById(gpuIds);
        if (!devices.isEmpty()) {
            GPUDevice first = devices.getFirst();
            String brand = first.getBrand();
            // 假设运行时决定调用的 provider IP/Port
            String ip = server.getIpAddress();
            int port = 20880;

            TaskAssignService myService = dubboDynamicInvoker.getService(TaskAssignService.class, "1.0.0", ip, port ,30000);
            if (GPUType.NVIDIA.equalsIgnoreCase(brand)) {
                DockerCreateResInfo dockerContainer = myService.createDockerContainer(GPUType.NVIDIA);
                System.out.println("调用结果: " + JsonUtils.stringify(dockerContainer));
            } else if (GPUType.AMD.equalsIgnoreCase(brand)) {
                DockerCreateResInfo dockerContainer = myService.createDockerContainer(GPUType.AMD);
                System.out.println("调用结果: " + JsonUtils.stringify(dockerContainer));
            }
        }
        return null;
    }
}
