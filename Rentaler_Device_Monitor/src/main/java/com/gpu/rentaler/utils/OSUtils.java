package com.gpu.rentaler.utils;

public class OSUtils {

    public enum OSType {
        WINDOWS,
        MAC,
        LINUX,
        SOLARIS,
        OTHER
    }

    /**
     * 获取当前操作系统类型
     */
    public static OSType getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OSType.WINDOWS;
        } else if (osName.contains("mac")) {
            return OSType.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OSType.LINUX;
        } else if (osName.contains("sunos")) {
            return OSType.SOLARIS;
        } else {
            return OSType.OTHER;
        }
    }
}
