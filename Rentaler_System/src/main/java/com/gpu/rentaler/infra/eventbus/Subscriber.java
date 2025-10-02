package com.gpu.rentaler.infra.eventbus;

import java.lang.reflect.Method;
import java.util.function.Predicate;

class Subscriber {
    private final Object instance;
    private final Method method;
    private final int priority;
    private final Predicate<Event> condition;
    private final boolean async;

    public Subscriber(Object instance, Method method, int priority,
                      Predicate<Event> condition, boolean async) {
        this.instance = instance;
        this.method = method;
        this.priority = priority;
        this.condition = condition;
        this.async = async;
        method.setAccessible(true);
    }

    public void invoke(Event event) throws Exception {
        if (condition == null || condition.test(event)) {
            method.invoke(instance, event);
        }
    }

    public int getPriority() { return priority; }
    public boolean isAsync() { return async; }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }

    public Predicate<Event> getCondition() {
        return condition;
    }
}
