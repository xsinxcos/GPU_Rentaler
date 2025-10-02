package com.gpu.rentaler.infra.eventbus;

class Example {

    // 示例类A - 发布者
    static class ServiceA {
        private EventBus eventBus;

        public ServiceA(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        public void methodA(String message) {
            System.out.println("方法A执行: " + message);
            // 发布事件
            eventBus.publish("methodA.executed", this, message);
        }

        public void methodA2(int value) {
            System.out.println("方法A2执行: " + value);
            eventBus.publish("methodA2.executed", this, value);
        }
    }

    // 示例类B - 订阅者
    static class ServiceB {

        @EventSubscriber(eventName = "methodA.executed", priority = 10)
        public void onMethodAExecuted(Event event) {
            String message = (String) event.getData();
            System.out.println("  → 方法B监听到方法A（同步），数据: " + message);

            // 可以进行条件判断
            if (message.contains("重要")) {
                System.out.println("  → 方法B检测到重要消息，触发连锁反应!");
                // 可以继续发布新事件，形成链式反应
            }
        }

        @EventSubscriber(eventName = "methodA2.executed", priority = 5, async = true)
        public void onMethodA2Executed(Event event) {
            Integer value = (Integer) event.getData();
            System.out.println("  → 方法B监听到方法A2（异步），值: " + value +
                             " [线程: " + Thread.currentThread().getName() + "]");

            // 模拟耗时操作
            try {
                Thread.sleep(1000);
                System.out.println("  → 方法B异步处理完成，值: " + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 条件判断
            if (value > 100) {
                System.out.println("  → 方法B检测到值超过100，执行特殊逻辑!");
            }
        }
    }

    // 示例类C - 另一个订阅者（演示多订阅者）
    static class ServiceC {

        @EventSubscriber(eventName = "methodA.executed", priority = 5, async = true)
        public void handleMethodA(Event event) {
            System.out.println("  → 方法C也监听到方法A（异步） [线程: " +
                             Thread.currentThread().getName() + "]");
            try {
                Thread.sleep(500);
                System.out.println("  → 方法C异步处理完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        EventBus eventBus = EventBus.getInstance();

        // 创建服务实例
        ServiceA serviceA = new ServiceA(eventBus);
        ServiceB serviceB = new ServiceB();
        ServiceC serviceC = new ServiceC();

        // 注册订阅者
        eventBus.register(serviceB);
        eventBus.register(serviceC);

        System.out.println("========== 测试1: 普通消息 ==========");
        serviceA.methodA("普通消息");

        System.out.println("\n========== 测试2: 重要消息 ==========");
        serviceA.methodA("这是重要消息");

        System.out.println("\n========== 测试3: 数值事件 ==========");
        serviceA.methodA2(50);
        serviceA.methodA2(150);

        System.out.println("\n========== 测试4: 手动订阅（带条件+异步） ==========");
        // 手动订阅，只有当消息长度大于5时才触发，且异步执行
        eventBus.subscribe(
            "methodA.executed",
            new Object() {
                public void conditionalHandler(Event event) {
                    System.out.println("  → 条件订阅者（异步）: 消息长度符合条件 [线程: " +
                                     Thread.currentThread().getName() + "]");
                }
            },
            Example.class.getDeclaredMethod("conditionalHandler", Event.class),
            0,
            event -> ((String) event.getData()).length() > 5,
            true  // 异步执行
        );

        serviceA.methodA("短");
        serviceA.methodA("这是一条长消息");

        // 等待异步任务完成
        System.out.println("\n等待所有异步任务完成...");
        Thread.sleep(2000);

        // 关闭事件总线
        eventBus.shutdown();
        System.out.println("\n事件总线已关闭");
    }

    // 用于手动订阅的方法
    public static void conditionalHandler(Event event) {
        System.out.println("  → 条件订阅者: 消息长度符合条件");
    }
}
