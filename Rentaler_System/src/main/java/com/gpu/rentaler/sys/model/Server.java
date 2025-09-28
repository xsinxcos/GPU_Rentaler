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

@Entity
@Table(name = "servers", schema = "gpu_rentaler_0")
public class Server extends BaseEntity {

    @Size(max = 64)
    @NotNull
    @Column(name = "server_id", nullable = false, length = 64)
    private String serverId;

    @Size(max = 128)
    @NotNull
    @Column(name = "hostname", nullable = false, length = 128)
    private String hostname;

    @Size(max = 45)
    @NotNull
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Size(max = 128)
    @Column(name = "location", length = 128)
    private String location;

    @Size(max = 128)
    @Column(name = "cpu_model", length = 128)
    private String cpuModel;

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column(name = "ram_total_gb")
    private Integer ramTotalGb;

    @Column(name = "storage_total_gb")
    private Integer storageTotalGb;

    @Column(name = "gpu_slots")
    private Integer gpuSlots;

    @ColumnDefault("'online'")
    @Lob
    @Column(name = "status")
    private String status;

    @Column(name = "load_average", precision = 4, scale = 2)
    private BigDecimal loadAverage;

    @Column(name = "cpu_usage", precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    @Column(name = "memory_usage", precision = 5, scale = 2)
    private BigDecimal memoryUsage;

    @Column(name = "disk_usage", precision = 5, scale = 2)
    private BigDecimal diskUsage;

    @Column(name = "bandwidth_mbps")
    private Integer bandwidthMbps;

    @Size(max = 64)
    @Column(name = "datacenter", length = 64)
    private String datacenter;

    @Size(max = 64)
    @Column(name = "region", length = 64)
    private String region;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time")
    private Instant createdTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_time")
    private Instant updatedTime;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Integer getRamTotalGb() {
        return ramTotalGb;
    }

    public void setRamTotalGb(Integer ramTotalGb) {
        this.ramTotalGb = ramTotalGb;
    }

    public Integer getStorageTotalGb() {
        return storageTotalGb;
    }

    public void setStorageTotalGb(Integer storageTotalGb) {
        this.storageTotalGb = storageTotalGb;
    }

    public Integer getGpuSlots() {
        return gpuSlots;
    }

    public void setGpuSlots(Integer gpuSlots) {
        this.gpuSlots = gpuSlots;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(BigDecimal loadAverage) {
        this.loadAverage = loadAverage;
    }

    public BigDecimal getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(BigDecimal cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public BigDecimal getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(BigDecimal memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public BigDecimal getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(BigDecimal diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Integer getBandwidthMbps() {
        return bandwidthMbps;
    }

    public void setBandwidthMbps(Integer bandwidthMbps) {
        this.bandwidthMbps = bandwidthMbps;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
