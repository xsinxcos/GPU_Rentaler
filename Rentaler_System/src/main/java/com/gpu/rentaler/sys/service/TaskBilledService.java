package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.model.TaskBilled;
import com.gpu.rentaler.sys.repository.TaskBilledRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TaskBilledService {
    private final TaskBilledRepository taskBilledRepository;

    public TaskBilledService(TaskBilledRepository taskBilledRepository) {
        this.taskBilledRepository = taskBilledRepository;
    }

    public Optional<TaskBilled> getLastByTaskId(Long taskId){
        return taskBilledRepository.findFirstByTaskIdOrderByEndBillTimeDesc(taskId);
    }

    public void save(Long userId , Long taskId, Instant startTime, Instant endTime , BigDecimal cost){
        TaskBilled taskBilled = new TaskBilled();
        taskBilled.setUserId(userId);
        taskBilled.setTaskId(taskId);
        taskBilled.setCost(cost);
        taskBilled.setStartBillTime(startTime);
        taskBilled.setEndBillTime(endTime);
        taskBilledRepository.save(taskBilled);
    }

    public BigDecimal getAllCostByTaskId(Long taskId){
        BigDecimal sum = new BigDecimal(0);
        List<TaskBilled> byTaskId = taskBilledRepository.findTaskBilledByTaskId(taskId);
        for (TaskBilled taskBilled : byTaskId) {
            sum = sum.add(taskBilled.getCost());
        }
        return sum;
    }
}
