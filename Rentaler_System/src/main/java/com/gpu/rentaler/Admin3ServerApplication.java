package com.gpu.rentaler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties(Admin3Properties.class)
@SpringBootApplication
@EnableScheduling // 启用定时任务
public class Admin3ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Admin3ServerApplication.class, args);
    }

}
