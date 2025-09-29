package com.gpu.rentaler.service;

import com.gpu.rentaler.config.UUIDProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ServerIDManager {
    private final UUIDProperties uuidProperties;
    private Long serverId;

    @Autowired
    public ServerIDManager(UUIDProperties uuidProperties) {
        this.uuidProperties = uuidProperties;
    }

    @PostConstruct
    public void init() {
        try {
            // 尝试读取已存在的UUID
            File uuidFile = new File(uuidProperties.getFilePath());

            // 如果文件存在则读取
            if (uuidFile.exists() && uuidFile.isFile()) {
                serverId = readServerFromFile(uuidFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application UUID", e);
        }
    }

    private Long readServerFromFile(File file) throws IOException {
        // 读取文件内容
        byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return Long.valueOf(new String(bytes, uuidProperties.getFileCharset()));
    }

    public Long saveServerID(Long serverId){
        File file = new File(uuidProperties.getFilePath());
        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 写入文件
        try {
            Files.write(Paths.get(file.getAbsolutePath()),
                String.valueOf(serverId).getBytes(uuidProperties.getFileCharset()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return serverId;
    }

    /**
     * 获取应用唯一标识
     *
     * @return UUID字符串
     */
    public Long getServerId() {
        return serverId;
    }
}
