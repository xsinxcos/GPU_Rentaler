package com.gpu.rentaler.service;

import com.gpu.rentaler.MonitorService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TaskService {

    // 定义时间格式化器
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private GPUInfoCollector gpuInfoCollector;

//
//    /**
//     * 每2秒执行一次的定时任务
//     * fixedRate: 以固定速率执行，即从上一次任务开始时间算起，间隔指定时间后再次执行
//     */
//    @Scheduled(fixedRate = 200000) // 2000毫秒 = 2秒
//    public void executeTask() {
//        // 获取当前时间
//        String currentTime = LocalDateTime.now().format(formatter);
//
//        // 执行具体业务逻辑
//        System.out.println("定时任务执行时间: " + currentTime + "，这是每2秒执行一次的任务");
//
//        // 这里可以添加你的业务方法调用
//        // yourBusinessMethod();
//    }
}
