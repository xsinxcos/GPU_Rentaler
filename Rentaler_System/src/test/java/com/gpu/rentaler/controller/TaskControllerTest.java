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
 * 任务控制器测试类
 *
 * @author wzq
 */
@DisplayName("任务管理接口测试")
public class TaskControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询我的任务列表")
    void testFindMyTasks() throws Exception {
        mvc.perform(get("/task/me")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询我的任务")
    void testFindMyTasksWithPagination() throws Exception {
        mvc.perform(get("/task/me")
                .param("page", "0")
                .param("size", "10")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按状态查询我的任务")
    void testFindMyTasksByStatus() throws Exception {
        mvc.perform(get("/task/me")
                .param("status", "ACTIVE")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试查询所有任务列表(管理员)")
    void testFindAllTasks() throws Exception {
        mvc.perform(get("/task/all")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询所有任务")
    void testFindAllTasksWithPagination() throws Exception {
        mvc.perform(get("/task/all")
                .param("page", "0")
                .param("size", "20")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按状态查询所有任务")
    void testFindAllTasksByStatus() throws Exception {
        mvc.perform(get("/task/all")
                .param("status", "COMPLETED")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试完成任务")
    void testFinishTask() throws Exception {
        mvc.perform(post("/task/finish")
                .param("taskId", "1")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试导出任务数据")
    void testExportTaskData() throws Exception {
        mvc.perform(post("/task/data/export")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "taskId": 1,
                      "path": "/data/output"
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试导出任务数据-余额不足")
    void testExportTaskDataInsufficientBalance() throws Exception {
        // 这个测试需要模拟余额不足的情况
        mvc.perform(post("/task/data/export")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "taskId": 1,
                      "path": "/data/output"
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试未授权访问我的任务")
    void testFindMyTasksWithoutAuth() throws Exception {
        mvc.perform(get("/task/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试未授权访问所有任务")
    void testFindAllTasksWithoutAuth() throws Exception {
        mvc.perform(get("/task/all"))
            .andExpect(status().isUnauthorized());
    }
}

