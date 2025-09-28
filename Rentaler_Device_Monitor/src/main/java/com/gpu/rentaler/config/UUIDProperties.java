package com.gpu.rentaler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.uuid")
public class UUIDProperties {
    // 默认文件路径
    private String filePath = "./uuid.dat";
    // 默认编码
    private String fileCharset = "UTF-8";

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileCharset() {
        return fileCharset;
    }

    public void setFileCharset(String fileCharset) {
        this.fileCharset = fileCharset;
    }
}
