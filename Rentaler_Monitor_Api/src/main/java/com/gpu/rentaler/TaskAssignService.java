package com.gpu.rentaler;


import com.gpu.rentaler.entity.VirtulBoxResInfo;

public interface TaskAssignService {
    VirtulBoxResInfo createVirtulBox(String gouType);

    void stopDockerContainer(String containerId);
}
