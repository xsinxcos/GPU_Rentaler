package com.gpu.rentaler.entity;

import java.io.Serializable;
import java.util.List;

public class ServerInfo implements Serializable {
    private String serverId;

    private String hostname;

    private String ipAddress;

    private String cpuModel;

    private int cpuCores;

    private int ramTotalGb;

    private int storageTotalGb;

    private int gpuSlots;

    private List<GPUDeviceInfo> gpuDeviceInfos;

    public ServerInfo() {

    }

    public ServerInfo(String serverId, String hostname, String ipAddress, String cpuModel, int cpuCores, int ramTotalGb, int storageTotalGb, int gpuSlots, List<GPUDeviceInfo> gpuDeviceInfos) {
        this.serverId = serverId;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.cpuModel = cpuModel;
        this.cpuCores = cpuCores;
        this.ramTotalGb = ramTotalGb;
        this.storageTotalGb = storageTotalGb;
        this.gpuSlots = gpuSlots;
        this.gpuDeviceInfos = gpuDeviceInfos;
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

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public int getRamTotalGb() {
        return ramTotalGb;
    }

    public void setRamTotalGb(int ramTotalGb) {
        this.ramTotalGb = ramTotalGb;
    }

    public int getStorageTotalGb() {
        return storageTotalGb;
    }

    public void setStorageTotalGb(int storageTotalGb) {
        this.storageTotalGb = storageTotalGb;
    }

    public int getGpuSlots() {
        return gpuSlots;
    }

    public void setGpuSlots(int gpuSlots) {
        this.gpuSlots = gpuSlots;
    }

    public List<GPUDeviceInfo> getGpuDeviceInfos() {
        return gpuDeviceInfos;
    }

    public void setGpuDeviceInfos(List<GPUDeviceInfo> gpuDeviceInfos) {
        this.gpuDeviceInfos = gpuDeviceInfos;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
