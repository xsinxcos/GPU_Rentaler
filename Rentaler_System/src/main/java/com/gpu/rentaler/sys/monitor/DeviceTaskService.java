package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.TaskAssignService;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.entity.DockerCreateResInfo;
import com.gpu.rentaler.infra.service.DubboDynamicInvoker;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class DeviceTaskService {
    @Resource
    private ServerService serverService;

    @Resource
    private DubboDynamicInvoker dubboDynamicInvoker;

    public DockerCreateResInfo createDockerContainer(Long serverId) {
        Server server = serverService.getById(serverId);

        // 假设运行时决定调用的 provider IP/Port
        String ip = server.getIpAddress();
        int port = 20880;

        TaskAssignService myService = dubboDynamicInvoker.getService(TaskAssignService.class, "1.0.0", ip, port);
        DockerCreateResInfo dockerContainer = myService.createDockerContainer();
        System.out.println("调用结果: " + JsonUtils.stringify(dockerContainer));
        return dockerContainer;
    }
}
