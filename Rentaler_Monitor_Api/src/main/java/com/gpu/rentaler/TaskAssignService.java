package com.gpu.rentaler;


import com.gpu.rentaler.entity.DContainerInfoResp;

import java.util.List;

public interface TaskAssignService {

    void stopDockerContainer(String containerId);

    DContainerInfoResp upDockerImage(byte[] imageFile, List<Integer> deviceIndexs);
}
