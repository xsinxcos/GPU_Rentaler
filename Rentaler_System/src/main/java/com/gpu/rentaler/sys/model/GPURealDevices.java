package com.gpu.rentaler.sys.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "gpu_real_devices", schema = "gpu_rentaler_0")
public class GPURealDevices extends BaseEntity {

    @Size(max = 64)
    @NotNull
    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @Size(max = 64)
    @NotNull
    @Column(name = "real_device_id", nullable = false, length = 64)
    private String realDeviceId;

    @NotNull
    @Column(name = "server_id", nullable = false)
    private Long serverId;

    @NotNull
    @Column(name = "device_index", nullable = false)
    private Integer deviceIndex;

    @Size(max = 20)
    @NotNull
    @Column(name = "brand", nullable = false, length = 20)
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

    @Size(max = 20)
    @ColumnDefault("'ONLINE'")
    @Column(name = "status", length = 20)
    private String status;

    @ColumnDefault("1")
    @Column(name = "is_rentable")
    private Boolean isRentable;

    @ColumnDefault("0.00")
    @Column(name = "hourly_rate", precision = 38, scale = 2)
    private BigDecimal hourlyRate;

    @ColumnDefault("0.00")
    @Column(name = "total_runtime_hours", precision = 38, scale = 2)
    private BigDecimal totalRuntimeHours;

    @ColumnDefault("0.00")
    @Column(name = "total_revenue", precision = 38, scale = 2)
    private BigDecimal totalRevenue;

    @ColumnDefault("0")
    @Column(name = "rental_count")
    private Integer rentalCount;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRealDeviceId() {
        return realDeviceId;
    }

    public void setRealDeviceId(String realDeviceId) {
        this.realDeviceId = realDeviceId;
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

}
