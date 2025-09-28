package com.gpu.rentaler.service;

import com.gpu.rentaler.config.UUIDProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class UUIDManager {
    private final UUIDProperties uuidProperties;
    private String applicationUUID;

    @Autowired
    public UUIDManager(UUIDProperties uuidProperties) {
        this.uuidProperties = uuidProperties;
    }

    @PostConstruct
    public void init() {
        try {
            // 尝试读取已存在的UUID
            File uuidFile = new File(uuidProperties.getFilePath());

            // 如果文件存在则读取
            if (uuidFile.exists() && uuidFile.isFile()) {
                applicationUUID = readUUIDFromFile(uuidFile);
            } else {
                // 文件不存在则生成新UUID并保存
                applicationUUID = generateAndSaveUUID(uuidFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application UUID", e);
        }
    }

    private String readUUIDFromFile(File file) throws IOException {
        // 读取文件内容
        byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String(bytes, uuidProperties.getFileCharset());
    }

    private String generateAndSaveUUID(File file) throws IOException {
        // 生成新的UUID
        String newUUID = UUID.randomUUID().toString();

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 写入文件
        Files.write(Paths.get(file.getAbsolutePath()),
            newUUID.getBytes(uuidProperties.getFileCharset()));

        return newUUID;
    }

    /**
     * 获取应用唯一标识
     *
     * @return UUID字符串
     */
    public String getApplicationUUID() {
        return applicationUUID;
    }
}
