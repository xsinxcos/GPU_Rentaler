package com.gpu.rentaler;

/**
 * 测试常量类
 * 
 * @author wzq
 */
public class Constants {
    /**
     * Token请求头名称
     */
    public static final String TOKEN_HEADER_NAME = "Authorization";
    
    /**
     * 测试用Token（testadmin管理员用户）
     * 对应session表中的记录，userId=1
     */
    public static final String TOKEN = "Bearer test_admin_token_123456";
    
    /**
     * 普通用户Token（testuser用户）
     * 对应session表中的记录，userId=2
     */
    public static final String USER_TOKEN = "Bearer test_user_token_123456";
    
    /**
     * 测试管理员用户名
     */
    public static final String ADMIN_USERNAME = "testadmin";
    
    /**
     * 测试普通用户名
     */
    public static final String USER_USERNAME = "testuser";
    
    /**
     * 测试密码（统一使用 123456）
     */
    public static final String TEST_PASSWORD = "123456";

    private Constants() {
        // 工具类，不允许实例化
    }
}
