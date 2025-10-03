package com.gpu.rentaler.service;

import com.gpu.rentaler.TaskAssignService;
import com.gpu.rentaler.config.DockerFileLocation;
import com.gpu.rentaler.constant.GPUType;
import com.gpu.rentaler.entity.ContainerInfo;
import com.gpu.rentaler.entity.VirtulBoxResInfo;
import com.gpu.rentaler.utils.DockerExecutor;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@DubboService(version = "1.0.0")
@Service
public class TaskExecuteService implements TaskAssignService {

    private static final Logger log = LogManager.getLogger(TaskExecuteService.class);

    @Override
    public VirtulBoxResInfo createVirtulBox(String gpuType) {
        DockerComposeExecutor executor = null;
        if (GPUType.NVIDIA.equals(gpuType)) {
            executor = new DockerComposeExecutor(DockerFileLocation.NVIDIA_DOCKER);
        } else if (GPUType.AMD.equals(gpuType)) {
            executor = new DockerComposeExecutor(DockerFileLocation.AMD_DOCKER);
        }
        VirtulBoxResInfo createResInfo = new VirtulBoxResInfo();
        Optional.ofNullable(executor).ifPresent(item -> {
            String secret = UUID.randomUUID().toString();
            item.addEnvironmentVariable("SSH_USER", "root");
            item.addEnvironmentVariable("SSH_PASSWORD", secret);
            try {
                item.up();
                ContainerInfo containerInfo = item.getContainerInfo();
                createResInfo.setContainerId(containerInfo.getContainerId());
                createResInfo.setPort(containerInfo.getPort());
                createResInfo.setSshName(containerInfo.getSshName());
                createResInfo.setSshPassword(containerInfo.getSshPassword());
            } catch (IOException e) {
                log.error("Error executing docker-compose up: {}", e.getMessage());
            }
        });
        return createResInfo;
    }

    @Override
    public void stopDockerContainer(String containerId) {
        try {
            DockerExecutor.stopContainer(containerId ,60);
        } catch (IOException e) {
            log.error("Error stopping container {}: {}", containerId, e.getMessage());
        }
    }
}
