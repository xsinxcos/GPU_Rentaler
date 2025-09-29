package com.gpu.rentaler.entity;

public class ContainerInfo {
    private String containerId;
    private String port;
    private String sshName;
    private String sshPassword;

    public ContainerInfo(String containerId, String port, String sshName, String sshPassword) {
        this.containerId = containerId;
        this.port = port;
        this.sshName = sshName;
        this.sshPassword = sshPassword;
    }

    // Getter & Setter
    public String getContainerId() { return containerId; }
    public String getPort() { return port; }
    public String getSshName() { return sshName; }
    public String getSshPassword() { return sshPassword; }

    @Override
    public String toString() {
        return "ContainerInfo{" +
                "containerId='" + containerId + '\'' +
                ", port='" + port + '\'' +
                ", sshName='" + sshName + '\'' +
                ", sshPassword='" + sshPassword + '\'' +
                '}';
    }
}
