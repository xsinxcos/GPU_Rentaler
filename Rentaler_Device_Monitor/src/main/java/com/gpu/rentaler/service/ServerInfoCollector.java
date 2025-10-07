package com.gpu.rentaler.service;

import com.gpu.rentaler.config.IPProperties;
import com.gpu.rentaler.entity.ServerInfo;
import jakarta.annotation.Resource;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务器信息收集器
 */
@Component
public class ServerInfoCollector {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS_NAME.contains("windows");
    private static final boolean IS_LINUX = OS_NAME.contains("linux");
    private static final boolean IS_MAC = OS_NAME.contains("mac");

    @Resource
    private IPProperties ipProperties;
    @Resource
    private ServerIDManager serverIDManager;

    /**
     * 获取完整的服务器信息
     */
    public ServerInfo getServerInfo() {
        ServerInfo serverInfo = new ServerInfo();

        try {
            // 获取主机ID
            serverInfo.setServerId(serverIDManager.getServerId());

            // 获取主机名
            serverInfo.setHostname(getHostname());

            // 获取IP地址
            serverInfo.setIpAddress(getIPAddress());

            // 获取CPU信息
            String[] cpuInfo = getCPUInfo();
            if (cpuInfo != null && cpuInfo.length >= 2) {
                serverInfo.setCpuModel(cpuInfo[0]);
                serverInfo.setCpuCores(Integer.parseInt(cpuInfo[1]));
            }

            // 获取内存信息
            serverInfo.setRamTotalGb(getRAMTotalGB());

            // 获取存储信息
            serverInfo.setStorageTotalGb(getStorageTotalGB());

            // 获取GPU插槽数量
            serverInfo.setGpuSlots(getGPUSlots());

        } catch (Exception e) {
            System.err.println("获取服务器信息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return serverInfo;
    }

    /**
     * 获取主机名
     */
    private String getHostname() {
        try {
            // 方法1: 使用InetAddress
            String hostname = InetAddress.getLocalHost().getHostName();
            if (hostname != null && !hostname.trim().isEmpty()) {
                return hostname.trim();
            }
        } catch (Exception e) {
            System.err.println("方法1获取主机名失败: " + e.getMessage());
        }

        try {
            // 方法2: 使用系统命令
            String command = "hostname";
            String result = executeCommand(command);
            if (result != null && !result.trim().isEmpty()) {
                return result.trim().split("\n")[0];
            }
        } catch (Exception e) {
            System.err.println("方法2获取主机名失败: " + e.getMessage());
        }

        try {
            // 方法3: 使用环境变量
            String hostname = System.getenv("HOSTNAME");
            if (hostname == null) {
                hostname = System.getenv("COMPUTERNAME");
            }
            if (hostname != null && !hostname.trim().isEmpty()) {
                return hostname.trim();
            }
        } catch (Exception e) {
            System.err.println("方法3获取主机名失败: " + e.getMessage());
        }

        return "unknown-host";
    }

    /**
     * 获取IP地址
     */
    private String getIPAddress() {
        if(StringUtils.hasText(ipProperties.getUrl())){
            return ipProperties.getUrl();
        }
        try {
            // 方法1: 获取本地主机地址
            String localIP = InetAddress.getLocalHost().getHostAddress();
            if (localIP != null && !localIP.equals("127.0.0.1")) {
                return localIP;
            }
        } catch (Exception e) {
            System.err.println("方法1获取IP地址失败: " + e.getMessage());
        }

        try {
            // 方法2: 遍历网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // 跳过回环接口和非活跃接口
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    // 获取IPv4地址，排除本地回环
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("方法2获取IP地址失败: " + e.getMessage());
        }

        try {
            // 方法3: 使用系统命令
            String command;
            if (IS_WINDOWS) {
                command = "ipconfig";
            } else {
                command = "ip route get 8.8.8.8";
            }

            String result = executeCommand(command);
            if (result != null) {
                return extractIPFromCommandOutput(result);
            }
        } catch (Exception e) {
            System.err.println("方法3获取IP地址失败: " + e.getMessage());
        }

        return "unknown-ip";
    }

    /**
     * 从命令输出中提取IP地址
     */
    private String extractIPFromCommandOutput(String output) {
        Pattern ipPattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
        Matcher matcher = ipPattern.matcher(output);

        while (matcher.find()) {
            String ip = matcher.group();
            // 排除常见的本地回环和广播地址
            if (!ip.startsWith("127.") && !ip.startsWith("255.") &&
                !ip.startsWith("0.") && !ip.equals("0.0.0.0")) {
                return ip;
            }
        }

        return "unknown-ip";
    }

    /**
     * 从配置文件读取位置信息
     */
    private String getLocationFromConfig() {
        try {
            // 尝试读取server.properties文件
            Path configPath = Paths.get("server.properties");
            if (Files.exists(configPath)) {
                Properties props = new Properties();
                try (InputStream is = Files.newInputStream(configPath)) {
                    props.load(is);
                    return props.getProperty("server.location");
                }
            }
        } catch (Exception e) {
            // 忽略配置文件读取错误
        }
        return null;
    }

    /**
     * 获取CPU信息
     *
     * @return 数组，[0]为CPU型号，[1]为核心数
     */
    private String[] getCPUInfo() {
        try {
            if (IS_WINDOWS) {
                return getCPUInfoWindows();
            } else if (IS_LINUX) {
                return getCPUInfoLinux();
            } else if (IS_MAC) {
                return getCPUInfoMac();
            }
        } catch (Exception e) {
            System.err.println("获取CPU信息失败: " + e.getMessage());
        }

        // 默认值
        return new String[]{"Unknown CPU", String.valueOf(Runtime.getRuntime().availableProcessors())};
    }

    /**
     * Windows系统获取CPU信息
     */
    private String[] getCPUInfoWindows() {
        try {
            // 获取CPU型号
            String wmicCPU = executeCommand("wmic cpu get name /format:list");
            String cpuModel = "Unknown CPU";
            if (wmicCPU != null) {
                Pattern pattern = Pattern.compile("Name=(.+)");
                Matcher matcher = pattern.matcher(wmicCPU);
                if (matcher.find()) {
                    cpuModel = matcher.group(1).trim();
                }
            }

            // 获取CPU核心数
            String wmicCores = executeCommand("wmic cpu get NumberOfCores /format:list");
            int totalCores = 0;
            if (wmicCores != null) {
                Pattern pattern = Pattern.compile("NumberOfCores=(\\d+)");
                Matcher matcher = pattern.matcher(wmicCores);
                while (matcher.find()) {
                    totalCores += Integer.parseInt(matcher.group(1));
                }
            }

            if (totalCores == 0) {
                totalCores = Runtime.getRuntime().availableProcessors();
            }

            return new String[]{cpuModel, String.valueOf(totalCores)};

        } catch (Exception e) {
            System.err.println("Windows获取CPU信息失败: " + e.getMessage());
            return new String[]{"Unknown CPU", String.valueOf(Runtime.getRuntime().availableProcessors())};
        }
    }

    /**
     * Linux系统获取CPU信息
     */
    private String[] getCPUInfoLinux() {
        try {
            String cpuModel = "Unknown CPU";
            int cpuCores = 0;

            // 读取/proc/cpuinfo
            String cpuInfo = executeCommand("cat /proc/cpuinfo");
            if (cpuInfo != null) {
                String[] lines = cpuInfo.split("\n");

                for (String line : lines) {
                    line = line.trim();

                    // 获取CPU型号
                    if (line.startsWith("model name") && cpuModel.equals("Unknown CPU")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            cpuModel = parts[1].trim();
                        }
                    }

                    // 计算CPU核心数
                    if (line.startsWith("processor")) {
                        cpuCores++;
                    }
                }
            }

            if (cpuCores == 0) {
                cpuCores = Runtime.getRuntime().availableProcessors();
            }

            return new String[]{cpuModel, String.valueOf(cpuCores)};

        } catch (Exception e) {
            System.err.println("Linux获取CPU信息失败: " + e.getMessage());
            return new String[]{"Unknown CPU", String.valueOf(Runtime.getRuntime().availableProcessors())};
        }
    }

    /**
     * macOS系统获取CPU信息
     */
    private String[] getCPUInfoMac() {
        try {
            String cpuModel = "Unknown CPU";
            int cpuCores = 0;

            // 获取CPU型号
            String cpuBrand = executeCommand("sysctl -n machdep.cpu.brand_string");
            if (cpuBrand != null && !cpuBrand.trim().isEmpty()) {
                cpuModel = cpuBrand.trim();
            }

            // 获取CPU核心数
            String coreCount = executeCommand("sysctl -n hw.ncpu");
            if (coreCount != null && !coreCount.trim().isEmpty()) {
                cpuCores = Integer.parseInt(coreCount.trim());
            }

            if (cpuCores == 0) {
                cpuCores = Runtime.getRuntime().availableProcessors();
            }

            return new String[]{cpuModel, String.valueOf(cpuCores)};

        } catch (Exception e) {
            System.err.println("macOS获取CPU信息失败: " + e.getMessage());
            return new String[]{"Unknown CPU", String.valueOf(Runtime.getRuntime().availableProcessors())};
        }
    }

    /**
     * 获取内存总量(GB)
     */
    private Integer getRAMTotalGB() {
        try {
            if (IS_WINDOWS) {
                return getRAMTotalWindows();
            } else if (IS_LINUX) {
                return getRAMTotalLinux();
            } else if (IS_MAC) {
                return getRAMTotalMac();
            }
        } catch (Exception e) {
            System.err.println("获取内存信息失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * Windows系统获取内存总量
     */
    private Integer getRAMTotalWindows() {
        try {
            String wmicRAM = executeCommand("wmic computersystem get TotalPhysicalMemory /format:list");
            if (wmicRAM != null) {
                Pattern pattern = Pattern.compile("TotalPhysicalMemory=(\\d+)");
                Matcher matcher = pattern.matcher(wmicRAM);
                if (matcher.find()) {
                    long totalBytes = Long.parseLong(matcher.group(1));
                    return (int) (totalBytes / (1024 * 1024 * 1024)); // 转换为GB
                }
            }
        } catch (Exception e) {
            System.err.println("Windows获取内存失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * Linux系统获取内存总量
     */
    private Integer getRAMTotalLinux() {
        try {
            String memInfo = executeCommand("cat /proc/meminfo | grep MemTotal");
            if (memInfo != null) {
                Pattern pattern = Pattern.compile("MemTotal:\\s+(\\d+)\\s+kB");
                Matcher matcher = pattern.matcher(memInfo);
                if (matcher.find()) {
                    long totalKB = Long.parseLong(matcher.group(1));
                    return (int) (totalKB / (1024 * 1024)); // 转换为GB
                }
            }
        } catch (Exception e) {
            System.err.println("Linux获取内存失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * macOS系统获取内存总量
     */
    private Integer getRAMTotalMac() {
        try {
            String memInfo = executeCommand("sysctl -n hw.memsize");
            if (memInfo != null && !memInfo.trim().isEmpty()) {
                long totalBytes = Long.parseLong(memInfo.trim());
                return (int) (totalBytes / (1024 * 1024 * 1024)); // 转换为GB
            }
        } catch (Exception e) {
            System.err.println("macOS获取内存失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 获取存储总量(GB)
     */
    private Integer getStorageTotalGB() {
        try {
            long totalStorage = 0;

            // 获取所有文件系统根目录
            File[] roots = File.listRoots();

            for (File root : roots) {
                try {
                    totalStorage += root.getTotalSpace();
                } catch (Exception e) {
                    System.err.println("获取磁盘空间失败: " + root.getPath() + " - " + e.getMessage());
                }
            }

            return (int) (totalStorage / (1024 * 1024 * 1024)); // 转换为GB

        } catch (Exception e) {
            System.err.println("获取存储信息失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 获取GPU插槽数量
     */
    private Integer getGPUSlots() {
        try {
            if (IS_WINDOWS) {
                return getGPUSlotsWindows();
            } else if (IS_LINUX) {
                return getGPUSlotsLinux();
            } else if (IS_MAC) {
                return getGPUSlotsMac();
            }
        } catch (Exception e) {
            System.err.println("获取GPU插槽数失败: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Windows系统获取GPU插槽数量
     */
    private Integer getGPUSlotsWindows() {
        try {
            String wmicGPU = executeCommand("wmic path win32_VideoController get name");
            if (wmicGPU != null) {
                String[] lines = wmicGPU.split("\n");
                int count = 0;

                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("Name") &&
                        (line.contains("NVIDIA") || line.contains("AMD") ||
                            line.contains("Intel") || line.contains("Radeon") ||
                            line.contains("GeForce") || line.contains("Quadro"))) {
                        count++;
                    }
                }

                return count;
            }
        } catch (Exception e) {
            System.err.println("Windows获取GPU插槽数失败: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Linux系统获取GPU插槽数量
     */
    private Integer getGPUSlotsLinux() {
        try {
            // 方法1: 使用lspci命令
            String lspciGPU = executeCommand("lspci | grep -i 'vga\\|3d\\|display'");
            if (lspciGPU != null) {
                String[] lines = lspciGPU.split("\n");
                int count = 0;

                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        count++;
                    }
                }

                if (count > 0) {
                    return count;
                }
            }

            // 方法2: 检查/dev/nvidia*设备
            String nvidiaDevices = executeCommand("ls /dev/nvidia* 2>/dev/null | wc -l");
            if (nvidiaDevices != null && !nvidiaDevices.trim().isEmpty()) {
                try {
                    int nvidiaCount = Integer.parseInt(nvidiaDevices.trim());
                    if (nvidiaCount > 0) {
                        return nvidiaCount - 1; // 减去nvidiactl设备
                    }
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }

        } catch (Exception e) {
            System.err.println("Linux获取GPU插槽数失败: " + e.getMessage());
        }

        return 0;
    }

    /**
     * macOS系统获取GPU插槽数量
     */
    private Integer getGPUSlotsMac() {
        try {
            String gpuInfo = executeCommand("system_profiler SPDisplaysDataType");
            if (gpuInfo != null) {
                int count = 0;
                String[] lines = gpuInfo.split("\n");

                for (String line : lines) {
                    if (line.trim().startsWith("Chipset Model:") ||
                        line.trim().startsWith("Graphics Cards:")) {
                        count++;
                    }
                }

                return count;
            }
        } catch (Exception e) {
            System.err.println("macOS获取GPU插槽数失败: " + e.getMessage());
        }

        return 0;
    }

    /**
     * 执行系统命令
     */
    private String executeCommand(String command) {
        try {
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            executor.setStreamHandler(streamHandler);

            // 设置退出值范围，允许非零退出值
            executor.setExitValues(new int[]{0, 1});

            executor.execute(cmdLine);
            return outputStream.toString("UTF-8");

        } catch (Exception e) {
            System.err.println("执行命令失败: " + command + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        ServerInfoCollector collector = new ServerInfoCollector();
        ServerInfo serverInfo = collector.getServerInfo();

        System.out.println("=== 服务器信息 ===");
        System.out.println("主机名: " + serverInfo.getHostname());
        System.out.println("IP地址: " + serverInfo.getIpAddress());
        System.out.println("CPU型号: " + serverInfo.getCpuModel());
        System.out.println("CPU核心数: " + serverInfo.getCpuCores());
        System.out.println("内存总量: " + serverInfo.getRamTotalGb() + " GB");
        System.out.println("存储总量: " + serverInfo.getStorageTotalGb() + " GB");
        System.out.println("GPU插槽数量: " + serverInfo.getGpuSlots());
        System.out.println("==================");
    }
}
