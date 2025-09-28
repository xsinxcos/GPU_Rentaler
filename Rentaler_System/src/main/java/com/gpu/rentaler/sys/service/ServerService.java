package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.repository.ServerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServerService {
    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Transactional
    public void saveOrUpdateServerInfo(String serverId, String hostname, String ipAddress, String cpuModel, int cpuCores, int ramTotalGb, int storageTotalGb, int gpuSlots) {
        List<Server> servers = serverRepository.getServerByServerId(serverId);
        Server server = null;
        if(!servers.isEmpty()){
            server = servers.getFirst();
        }
        Optional.ofNullable(server).ifPresentOrElse(s -> {
            // Update existing server
            s.setHostname(hostname);
            s.setIpAddress(ipAddress);
            s.setCpuModel(cpuModel);
            s.setCpuCores(cpuCores);
            s.setRamTotalGb(ramTotalGb);
            s.setStorageTotalGb(storageTotalGb);
            s.setGpuSlots(gpuSlots);
            serverRepository.save(s);
        }, () -> {
            // Create new server
            Server newServer = new Server();
            newServer.setServerId(serverId);
            newServer.setHostname(hostname);
            newServer.setIpAddress(ipAddress);
            newServer.setCpuModel(cpuModel);
            newServer.setCpuCores(cpuCores);
            newServer.setRamTotalGb(ramTotalGb);
            newServer.setStorageTotalGb(storageTotalGb);
            newServer.setGpuSlots(gpuSlots);
            serverRepository.save(newServer);
        });
    }
}
