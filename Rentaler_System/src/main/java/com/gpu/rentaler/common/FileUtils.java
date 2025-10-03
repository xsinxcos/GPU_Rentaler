package com.gpu.rentaler.common;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    /**
     * 校验文件是否为Docker导出的镜像文件
     *
     * @return true如果是有效的Docker镜像文件，否则返回false
     */
    public static boolean isDockerImage(InputStream bis) {
        try (TarInputStream tis = new TarInputStream(bis)) {

            boolean hasManifest = false;
            boolean hasLayerOrConfig = false;

            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                String name = entry.getName();

                // 检查manifest.json（Docker镜像必需文件）
                if (name.equals("manifest.json")) {
                    hasManifest = true;
                }

                // 检查是否有layer.tar或配置json文件
                if (name.endsWith("/layer.tar") ||
                    name.matches("^[a-f0-9]{64}\\.json$")) {
                    hasLayerOrConfig = true;
                }
            }

            // Docker镜像至少要有manifest.json
            return hasManifest;

        } catch (IOException e) {
            return false;
        }
    }
}
