package com.gpu.rentaler.sys.monitor;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

public class DirectMemoryMonitor {
    /**
     * 获取直接内存缓冲池监控Bean
     */
    public static BufferPoolMXBean getDirectBufferPool() {
        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean pool : pools) {
            if ("direct".equals(pool.getName())) {
                return pool;
            }
        }
        return null;
    }

    /**
     * 打印直接内存使用情况
     */
    public static void printDirectMemoryUsage() {
        try {
            BufferPoolMXBean directPool = getDirectBufferPool();
            if (directPool != null) {
                long totalCapacity = directPool.getTotalCapacity();
                long memoryUsed = directPool.getMemoryUsed();

                System.out.printf("直接内存池总容量: %.2f MB%n", totalCapacity / 1024.0 / 1024.0);
                System.out.printf("已使用直接内存: %.2f MB%n", memoryUsed / 1024.0 / 1024.0);
                System.out.printf("剩余直接内存: %.2f MB%n", (totalCapacity - memoryUsed) / 1024.0 / 1024.0);
            } else {
                System.out.println("未找到直接内存缓冲池");
            }
        } catch (Exception e) {
            System.out.println("无法获取直接内存使用情况: " + e.getMessage());
        }
    }
}
