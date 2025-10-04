package com.gpu.rentaler.entity;

import java.io.Serializable;

public record DContainerInfoResp (
    String containerName,
    String containerId
) implements Serializable {
}
