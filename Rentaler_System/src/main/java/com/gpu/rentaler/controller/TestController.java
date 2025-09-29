package com.gpu.rentaler.controller;

import com.gpu.rentaler.infra.service.DubboDynamicInvoker;
import com.gpu.rentaler.TaskAssignService;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.entity.DockerCreateResInfo;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.monitor.DeviceTaskService;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private DeviceTaskService deviceTaskService;

    @GetMapping
    public void test() {
        deviceTaskService.createDockerContainer(45L);
    }
}
