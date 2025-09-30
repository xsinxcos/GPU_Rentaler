package com.gpu.rentaler.controller;


import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.dto.GPUDeviceCertificateDTO;
import com.gpu.rentaler.sys.service.dto.GPUDeviceDTO;
import com.gpu.rentaler.sys.service.dto.GPURantalDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/gpu")
public class GPUDeviceController {

    @RequiresPermissions("gpu:release")
    @DeleteMapping("/release")
    public ResponseEntity<Void> deleteGPUDevice() {
        //todo
        // 释放GPU设备的逻辑
        return ResponseEntity.noContent().build();
    }

    @RequiresPermissions("gpu:view")
    @GetMapping("/{gpuId}/devices")
    public ResponseEntity<PageDTO<GPUDeviceDTO>> findGPUDevices(Pageable pageable,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) Long serverId) {
        //todo
        // 获取GPU设备的逻辑
        return ResponseEntity.ok(new PageDTO<>(new ArrayList<>(), 0));
    }

    @RequiresPermissions("gpu:isRentable:view")
    @GetMapping("/rentable-devices")
    public ResponseEntity<PageDTO<GPUDeviceDTO>> findRentableGPUCanDevice(Pageable pageable) {
        //todo
        // 获取可租用的GPU设备的逻辑
        return ResponseEntity.ok(new PageDTO<>(new ArrayList<>(), 0));
    }

    @RequiresPermissions("gpu:modify")
    @PostMapping("/{deviceId}/devices")
    public ResponseEntity<Void> updateGPUDevice(@PathVariable String deviceId, @RequestBody GPUDeviceDTO gpuDeviceDTO) {
        //todo
        // 更新GPU设备的逻辑
        return ResponseEntity.noContent().build();
    }


    @RequiresPermissions("gpu:lease")
    @PostMapping("/{deviceId}/lease")
    public ResponseEntity<GPUDeviceCertificateDTO> leaseGPUDevice(@PathVariable String deviceId) {
        //todo
        // 租用GPU设备的逻辑
        return ResponseEntity.ok(new GPUDeviceCertificateDTO("1", "test", "test", "127.0.0.1:2222"));
    }

    @RequiresPermissions("gpu:return")
    @PostMapping("/{deviceId}/return")
    public ResponseEntity<Void> returnGPUDevice(@PathVariable String deviceId) {
        //todo
        // 归还GPU设备的逻辑
        return ResponseEntity.noContent().build();
    }

    @RequiresPermissions("gpu:mylease")
    @PostMapping("/mylease")
    public ResponseEntity<PageDTO<GPURantalDTO>> findMyLeaseGPUDevices(Pageable pageable) {
        //todo
        // 获取我租用的GPU设备的逻辑
        return ResponseEntity.ok(new PageDTO<>(new ArrayList<>(), 0));
    }


    @RequiresPermissions("gpu:alllease")
    @GetMapping("/alllease")
    public ResponseEntity<PageDTO<GPURantalDTO>> findAllLeaseGPUDevices(Pageable pageable) {
        //todo
        // 获取所有租用的GPU设备的逻辑
        return ResponseEntity.ok(new PageDTO<>(new ArrayList<>(), 0));
    }
}
