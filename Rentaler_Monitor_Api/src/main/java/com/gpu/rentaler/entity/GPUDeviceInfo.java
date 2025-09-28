package com.gpu.rentaler.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GPU设备信息实体类
 */
public class GPUDeviceInfo implements Serializable {
    private String deviceId;
    private Long serverId;
    private Integer deviceIndex;
    private String brand;
    private String model;
    private String architecture;
    private Long memoryTotal;
    private String memoryType;
    private Integer cudaCores;
    private Integer tensorCores;
    private Integer baseClock;
    private Integer boostClock;
    private String status = "active";
    private BigDecimal healthScore = new BigDecimal("100.00");
    private Integer temperatureLimit = 83;
    private Integer powerLimit;

    // 构造函数
    public GPUDeviceInfo() {}

    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getServerId() { return serverId; }
    public void setServerId(Long serverId) { this.serverId = serverId; }

    public Integer getDeviceIndex() { return deviceIndex; }
    public void setDeviceIndex(Integer deviceIndex) { this.deviceIndex = deviceIndex; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getArchitecture() { return architecture; }
    public void setArchitecture(String architecture) { this.architecture = architecture; }

    public Long getMemoryTotal() { return memoryTotal; }
    public void setMemoryTotal(Long memoryTotal) { this.memoryTotal = memoryTotal; }

    public String getMemoryType() { return memoryType; }
    public void setMemoryType(String memoryType) { this.memoryType = memoryType; }

    public Integer getCudaCores() { return cudaCores; }
    public void setCudaCores(Integer cudaCores) { this.cudaCores = cudaCores; }

    public Integer getTensorCores() { return tensorCores; }
    public void setTensorCores(Integer tensorCores) { this.tensorCores = tensorCores; }

    public Integer getBaseClock() { return baseClock; }
    public void setBaseClock(Integer baseClock) { this.baseClock = baseClock; }

    public Integer getBoostClock() { return boostClock; }
    public void setBoostClock(Integer boostClock) { this.boostClock = boostClock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getHealthScore() { return healthScore; }
    public void setHealthScore(BigDecimal healthScore) { this.healthScore = healthScore; }

    public Integer getTemperatureLimit() { return temperatureLimit; }
    public void setTemperatureLimit(Integer temperatureLimit) { this.temperatureLimit = temperatureLimit; }

    public Integer getPowerLimit() { return powerLimit; }
    public void setPowerLimit(Integer powerLimit) { this.powerLimit = powerLimit; }

    @Override
    public String toString() {
        return String.format(
            "GPUDeviceInfo{deviceId='%s', serverId=%d, deviceIndex=%d, brand='%s', model='%s', " +
            "architecture='%s', memoryTotal=%d MB, memoryType='%s', cudaCores=%d, tensorCores=%d, " +
            "baseClock=%d MHz, boostClock=%d MHz, status='%s', healthScore=%s, " +
            "temperatureLimit=%d°C, powerLimit=%d W}",
            deviceId, serverId, deviceIndex, brand, model, architecture, memoryTotal, memoryType,
            cudaCores, tensorCores, baseClock, boostClock, status, healthScore, temperatureLimit, powerLimit
        );
    }
}
