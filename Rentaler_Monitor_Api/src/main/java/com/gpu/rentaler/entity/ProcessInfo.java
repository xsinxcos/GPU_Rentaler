package com.gpu.rentaler.entity;

import java.io.Serializable;
import java.time.Instant;

public class ProcessInfo implements Serializable {
    String pid;
    String name;
    String deviceId;
    String usedMemoryMB;
    String containerId; // Optional: Docker container ID if applicable
    Instant time = Instant.now();

    public ProcessInfo(String pid, String name, String deviceId, String usedMemoryMB, String containerId) {
        this.pid = pid;
        this.name = name;
        this.deviceId = deviceId;
        this.usedMemoryMB = usedMemoryMB;
        this.containerId = containerId;
    }

    public ProcessInfo() {
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsedMemoryMB() {
        return usedMemoryMB;
    }

    public void setUsedMemoryMB(String usedMemoryMB) {
        this.usedMemoryMB = usedMemoryMB;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Instant getTime() {
        return time;
    }
}
