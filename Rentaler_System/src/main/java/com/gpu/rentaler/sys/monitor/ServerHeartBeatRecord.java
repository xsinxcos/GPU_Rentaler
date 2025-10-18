package com.gpu.rentaler.sys.monitor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerHeartBeatRecord {

    private final Map<Long, Long> serverHeartBeatMap = new ConcurrentHashMap<>(); // 服务器ID到最后心跳时间的映射

    private final Long heartbeatTimeout = 30000L; // 心跳超时时间，单位：毫秒（例如：60秒）

    public synchronized void load(List<Long> serverIds){
        long currented = System.currentTimeMillis();
        serverIds.forEach(item -> serverHeartBeatMap.put(item ,currented - heartbeatTimeout));
    }

    public synchronized void recordHeartBeat(Long serverId) {
        serverHeartBeatMap.put(serverId, System.currentTimeMillis());
    }

    public synchronized List<Long> getDeadServersAndRemove() {
        Long currentTime = System.currentTimeMillis();
        List<Long> dead = serverHeartBeatMap.entrySet().stream()
            .filter(entry -> currentTime - entry.getValue() > heartbeatTimeout)
            .map(Map.Entry::getKey)
            .toList();
        dead.forEach(serverHeartBeatMap::remove);
        return dead;
    }
}
