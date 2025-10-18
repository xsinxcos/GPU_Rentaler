package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.sys.model.GPUProcessActivity;
import com.gpu.rentaler.sys.model.GPURealDevices;
import com.gpu.rentaler.sys.model.GPUTask;
import com.gpu.rentaler.sys.model.TaskBilled;
import com.gpu.rentaler.sys.service.*;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
public class BillingSettleService {
    @Resource
    private GPUTaskService gpuTaskService;

    @Resource
    private TaskBilledService taskBilledService;

    @Resource
    private WalletService walletService;

    @Resource
    private GPUProcessActivityService activityService;

    @Resource
    private DeviceTaskService deviceTaskService;

    @Resource
    private GPURealDevicesService gpuRealDevicesService;

    /**
     * 一分钟 结算一次费用
     */
    @Scheduled(fixedRate = 10 * 1000) // 10秒
    public void executeTask() {
        List<GPUTask> allRunningTask = gpuTaskService.getAllRunningTask();
        for (GPUTask task : allRunningTask) {
            Optional<TaskBilled> taskBilled = taskBilledService.getLastByTaskId(task.getId());
            Instant time = task.getStartTime();
            if (taskBilled.isPresent()) {
                time = taskBilled.get().getEndBillTime();
            }
            List<GPUProcessActivity> allAfterTime = activityService.getAllAfterTime(task.getDeviceId(), task.getContainerId().substring(0, 12), time);
            if (allAfterTime.isEmpty()) continue;
            List<GPUProcessActivity> activities = keepUniqueByRecordId(allAfterTime);
            long costTimeSecond = costTimeSecond(activities);
            BigDecimal cost = getCostByHourlyRate(task.getHourlyRate(), costTimeSecond);

            BigDecimal nowBalance = walletService.cost(task.getUserId(), cost);

            Instant beginTime = getBeginTime(activities);
            Instant endTime = getEndTime(activities);
            taskBilledService.save(task.getUserId(), task.getId(), beginTime, endTime, cost);

            // 欠费超过 20 ，强制停止任务
            if (nowBalance.compareTo(new BigDecimal(20)) < 0) {
                gpuTaskService.forceCancelTask(task.getId());
                GPURealDevices device = gpuRealDevicesService.getByDeviceId(task.getDeviceId());
                // 停止容器
                deviceTaskService.stopContainer(device.getServerId(), task.getContainerId());
            }
        }
    }


    private Instant getBeginTime(List<GPUProcessActivity> activities) {
        GPUProcessActivity min = activities.getFirst();
        for (GPUProcessActivity activity : activities) {
            if (activity.getTime().compareTo(min.getTime()) < 0) {
                min = activity;
            }
        }
        return min.getTime().minusSeconds(min.getDuration());
    }

    private Instant getEndTime(List<GPUProcessActivity> activities) {
        GPUProcessActivity max = activities.getFirst();
        for (GPUProcessActivity activity : activities) {
            if (activity.getTime().compareTo(max.getTime()) > 0) {
                max = activity;
            }
        }
        return max.getTime();
    }

    /**
     * 根据每小时费率和秒数计算总费用，结果保留小数点后4位
     *
     * @param hourlyRate 每小时费率
     * @param second     时长（秒）
     * @return 总费用，精确到小数点后4位
     */
    private BigDecimal getCostByHourlyRate(BigDecimal hourlyRate, long second) {
        // 参数验证
        if (hourlyRate == null) {
            throw new IllegalArgumentException("每小时费率不能为null");
        }
        if (hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("每小时费率不能为负数");
        }
        if (second < 0) {
            throw new IllegalArgumentException("时长（秒）不能为负数");
        }

        // 特殊情况处理：时长为0或费率为0时，费用为0
        if (second == 0 || hourlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }

        // 将秒转换为小时：秒数 / 3600
        BigDecimal hours = new BigDecimal(second)
            .divide(new BigDecimal(3600), 10, RoundingMode.HALF_UP);

        // 计算总费用：每小时费率 * 小时数
        BigDecimal totalCost = hourlyRate.multiply(hours);

        // 保留4位小数，使用HALF_UP舍入模式
        return totalCost.setScale(4, RoundingMode.HALF_UP);
    }


    private long costTimeSecond(List<GPUProcessActivity> activities) {
        long sum = 0;
        for (GPUProcessActivity activity : activities) {
            sum += activity.getDuration();
        }
        return sum;
    }

    private List<GPUProcessActivity> keepUniqueByRecordId(List<GPUProcessActivity> allAfterTime) {
        // 创建一个HashMap用于存储去重后的结果，key为recordId，value为对应的GPUProcessActivity
        Map<String, GPUProcessActivity> uniqueMap = new HashMap<>();

        // 遍历列表，将元素放入map中，相同recordId会自动覆盖，保留最后一个出现的
        for (GPUProcessActivity activity : allAfterTime) {
            uniqueMap.put(activity.getRecordId(), activity);
        }

        // 将map中的值转换为列表并返回
        return new ArrayList<>(uniqueMap.values());
    }
}
