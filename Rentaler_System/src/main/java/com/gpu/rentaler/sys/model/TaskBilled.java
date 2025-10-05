package com.gpu.rentaler.sys.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "task_billed", schema = "gpu_rentaler_0")
public class TaskBilled extends BaseEntity {

    private Long userId;

    private Long taskId;

    private BigDecimal cost;

    private Instant startBillTime;

    private Instant endBillTime;

    public Instant getEndBillTime() {
        return endBillTime;
    }

    public void setEndBillTime(Instant endBillTime) {
        this.endBillTime = endBillTime;
    }

    public Instant getStartBillTime() {
        return startBillTime;
    }

    public void setStartBillTime(Instant startBillTime) {
        this.startBillTime = startBillTime;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
