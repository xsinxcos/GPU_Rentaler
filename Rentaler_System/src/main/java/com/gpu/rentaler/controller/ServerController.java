package com.gpu.rentaler.controller;

import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.ServerService;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.ServerDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping
@RestController
public class ServerController {
    @Resource
    private ServerService serverService;

    @RequiresPermissions("server:view")
    @GetMapping("/servers")
    public ResponseEntity<PageDTO<ServerDTO>> findServers(Pageable pageable) {
        PageDTO<ServerDTO> servers = serverService.findServers(pageable);
        return ResponseEntity.ok(servers);
    }

    @RequiresPermissions("server:modify")
    @PostMapping("/{serverId}/modify")
    public ResponseEntity<Void> modifyServers(@PathVariable Long serverId, @RequestBody ServerDTO serverDTO) {
        serverService.updateServerById(serverId,
            serverDTO.hostname(), serverDTO.ipAddress(), serverDTO.location(), serverDTO.cpuModel(), serverDTO.cpuCores()
            , serverDTO.ramTotalGb(), serverDTO.storageTotalGb(), serverDTO.gpuSlots(), serverDTO.status(), serverDTO.datacenter()
            , serverDTO.region());
        return ResponseEntity.noContent().build();
    }

    @RequiresPermissions("server:delete")
    @DeleteMapping("/{serverId}/delete")
    public ResponseEntity<Void> deleteServers(@PathVariable Long serverId) {
        serverService.deleteById(serverId);
        return ResponseEntity.noContent().build();
    }


}
