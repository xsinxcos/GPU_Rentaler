package com.gpu.rentaler.entity;

import java.io.Serializable;

public class DockerCreateResInfo implements Serializable {
    private String containerId;
    private String port;
    private String sshName;
    private String sshPassword;

    public DockerCreateResInfo(String containerId, String port, String sshName, String sshPassword) {
        this.containerId = containerId;
        this.port = port;
        this.sshName = sshName;
        this.sshPassword = sshPassword;
    }

    public DockerCreateResInfo() {
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
}
