package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.TaskAssignService;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.constant.GPUType;
import com.gpu.rentaler.entity.VirtulBoxResInfo;
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

    public VirtulBoxResInfo createDockerContainer(Long serverId, List<String> deviceIds) {
        Server server = serverService.getById(serverId);
        List<GPUDevice> devices = gpuDeviceService.getByDeviceIds(deviceIds);
        if (!devices.isEmpty()) {
            GPUDevice first = devices.getFirst();
            String brand = first.getBrand();
            String ip = server.getIpAddress();
            int port = 20880;

            TaskAssignService myService = dubboDynamicInvoker.getService(TaskAssignService.class, "1.0.0", ip, port ,30000);
            VirtulBoxResInfo dockerContainer = null;
            if (GPUType.NVIDIA.equalsIgnoreCase(brand)) {
                dockerContainer = myService.createVirtulBox(GPUType.NVIDIA);
                dockerContainer.setIp(ip);
                System.out.println("调用结果: " + JsonUtils.stringify(dockerContainer));
            } else if (GPUType.AMD.equalsIgnoreCase(brand)) {
                dockerContainer = myService.createVirtulBox(GPUType.AMD);
                dockerContainer.setIp(ip);
                System.out.println("调用结果: " + JsonUtils.stringify(dockerContainer));
            }
            return dockerContainer;
        }
        return null;
    }
}
