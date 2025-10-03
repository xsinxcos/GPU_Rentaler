package com.gpu.rentaler.common;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {
    /**
     * 校验文件是否为Docker导出的镜像文件
     *
     * @return true如果是有效的Docker镜像文件，否则返回false
     */
    public static boolean isDockerImage(InputStream bis) {
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new BufferedInputStream(bis))) {
            Set<String> entryNames = new HashSet<>();
            ArchiveEntry entry;

            while ((entry = tarInput.getNextEntry()) != null) {
                String name = entry.getName();
                entryNames.add(name);

                // 提前返回优化：有明显 docker 文件结构
                if (name.equals("manifest.json") || name.startsWith("layer.tar") || name.endsWith("/layer.tar")) {
                    return true; // docker save 导出的镜像
                }
                if (name.equals("etc/hosts") || name.equals("bin/sh") || name.equals("root/.bashrc")) {
                    return true; // docker export 导出的容器文件系统
                }
            }

            // 兜底判断：检查 entry 数量/特征
            if (entryNames.contains("manifest.json")) {
                return true;
            }
            if (entryNames.stream().anyMatch(n -> n.endsWith("/layer.tar"))) {
                return true;
            }

        } catch (IOException e) {
            // 不是有效的 tar 文件
            return false;
        }

        return false;
    }
}
