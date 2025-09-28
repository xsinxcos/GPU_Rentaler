package com.gpu.rentaler.controller;

import com.gpu.rentaler.sys.monitor.GPUMonitorService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private GPUMonitorService gpuMonitorService;

    @GetMapping
    public void test(String command) {
//        gpuMonitorService.query(command);
    }
}
