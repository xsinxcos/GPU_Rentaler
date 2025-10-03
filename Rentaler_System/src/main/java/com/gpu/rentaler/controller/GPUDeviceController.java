package com.gpu.rentaler.controller;


import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.entity.VirtulBoxResInfo;
import com.gpu.rentaler.sys.constant.RantalStatus;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.GPURantals;
import com.gpu.rentaler.sys.monitor.DeviceTaskService;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.GPURantalsService;
import com.gpu.rentaler.sys.service.SessionService;
import com.gpu.rentaler.sys.service.dto.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/gpu")
public class GPUDeviceController {
    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private GPURantalsService gpuRantalsService;

    @Resource
    private SessionService sessionService;

    @Resource
    private DeviceTaskService deviceTaskService;

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
    public ResponseEntity<GPUDeviceCertificateDTO> leaseGPUDevice(HttpServletRequest request ,String storageId, @PathVariable String deviceId) {
        // 租用GPU设备的逻辑
        gpuDeviceService.leaseDevice(deviceId);
        GPUDevice device = gpuDeviceService.getByDeviceId(deviceId);

        String token = request.getHeader("Authorization").replace("Bearer", "").trim();
        UserinfoDTO userInfo = sessionService.getLoginUserInfo(token);

        VirtulBoxResInfo container = deviceTaskService.createDockerContainer(device.getServerId(), List.of(deviceId));

        GPURantals rantals = gpuRantalsService.saveGPURental(deviceId, userInfo.userId(), Instant.now(), device.getHourlyRate(), RantalStatus.ACTIVE,
            container.getContainerId(), container.getIp() + ":" + container.getPort(), container.getSshName(), container.getSshPassword());

        GPUDeviceCertificateDTO dto = new GPUDeviceCertificateDTO(deviceId, rantals.getSshUsername(), rantals.getSshPassword(), rantals.getSshHost());
        return ResponseEntity.ok(dto);
    }

    @RequiresPermissions("gpu:return")
    @PostMapping("/{deviceId}/return")
    public ResponseEntity<Void> returnGPUDevice(@PathVariable String deviceId) {
        // 归还GPU设备的逻辑
        gpuDeviceService.returnDevice(deviceId);
        // todo 完善租借表
        return ResponseEntity.noContent().build();
    }

}
