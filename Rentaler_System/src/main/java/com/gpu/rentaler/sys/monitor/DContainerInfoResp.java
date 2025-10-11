package com.gpu.rentaler.sys.monitor;

import java.io.Serializable;

public record DContainerInfoResp (
    String containerName,
    String containerId
) implements Serializable {
}
