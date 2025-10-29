package com.gpu.rentaler.controller;

import com.gpu.rentaler.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.gpu.rentaler.Constants.TOKEN;
import static com.gpu.rentaler.Constants.TOKEN_HEADER_NAME;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 日志控制器测试类
 *
 * @author wzq
 */
@DisplayName("日志管理接口测试")
public class LogControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询日志列表")
    void testFindLogs() throws Exception {
        mvc.perform(get("/logs")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询日志")
    void testFindLogsWithPagination() throws Exception {
        mvc.perform(get("/logs")
                .param("page", "0")
                .param("size", "20")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按类型查询日志")
    void testFindLogsByType() throws Exception {
        mvc.perform(get("/logs")
                .param("type", "com.gpu.rentaler.sys.event.UserLoggedIn")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按用户查询日志")
    void testFindLogsByUsername() throws Exception {
        mvc.perform(get("/logs")
                .param("username", "testadmin")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按时间范围查询日志")
    void testFindLogsByDateRange() throws Exception {
        mvc.perform(get("/logs")
                .param("startDate", "2023-01-01")
                .param("endDate", "2025-12-31")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试清空日志")
    void testCleanLogs() throws Exception {
        mvc.perform(delete("/logs")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isNoContent());
    }

}
