package com.gpu.rentaler.entity;

import java.io.Serializable;

public class ProcessInfo implements Serializable {
    String pid;
    String name;
    String gpuUuid;
    String usedMemoryMB;

    @Override
    public String toString() {
        return "PID: " + pid + ", Name: " + name +
            ", GPU UUID: " + gpuUuid + ", Memory: " + usedMemoryMB + " MiB";
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

    public String getGpuUuid() {
        return gpuUuid;
    }

    public void setGpuUuid(String gpuUuid) {
        this.gpuUuid = gpuUuid;
    }

    public String getUsedMemoryMB() {
        return usedMemoryMB;
    }

    public void setUsedMemoryMB(String usedMemoryMB) {
        this.usedMemoryMB = usedMemoryMB;
    }
}
