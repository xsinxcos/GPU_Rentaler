package com.gpu.rentaler.infra.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface EventSubscriber {
    String eventName();
    int priority() default 0; // 优先级，数字越大优先级越高
    boolean async() default false; // 是否异步执行
}
