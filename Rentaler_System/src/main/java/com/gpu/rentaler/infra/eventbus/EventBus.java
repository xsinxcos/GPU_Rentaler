package com.gpu.rentaler.infra.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

class EventBus {
    private static final EventBus INSTANCE = new EventBus();

    private final Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();
    private final boolean enableLogging;
    private final java.util.concurrent.ExecutorService executorService;

    private EventBus() {
        this(false);
    }

    private EventBus(boolean enableLogging) {
        this.enableLogging = enableLogging;
        // 创建线程池用于异步执行
        this.executorService = java.util.concurrent.Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2,
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("EventBus-Async-" + thread.getId());
                thread.setDaemon(true);
                return thread;
            }
        );
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    /**
     * 注册对象中所有带 @EventSubscriber 注解的方法
     */
    public void register(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            EventSubscriber annotation = method.getAnnotation(EventSubscriber.class);
            if (annotation != null) {
                String eventName = annotation.eventName();
                int priority = annotation.priority();
                boolean async = annotation.async();

                // 验证方法签名
                if (method.getParameterCount() != 1 ||
                    !method.getParameterTypes()[0].equals(Event.class)) {
                    throw new IllegalArgumentException(
                        "订阅方法必须接收一个 Event 参数: " + method.getName()
                    );
                }

                Subscriber subscriber = new Subscriber(obj, method, priority, null, async);
                subscribers.computeIfAbsent(eventName, k -> new ArrayList<>()).add(subscriber);

                // 按优先级排序
                subscribers.get(eventName).sort((a, b) ->
                    Integer.compare(b.getPriority(), a.getPriority())
                );

                if (enableLogging) {
                    System.out.println("注册订阅者: " + eventName + " -> " + method.getName() +
                        (async ? " [异步]" : " [同步]"));
                }
            }
        }
    }

    /**
     * 手动订阅事件（支持条件判断）
     */
    public void subscribe(String eventName, Object instance, Method method,
                          Predicate<Event> condition) {
        subscribe(eventName, instance, method, 0, condition, false);
    }

    public void subscribe(String eventName, Object instance, Method method,
                          int priority, Predicate<Event> condition) {
        subscribe(eventName, instance, method, priority, condition, false);
    }

    public void subscribe(String eventName, Object instance, Method method,
                          int priority, Predicate<Event> condition, boolean async) {
        Subscriber subscriber = new Subscriber(instance, method, priority, condition, async);
        subscribers.computeIfAbsent(eventName, k -> new ArrayList<>()).add(subscriber);

        subscribers.get(eventName).sort((a, b) ->
            Integer.compare(b.getPriority(), a.getPriority())
        );
    }

    /**
     * 发布事件
     */
    public void publish(String eventName, Object source, Object data) {
        Event event = new Event(eventName, source, data);
        List<Subscriber> eventSubscribers = subscribers.get(eventName);

        if (eventSubscribers == null || eventSubscribers.isEmpty()) {
            if (enableLogging) {
                System.out.println("没有订阅者监听事件: " + eventName);
            }
            return;
        }

        if (enableLogging) {
            System.out.println("发布事件: " + eventName + " [订阅者数: " +
                eventSubscribers.size() + "]");
        }

        for (Subscriber subscriber : eventSubscribers) {
            if (subscriber.isAsync()) {
                // 异步执行
                executorService.submit(() -> {
                    try {
                        if (enableLogging) {
                            System.out.println("  [异步] 执行订阅者: " +
                                subscriber.getMethod().getName() +
                                " [线程: " + Thread.currentThread().getName() + "]");
                        }
                        subscriber.invoke(event);
                    } catch (Exception e) {
                        System.err.println("异步订阅者执行失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                // 同步执行
                try {
                    if (enableLogging) {
                        System.out.println("  [同步] 执行订阅者: " + subscriber.getMethod().getName());
                    }
                    subscriber.invoke(event);
                } catch (Exception e) {
                    System.err.println("同步订阅者执行失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取消注册
     */
    public void unregister(Object obj) {
        subscribers.values().forEach(list ->
            list.removeIf(sub -> sub.getInstance().equals(obj))
        );
    }

    /**
     * 清空所有订阅者
     */
    public void clear() {
        subscribers.clear();
    }

    /**
     * 关闭事件总线，释放线程池资源
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
