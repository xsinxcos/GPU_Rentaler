package com.gpu.rentaler.controller;

import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.ServerDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping
@RestController
public class ServerController {

    @RequiresPermissions("server:view")
    @GetMapping("/servers")
    public ResponseEntity<PageDTO<ServerDTO>> findServers() {
        //todo
        // 获取服务器列表的逻辑
        return ResponseEntity.ok(new PageDTO<>(null, 0));
    }

    @RequiresPermissions("server:modify")
    @PostMapping("/{serverId}/modify")
    public ResponseEntity<Void> modifyServers(@PathVariable Long serverId, @RequestBody ServerDTO serverDTO) {
        //todo
        // 刷新服务器列表的逻辑
        return ResponseEntity.noContent().build();
    }

    @RequiresPermissions("server:delete")
    @DeleteMapping("/{serverId}/delete")
    public ResponseEntity<Void> deleteServers(@PathVariable Long serverId) {
        //todo
        // 删除服务器的逻辑
        return ResponseEntity.noContent().build();
    }


}
