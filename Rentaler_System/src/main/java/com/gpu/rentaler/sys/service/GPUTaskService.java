package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.constant.TaskStatus;
import com.gpu.rentaler.sys.model.GPUTask;
import com.gpu.rentaler.sys.repository.GPUTaskRepository;
import com.gpu.rentaler.sys.service.dto.GPUTaskDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class GPUTaskService {
    @Resource
    private GPUTaskRepository gpuTaskRepository;


    // 方法定义处修改参数名
    public GPUTask initGPUTask(String deviceId, Long userId, Instant rentalStartTime,
                               BigDecimal hourlyRate, String rentalStatus) {
        GPUTask gpuRental = new GPUTask();
        gpuRental.setDeviceId(deviceId);
        gpuRental.setUserId(userId);
        gpuRental.setStartTime(rentalStartTime);
        gpuRental.setHourlyRate(hourlyRate);
        gpuRental.setStatus(rentalStatus);

        // 持久化操作
        return gpuTaskRepository.save(gpuRental);
    }

    // 方法定义处修改参数名
    public void runningGPUTask(Long id ,String status, String containerId ,String containerName) {
        Optional<GPUTask> gpuTask = gpuTaskRepository.findById(id);
        gpuTask.ifPresent(item -> {
            item.setStatus(status);
            item.setContainerId(containerId);
            item.setContainerName(containerName);
            gpuTaskRepository.save(item);
        });
    }





    public PageDTO<GPUTaskDTO> findByUserId(Pageable pageable, Long userId, String status) {
        Page<GPUTask> gpuTasks = gpuTaskRepository.findPageByUserIdAndStatus(userId, status, pageable);
        List<GPUTaskDTO> dtos = gpuTasks.stream()
            .map(task -> new GPUTaskDTO(
                task.getId(),
                task.getUserId(),
                task.getDeviceId(),
                task.getStartTime(),
                task.getEndTime(),
                task.getActualDurationHours(),
                task.getHourlyRate(),
                task.getTotalCost(),
                task.getStatus()
            ))
            .toList();
        return new PageDTO<>(dtos, gpuTasks.getTotalElements());
    }

    public PageDTO<GPUTaskDTO> findByAll(Pageable pageable, String status) {
        Page<GPUTask> gpuTasks = gpuTaskRepository.findPageByStatus(status, pageable);
        List<GPUTaskDTO> dtos = gpuTasks.stream()
            .map(task -> new GPUTaskDTO(
                task.getId(),
                task.getUserId(),
                task.getDeviceId(),
                task.getStartTime(),
                task.getEndTime(),
                task.getActualDurationHours(),
                task.getHourlyRate(),
                task.getTotalCost(),
                task.getStatus()
            ))
            .toList();
        return new PageDTO<>(dtos, gpuTasks.getTotalElements());
    }


    public Optional<GPUTask> getById(Long id) {
        return gpuTaskRepository.findById(id);
    }

    public void finishTask(Long taskId ,BigDecimal sumCost) {
        Optional<GPUTask> gpuTask = gpuTaskRepository.findById(taskId);
        if (gpuTask.isPresent()) {
            GPUTask task = gpuTask.get();
            Instant endTime = Instant.now();
            task.setEndTime(endTime);
            task.setActualDurationHours(getDur(task.getStartTime() ,task.getEndTime()));
            task.setStatus(TaskStatus.COMPLETED);
            task.setTotalCost(sumCost);
            gpuTaskRepository.save(task);
        }
    }

    public void forceCancelTask(Long taskId) {
        Optional<GPUTask> gpuTask = gpuTaskRepository.findById(taskId);
        if (gpuTask.isPresent()) {
            GPUTask task = gpuTask.get();
            Instant endTime = Instant.now();
            task.setEndTime(endTime);
            task.setActualDurationHours(getDur(task.getStartTime() ,task.getEndTime()));
            task.setStatus(TaskStatus.CANCELLED);
            gpuTaskRepository.save(task);
        }
    }

    private BigDecimal getDur(Instant t1, Instant t2) {
        Duration duration = Duration.between(t1, t2);

        BigDecimal seconds = BigDecimal.valueOf(duration.getSeconds())
            .add(BigDecimal.valueOf(duration.getNano(), 9));
        return  seconds.divide(BigDecimal.valueOf(3600), 9, RoundingMode.HALF_UP);
    }

    public List<GPUTask> getAllRunningTask(){
        return gpuTaskRepository.findByStatus(TaskStatus.ACTIVE);
    }
}
