package com.gpu.rentaler.controller;


import com.gpu.rentaler.common.Constants;
import com.gpu.rentaler.common.SessionItemHolder;
import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.entity.DContainerInfoResp;
import com.gpu.rentaler.sys.constant.RantalStatus;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.GPURantals;
import com.gpu.rentaler.sys.monitor.DeviceTaskService;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.GPURantalsService;
import com.gpu.rentaler.sys.service.StorageService;
import com.gpu.rentaler.sys.service.dto.GPUDeviceDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.RentableGPUDeviceDTO;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/gpu")
public class GPUDeviceController {
    private static final Logger log = LogManager.getLogger(GPUDeviceController.class);
    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private GPURantalsService gpuRantalsService;

    @Resource
    private DeviceTaskService deviceTaskService;

    @Resource
    private StorageService storageService;

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
    public ResponseEntity<LeaseGPUDeviceDTO> leaseGPUDevice(String key, @PathVariable String deviceId) {
        // 租用GPU设备的逻辑
        Optional<GPUDevice> lease = gpuDeviceService.lease(deviceId);
        final LeaseGPUDeviceDTO[] dto = new LeaseGPUDeviceDTO[1];
        lease.ifPresentOrElse(device -> {
            UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
            asyncExecute(() -> {
                org.springframework.core.io.Resource resource = storageService.loadAsResource(key);
                try {
                    InputStream inputStream = resource.getInputStream();
                    List<String> devices = new ArrayList<>();
                    devices.add(deviceId);
                    DContainerInfoResp infoResp = deviceTaskService.exportAndUpDockerImage(
                        inputStream, device.getServerId(), devices);
                    gpuRantalsService.saveGPURental(deviceId, userInfo.userId(), Instant.now(), device.getHourlyRate(), RantalStatus.ACTIVE,
                        infoResp.containerId(), infoResp.containerName());
                } catch (IOException e) {
                    log.warn("{} 镜像运行失败：{}", resource.getFilename(), e.getMessage());
                }
            });
            dto[0] = new LeaseGPUDeviceDTO(true, "成功租用");
        }, () -> {
            dto[0] = new LeaseGPUDeviceDTO(false, "已被租用，请重新选择设备");
        });
        return ResponseEntity.ok(dto[0]);
    }

    @Async("IOTaskExecutor")
    protected void asyncExecute(Runnable task) {
        task.run();
    }

    public record LeaseGPUDeviceDTO(boolean isSuccess, String msg) {
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
