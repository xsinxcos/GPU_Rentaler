package com.gpu.rentaler.sys.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "servers", schema = "gpu_rentaler_0")
public class Server extends BaseEntity {
    private String hostname;

    private String ipAddress;

    private String location;

    private String cpuModel;

    private Integer cpuCores;

    private Integer ramTotalGb;

    private Integer storageTotalGb;

    private Integer gpuSlots;

    private String status;

    private Integer bandwidthMbps;

    private String datacenter;

    private String region;

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

}
