package com.gpu.rentaler.controller;

import com.gpu.rentaler.common.Constants;
import com.gpu.rentaler.common.SessionItemHolder;
import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.GPUTask;
import com.gpu.rentaler.sys.monitor.DeviceTaskService;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.GPUTaskService;
import com.gpu.rentaler.sys.service.ServerService;
import com.gpu.rentaler.sys.service.dto.GPUTaskDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/task")
public class TaskController {

    @Resource
    private GPUTaskService gpuTaskService;

    @Resource
    private DeviceTaskService deviceTaskService;

    @Resource
    private GPUDeviceService gpuDeviceService;

    @Resource
    private ServerService serverService;

    @RequiresPermissions("task:me")
    @GetMapping("/me")
    public ResponseEntity<PageDTO<GPUTaskDTO>> findMyTask(Pageable pageable, String status) {
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        PageDTO<GPUTaskDTO> dto = gpuTaskService.findByUserId(pageable, userInfo.userId(), status);
        return ResponseEntity.ok(dto);
    }


    @RequiresPermissions("task:all")
    @GetMapping("/all")
    public ResponseEntity<PageDTO<GPUTaskDTO>> getAllTask(Pageable pageable, String status) {
        PageDTO<GPUTaskDTO> dto = gpuTaskService.findByAll(pageable, status);
        return ResponseEntity.ok(dto);
    }

    @RequiresPermissions("task:log")
    @GetMapping("/log")
    public ResponseEntity<String> getLogs(int logNum, Long taskId) {
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        if (logNum >= 1000) {
            return ResponseEntity.badRequest().body("查看最新日志的数量不能大于 1000 条");
        }
        String res = "";
        Optional<GPUTask> gpuTask = gpuTaskService.getById(taskId);

        if (gpuTask.isPresent()) {
            GPUTask item = gpuTask.get();
            if (item.getUserId().equals(userInfo.userId())) {
                GPUDevice device = gpuDeviceService.getByDeviceId(item.getDeviceId());
                res = deviceTaskService.getLog(device.getServerId(), item.getContainerId(), logNum);
            }
        }
        return ResponseEntity.ok(res);
    }

    @RequiresPermissions("task:finish")
    @PostMapping("/finish")
    public ResponseEntity<Void> finishTask(Long taskId) {
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        Optional<GPUTask> gpuTask = gpuTaskService.getById(taskId);

        if (gpuTask.isPresent()) {
            GPUTask item = gpuTask.get();
            if (item.getUserId().equals(userInfo.userId())) {
                gpuTaskService.finishTask(taskId);
                GPUDevice device = gpuDeviceService.getByDeviceId(item.getDeviceId());
                deviceTaskService.stopContainer(device.getServerId(), item.getContainerId());
            }
        }
        return ResponseEntity.ok().build();
    }

    @RequiresPermissions("task:data:export")
    @PostMapping("/data/export")
    public ResponseEntity<org.springframework.core.io.Resource> exportData(@RequestBody ExportDataReq req) {
        Long taskId = req.taskId;
        String path = req.path;
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        Optional<GPUTask> gpuTask = gpuTaskService.getById(taskId);
        org.springframework.core.io.Resource resp = null;
        if (gpuTask.isPresent()) {
            GPUTask item = gpuTask.get();
            if (item.getUserId().equals(userInfo.userId())) {
                GPUDevice device = gpuDeviceService.getByDeviceId(item.getDeviceId());
                resp = deviceTaskService.exportContainerData(device.getServerId(), item.getContainerId(), path);
            }
        }
        return ResponseEntity.ok(resp);
    }

    public record ExportDataReq(Long taskId, String path) {
    }

}
