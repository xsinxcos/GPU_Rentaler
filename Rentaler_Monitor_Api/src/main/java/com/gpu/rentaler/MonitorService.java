package com.gpu.rentaler;

import com.gpu.rentaler.entity.ProcessInfo;
import com.gpu.rentaler.entity.ServerInfo;

import java.util.List;

public interface MonitorService {

    Long reportServerInfo(ServerInfo serverInfo);

    void updateServerInfo(ServerInfo serverInfo);

    void reportProcessMsg(Long serverId, List<ProcessInfo> processInfos);
}
