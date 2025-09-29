package com.gpu.rentaler.sys.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class MonitorThreadPool {

    @Bean("monitorTaskExecutor")
    public Executor monitorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：线程池维护的最小线程数量
        executor.setCorePoolSize(5);

        // 最大线程数：线程池允许的最大线程数量
        executor.setMaxPoolSize(10);

        // 队列容量：用于缓存等待执行的任务
        executor.setQueueCapacity(200);

        // 线程空闲时间：当线程数超过核心线程数时，多余线程的存活时间
        executor.setKeepAliveSeconds(60);

        // 线程名称前缀：方便日志追踪
        executor.setThreadNamePrefix("my-thread-");

        // 拒绝策略：当任务过多且无法处理时的处理方式
        // CallerRunsPolicy：让提交任务的线程自己执行该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化线程池
        executor.initialize();

        return executor;
    }
}
