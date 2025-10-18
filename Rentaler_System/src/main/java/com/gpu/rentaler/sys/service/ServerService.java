package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.constant.DeviceStatus;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.repository.ServerRepository;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.ServerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public PageDTO<ServerDTO> findServers(Pageable pageable) {
        Page<Server> servers = serverRepository.findServers(pageable);
        List<ServerDTO> serverDTOS = servers.get().map(item ->
                new ServerDTO(item.getId(), item.getHostname(), item.getIpAddress(), item.getLocation(),
                    item.getCpuModel(), item.getCpuCores(), item.getRamTotalGb(),
                    item.getStorageTotalGb(), item.getGpuSlots(), item.getStatus(),
                    item.getDatacenter() ,item.getRegion()))
            .toList();
        return new PageDTO<>(serverDTOS, servers.getTotalElements());
    }

    public void updateServerById(Long serverId, String hostname, String ipAddress, String location, String cpuModel,
                                 Integer cpuCores, Integer ramTotalGb, Integer storageTotalGb,
                                 Integer gpuSlots, String status , String datacenter , String region) {
        Server server = serverRepository.findById(serverId).orElseThrow(() ->
            new RuntimeException("Server with ID " + serverId + " not found."));
        server.setHostname(hostname);
        server.setIpAddress(ipAddress);
        server.setCpuModel(cpuModel);
        server.setCpuCores(cpuCores);
        server.setRamTotalGb(ramTotalGb);
        server.setStorageTotalGb(storageTotalGb);
        server.setGpuSlots(gpuSlots);
        server.setStatus(status);
        server.setLocation(location);
        server.setDatacenter(datacenter);
        server.setRegion(region);
        serverRepository.save(server);
    }

    public void deleteById(Long serverId) {
        serverRepository.deleteById(serverId);
    }

    public List<Long> getAllIds() {
        return serverRepository.findAll().stream().map(Server::getId).toList();
    }
}
