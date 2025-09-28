package com.gpu.rentaler.sys.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "gpu_devices", schema = "gpu_rentaler_0")
public class GPUDevice extends BaseEntity {

    @Size(max = 64)
    @NotNull
    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @NotNull
    @Column(name = "server_id", nullable = false)
    private Long serverId;

    @NotNull
    @Column(name = "device_index", nullable = false)
    private Integer deviceIndex;

    @NotNull
    @Lob
    @Column(name = "brand", nullable = false)
    private String brand;

    @Size(max = 128)
    @NotNull
    @Column(name = "model", nullable = false, length = 128)
    private String model;

    @Size(max = 64)
    @Column(name = "architecture", length = 64)
    private String architecture;

    @NotNull
    @Column(name = "memory_total", nullable = false)
    private Long memoryTotal;

    @Size(max = 32)
    @Column(name = "memory_type", length = 32)
    private String memoryType;

    @ColumnDefault("'active'")
    @Lob
    @Column(name = "status")
    private String status;

    @ColumnDefault("100.00")
    @Column(name = "health_score", precision = 5, scale = 2)
    private BigDecimal healthScore;

    @ColumnDefault("83")
    @Column(name = "temperature_limit")
    private Integer temperatureLimit;

    @Column(name = "power_limit")
    private Integer powerLimit;

    @ColumnDefault("1")
    @Column(name = "is_rentable")
    private Boolean isRentable;

    @NotNull
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal hourlyRate;

    @ColumnDefault("'standard'")
    @Lob
    @Column(name = "tier")
    private String tier;

    @ColumnDefault("1")
    @Column(name = "max_concurrent_users")
    private Integer maxConcurrentUsers;

    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;

    @Column(name = "next_maintenance")
    private LocalDate nextMaintenance;

    @Lob
    @Column(name = "maintenance_notes")
    private String maintenanceNotes;

    @ColumnDefault("0.00")
    @Column(name = "total_runtime_hours", precision = 12, scale = 2)
    private BigDecimal totalRuntimeHours;

    @ColumnDefault("0.0000")
    @Column(name = "total_revenue", precision = 15, scale = 4)
    private BigDecimal totalRevenue;

    @ColumnDefault("0")
    @Column(name = "rental_count")
    private Integer rentalCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time")
    private Instant createdTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_time")
    private Instant updatedTime;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Integer getDeviceIndex() {
        return deviceIndex;
    }

    public void setDeviceIndex(Integer deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(Long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(BigDecimal healthScore) {
        this.healthScore = healthScore;
    }

    public Integer getTemperatureLimit() {
        return temperatureLimit;
    }

    public void setTemperatureLimit(Integer temperatureLimit) {
        this.temperatureLimit = temperatureLimit;
    }

    public Integer getPowerLimit() {
        return powerLimit;
    }

    public void setPowerLimit(Integer powerLimit) {
        this.powerLimit = powerLimit;
    }

    public Boolean getIsRentable() {
        return isRentable;
    }

    public void setIsRentable(Boolean isRentable) {
        this.isRentable = isRentable;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Integer getMaxConcurrentUsers() {
        return maxConcurrentUsers;
    }

    public void setMaxConcurrentUsers(Integer maxConcurrentUsers) {
        this.maxConcurrentUsers = maxConcurrentUsers;
    }

    public LocalDate getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(LocalDate lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public LocalDate getNextMaintenance() {
        return nextMaintenance;
    }

    public void setNextMaintenance(LocalDate nextMaintenance) {
        this.nextMaintenance = nextMaintenance;
    }

    public String getMaintenanceNotes() {
        return maintenanceNotes;
    }

    public void setMaintenanceNotes(String maintenanceNotes) {
        this.maintenanceNotes = maintenanceNotes;
    }

    public BigDecimal getTotalRuntimeHours() {
        return totalRuntimeHours;
    }

    public void setTotalRuntimeHours(BigDecimal totalRuntimeHours) {
        this.totalRuntimeHours = totalRuntimeHours;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(Integer rentalCount) {
        this.rentalCount = rentalCount;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Instant updatedTime) {
        this.updatedTime = updatedTime;
    }

}
