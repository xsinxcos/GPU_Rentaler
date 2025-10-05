package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.sys.model.GPUTask;
import com.gpu.rentaler.sys.service.GPUTaskService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingSettleService {
    @Resource
    private GPUTaskService gpuTaskService;

    @Resource
    private BillingSettleService billingSettleService;
    /**
     * 一分钟 结算一次费用
     */
    @Scheduled(fixedRate = 60 * 1000) // 10秒
    public void executeTask() {
        List<GPUTask> allRunningTask = gpuTaskService.getAllRunningTask();

    }
}
