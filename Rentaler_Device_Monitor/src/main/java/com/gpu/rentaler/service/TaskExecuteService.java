package com.gpu.rentaler.service;

import com.gpu.rentaler.TaskAssignService;
import com.gpu.rentaler.entity.DockerCreateResInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

@DubboService(version = "1.0.0")
@Service
public class TaskExecuteService implements TaskAssignService {

    @Override
    public DockerCreateResInfo createDockerContainer() {
        return new DockerCreateResInfo("-1" ,"2222" ,"root" ,"123456");
    }
}
