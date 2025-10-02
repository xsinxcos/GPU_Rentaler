package com.gpu.rentaler.controller;


import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.dto.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/gpu")
public class GPUDeviceController {
    @Resource
    private GPUDeviceService gpuDeviceService;

    @RequiresPermissions("gpu:release")
    @DeleteMapping("/{deviceId}/release")
    public ResponseEntity<Void> deleteGPUDevice(@PathVariable String deviceId) {
        gpuDeviceService.deleteByDeviceId(deviceId);
        return ResponseEntity.noContent().build();
    }

    @RequiresPermissions("gpu:view")
    @GetMapping("/devices")
    public ResponseEntity<PageDTO<GPUDeviceDTO>> findGPUDevices(Pageable pageable,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) Long serverId) {
        PageDTO<GPUDeviceDTO> res = gpuDeviceService.findGPUDevices(pageable, status, serverId);
        return ResponseEntity.ok(res);
    }

    @RequiresPermissions("gpu:isRentable:view")
    @GetMapping("/rentable-devices")
    public ResponseEntity<PageDTO<RentableGPUDeviceDTO>> findRentableGPUCanDevice(Pageable pageable) {
        PageDTO<RentableGPUDeviceDTO> dto = gpuDeviceService.findRentableGPUCanDevice(pageable);
        return ResponseEntity.ok(dto);
    }

    @RequiresPermissions("gpu:modify")
    @PostMapping("/{deviceId}/devices")
    public ResponseEntity<Void> updateGPUDevice(@PathVariable String deviceId, @RequestBody GPUDeviceDTO gpuDeviceDTO) {
        gpuDeviceService.updateGPUDeviceByDeviceId(deviceId, gpuDeviceDTO.deviceIndex(), gpuDeviceDTO.brand(),
            gpuDeviceDTO.model(), gpuDeviceDTO.memoryTotal(), gpuDeviceDTO.architecture(), gpuDeviceDTO.memoryType(), gpuDeviceDTO.status(),
            gpuDeviceDTO.isRentable(), gpuDeviceDTO.hourlyRate(), gpuDeviceDTO.totalRuntimeHours(), gpuDeviceDTO.totalRevenue());

        return ResponseEntity.noContent().build();
    }


    @RequiresPermissions("gpu:lease")
    @PostMapping("/{deviceId}/lease")
    public ResponseEntity<GPUDeviceCertificateDTO> leaseGPUDevice(@PathVariable String deviceId) {
        // 租用GPU设备的逻辑
        gpuDeviceService.leaseDevice(deviceId);
        // todo 生成租借表
        return ResponseEntity.ok(new GPUDeviceCertificateDTO("1", "test", "test", "127.0.0.1:2222"));
    }

    @RequiresPermissions("gpu:return")
    @PostMapping("/{deviceId}/return")
    public ResponseEntity<Void> returnGPUDevice(@PathVariable String deviceId) {
        // 归还GPU设备的逻辑
        gpuDeviceService.returnDevice(deviceId);
        // todo 完善租借表
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
