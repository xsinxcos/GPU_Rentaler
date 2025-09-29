package com.gpu.rentaler;


import com.gpu.rentaler.entity.DockerCreateResInfo;

public interface TaskAssignService {
    DockerCreateResInfo createDockerContainer(String gouType);
}
