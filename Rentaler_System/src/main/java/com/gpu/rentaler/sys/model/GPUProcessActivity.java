package com.gpu.rentaler.sys.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
@DynamicInsert
@Entity
@Table(name = "gpu_process_activity", schema = "gpu_rentaler_0")
public class GPUProcessActivity extends BaseEntity{

    private String deviceId;

    private Long pid;

    private String processName;

    private Instant time;

    public GPUProcessActivity(String deviceId, Long pid, String processName, Instant time) {
        this.deviceId = deviceId;
        this.pid = pid;
        this.processName = processName;
        this.time = time;
    }

    public GPUProcessActivity() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

}
