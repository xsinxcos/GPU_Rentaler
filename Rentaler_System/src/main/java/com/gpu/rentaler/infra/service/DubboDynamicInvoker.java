package com.gpu.rentaler.infra.service;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.springframework.stereotype.Component;

@Component
public class DubboDynamicInvoker {

    private final ApplicationConfig application = new ApplicationConfig("dynamic-consumer");

    /**
     * 动态获取服务实例
     *
     * @param clazz   服务接口
     * @param version 服务版本
     * @param ip      提供者 IP
     * @param port    提供者端口
     * @param <T>     泛型
     * @return 服务代理对象
     */
    public <T> T getService(Class<T> clazz, String version, String ip, int port) {
        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setInterface(clazz);
        reference.setVersion(version);
        reference.setUrl("dubbo://" + ip + ":" + port); // 动态 IP 直连
        reference.setTimeout(5000);
        return reference.get();
    }
}
