package com.gpu.rentaler;

/**
 * 测试Session辅助类
 * 提供测试用的token和用户信息
 *
 * @author wzq
 */
public class TestSessionHelper {

    /**
     * 测试管理员用户信息
     */
    public static class AdminUser {
        public static final Long USER_ID = 1L;
        public static final String USERNAME = "admin";
        public static final String PASSWORD = "123456";
        public static final String TOKEN = "test_admin_token_123456";
        public static final String BEARER_TOKEN = "Bearer " + TOKEN;
        public static final Long CREDENTIAL_ID = 1L;
    }

    /**
     * 测试普通用户信息
     */
    public static class NormalUser {
        public static final Long USER_ID = 2L;
        public static final String USERNAME = "admin";
        public static final String PASSWORD = "123456";
        public static final String TOKEN = "test_user_token_123456";
        public static final String BEARER_TOKEN = "Bearer " + TOKEN;
        public static final Long CREDENTIAL_ID = 2L;
    }

    /**
     * Session信息
     */
    public static class SessionInfo {
        public static final Long ADMIN_SESSION_ID = 1L;
        public static final Long USER_SESSION_ID = 2L;
        public static final String EXPIRE_TIME = "2099-12-31 23:59:59";
    }

    /**
     * 管理员权限列表（完整权限）
     */
    public static final String[] ADMIN_PERMISSIONS = {
        "*", "dashboard", "sys",
        "user:view", "user:create", "user:update", "user:delete",
        "role:view", "role:create", "role:update", "role:delete",
        "resource:view", "resource:create", "resource:update", "resource:delete",
        "organization:create", "organization:update", "organization:delete",
        "log:view", "log:clean",
        "storage:view", "storage:create", "storage:update", "storage:delete", "storage:markAsDefault",
        "gpu", "gpu:release", "gpu:view", "gpu:modify",
        "server:view", "server:modify", "server:delete",
        "sandbox", "sandbox:wallet:recharge",
        "task:all", "application",
        "gpu:isRentable:view", "gpu:lease",
        "task:me", "task:log", "task:finish", "task:data:export",
        "wallet:my",
        "dockerImage:me", "dockerImage:upload"
    };
    
    /**
     * 普通用户权限列表（应用中心相关）
     */
    public static final String[] USER_PERMISSIONS = {
        "dashboard", "application",
        "gpu:isRentable:view", "gpu:lease",
        "task:me", "task:log", "task:finish", "task:data:export",
        "wallet:my",
        "dockerImage:me", "dockerImage:upload"
    };

    private TestSessionHelper() {
        // 工具类，不允许实例化
    }

    /**
     * 获取管理员Token（带Bearer前缀）
     */
    public static String getAdminToken() {
        return AdminUser.BEARER_TOKEN;
    }

    /**
     * 获取普通用户Token（带Bearer前缀）
     */
    public static String getUserToken() {
        return NormalUser.BEARER_TOKEN;
    }

    /**
     * 获取管理员用户ID
     */
    public static Long getAdminUserId() {
        return AdminUser.USER_ID;
    }

    /**
     * 获取普通用户ID
     */
    public static Long getUserUserId() {
        return NormalUser.USER_ID;
    }
}

