package com.gpu.rentaler.service;

import com.gpu.rentaler.MonitorService;
import com.gpu.rentaler.entity.ProcessInfo;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProcessTaskService {

    // 定义时间格式化器
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private GPUFactory gpuFactory;

    @Resource
    private MonitorService monitorService;

    @Resource
    private ServerIDManager serverIDManager;

    /**
     * 每5秒执行一次的定时任务
     * fixedRate: 以固定速率执行，即从上一次任务开始时间算起，间隔指定时间后再次执行
     */
    @Scheduled(fixedRate = 5000) // 5秒
    public void executeTask() {
        // 这里可以添加你的业务方法调用
        List<ProcessInfo> allGPUActivityInfo = gpuFactory.getAllGPUActivityInfo();
        Long serverId = serverIDManager.getServerId();
        monitorService.reportProcessMsg(serverId ,allGPUActivityInfo);
    }
}
