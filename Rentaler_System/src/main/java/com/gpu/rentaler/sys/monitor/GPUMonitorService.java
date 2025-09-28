package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.MonitorService;
import com.gpu.rentaler.entity.ServerInfo;
import com.gpu.rentaler.common.JsonUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@DubboService
public class GPUMonitorService implements MonitorService {
    private static final Logger log = LogManager.getLogger(GPUMonitorService.class);

    @Override
    public void reportServerInfo(ServerInfo serverInfo) {
        String stringify = JsonUtils.stringify(serverInfo);
        log.debug(stringify);
    }
}
