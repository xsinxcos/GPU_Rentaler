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

    private String realDeviceId;

    private Long serverId;

    private Integer deviceIndex;


    private String brand;

    private String model;

    private String architecture;

    private Long memoryTotal;

    private String memoryType;

    private String status;

    private Boolean isRentable;

    private BigDecimal totalRuntimeHours;


    private BigDecimal totalRevenue;

    private Integer rentalCount;

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
