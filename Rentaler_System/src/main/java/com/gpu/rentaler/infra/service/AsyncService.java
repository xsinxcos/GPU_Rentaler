package com.gpu.rentaler.infra.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    @Async("IOTaskExecutor")
    public void asyncExecute(Runnable task) {
        task.run();  // 这里的 run 会在异步线程里执行
    }
}
