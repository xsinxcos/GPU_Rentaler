package com.gpu.rentaler.entity;

import java.io.Serializable;

public class VirtulBoxResInfo implements Serializable {
    private String containerId;
    private String ip;
    private String port;
    private String sshName;
    private String sshPassword;


    public VirtulBoxResInfo() {
    }

    public VirtulBoxResInfo(String containerId, String ip, String port, String sshName, String sshPassword) {
        this.containerId = containerId;
        this.ip = ip;
        this.port = port;
        this.sshName = sshName;
        this.sshPassword = sshPassword;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSshName() {
        return sshName;
    }

    public void setSshName(String sshName) {
        this.sshName = sshName;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
