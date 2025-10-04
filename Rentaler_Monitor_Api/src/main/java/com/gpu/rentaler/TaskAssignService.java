package com.gpu.rentaler;


import com.gpu.rentaler.entity.DContainerInfoResp;
import com.gpu.rentaler.entity.VirtulBoxResInfo;

import java.io.InputStream;
import java.util.List;

public interface TaskAssignService {
    VirtulBoxResInfo createVirtulBox(String gouType);

    void stopDockerContainer(String containerId);

    DContainerInfoResp upDockerImage(InputStream inputStream , List<Integer> deviceIndexs);
}
