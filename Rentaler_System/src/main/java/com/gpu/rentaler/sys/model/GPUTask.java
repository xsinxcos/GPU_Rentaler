package com.gpu.rentaler.sys.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.time.Instant;

@DynamicInsert
@Entity
@Table(name = "gpu_task", schema = "gpu_rentaler_0")
public class GPUTask extends BaseEntity{

    private Long userId;

    private String deviceId;

    private Instant startTime;

    private Instant endTime;

    private BigDecimal plannedDurationHours;

    private BigDecimal actualDurationHours;

    private BigDecimal hourlyRate;

    private BigDecimal totalCost;

    private String status;

    private String containerId;

    private String containerName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getPlannedDurationHours() {
        return plannedDurationHours;
    }

    public void setPlannedDurationHours(BigDecimal plannedDurationHours) {
        this.plannedDurationHours = plannedDurationHours;
    }

    public BigDecimal getActualDurationHours() {
        return actualDurationHours;
    }

    public void setActualDurationHours(BigDecimal actualDurationHours) {
        this.actualDurationHours = actualDurationHours;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
}
