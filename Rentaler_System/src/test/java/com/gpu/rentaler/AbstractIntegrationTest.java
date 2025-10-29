package com.gpu.rentaler;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类
 * - 使用 Testcontainers 提供 MySQL 测试环境
 * - 激活 test profile
 * - 自动配置 MockMvc
 * 
 * @author wzq
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ContainersConfig.class)
public abstract class AbstractIntegrationTest {
}
