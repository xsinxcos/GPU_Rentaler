package com.gpu.rentaler.controller;

import com.gpu.rentaler.common.Constants;
import com.gpu.rentaler.common.SessionItemHolder;
import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.model.Resource;
import com.gpu.rentaler.sys.service.ResourceService;
import com.gpu.rentaler.sys.service.dto.MenuResourceDTO;
import com.gpu.rentaler.sys.service.dto.ResourceTreeDTO;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cjbi
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/menu")
    public ResponseEntity<List<MenuResourceDTO>> findMenus() {
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        return ResponseEntity.ok(resourceService.findMenus(userInfo.permissions()));
    }

    @RequiresPermissions("resource:view")
    @GetMapping("/tree")
    public ResponseEntity<List<ResourceTreeDTO>> findResourceTree() {
        return ResponseEntity.ok(resourceService.findResourceTree());
    }

    @RequiresPermissions("resource:create")
    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody @Valid ResourceRequest request) {
        return new ResponseEntity<>(resourceService.createResource(request.name(), request.type(), request.url(), request.icon(), request.permission(), request.parentId()), HttpStatus.CREATED);
    }

    @RequiresPermissions("resource:update")
    @PutMapping("/{resourceId}")
    public ResponseEntity<Resource> updateResource(@PathVariable Long resourceId, @RequestBody @Valid ResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateResource(resourceId, request.name(), request.type(), request.url(), request.icon(), request.permission(), request.parentId()));
    }

    @RequiresPermissions("resource:delete")
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResourceById(resourceId);
        return ResponseEntity.noContent().build();
    }


    record ResourceRequest(@NotBlank String name,
                           @NotNull Resource.Type type,
                           String url,
                           String icon,
                           @NotBlank String permission,
                           @NotNull Long parentId) {

    }

}
