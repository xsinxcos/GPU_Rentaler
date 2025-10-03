package com.gpu.rentaler;


import com.gpu.rentaler.entity.VirtulBoxResInfo;

import java.io.InputStream;

public interface TaskAssignService {
    VirtulBoxResInfo createVirtulBox(String gouType);

    void stopDockerContainer(String containerId);

    void upDockerImage(InputStream inputStream);
}
