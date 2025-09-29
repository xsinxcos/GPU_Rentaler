package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.constant.DeviceStatus;
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
    public Server saveServerInfo(String hostname, String ipAddress, String cpuModel, int cpuCores, int ramTotalGb, int storageTotalGb, int gpuSlots) {
        // Create new server
        Server newServer = new Server();
        newServer.setHostname(hostname);
        newServer.setIpAddress(ipAddress);
        newServer.setCpuModel(cpuModel);
        newServer.setCpuCores(cpuCores);
        newServer.setRamTotalGb(ramTotalGb);
        newServer.setStorageTotalGb(storageTotalGb);
        newServer.setGpuSlots(gpuSlots);
        newServer.setStatus(DeviceStatus.ONLINE);
        return serverRepository.save(newServer);
    }

    @Transactional
    public void updateServerInfo(Long serverId, String hostname, String ipAddress, String cpuModel, int cpuCores, int ramTotalGb, int storageTotalGb, int gpuSlots) {
        Optional<Server> optionalServer = serverRepository.findById(serverId);
        if (optionalServer.isPresent()) {
            Server existingServer = optionalServer.get();
            existingServer.setHostname(hostname);
            existingServer.setIpAddress(ipAddress);
            existingServer.setCpuModel(cpuModel);
            existingServer.setCpuCores(cpuCores);
            existingServer.setRamTotalGb(ramTotalGb);
            existingServer.setStorageTotalGb(storageTotalGb);
            existingServer.setGpuSlots(gpuSlots);
            existingServer.setStatus(DeviceStatus.ONLINE);
            serverRepository.save(existingServer);
        } else {
            // Handle the case where the server does not exist
            throw new RuntimeException("Server with ID " + serverId + " not found.");
        }
    }

    public void changeStatus(Long serverId, String status) {
        Optional<Server> optionalServer = serverRepository.findById(serverId);
        if (optionalServer.isPresent()) {
            Server existingServer = optionalServer.get();
            existingServer.setStatus(status);
            serverRepository.save(existingServer);
        } else {
            // Handle the case where the server does not exist
            throw new RuntimeException("Server with ID " + serverId + " not found.");
        }
    }

    public Server getById(Long serverId) {
        return serverRepository.findById(serverId).orElseThrow(() -> new RuntimeException("Server with ID " + serverId + " not found."));
    }
}
