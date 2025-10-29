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
 * 通用功能控制器测试类
 *
 * @author wzq
 */
@DisplayName("通用功能接口测试")
public class CommonControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询事件类型列表")
    void testFindEventTypes() throws Exception {
        mvc.perform(get("/common/event-types")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$", isA(Iterable.class)));
    }

    @Test
    @DisplayName("测试事件类型列表包含标签和值")
    void testEventTypesContainLabelAndValue() throws Exception {
        mvc.perform(get("/common/event-types")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].label", notNullValue()))
            .andExpect(jsonPath("$[0].value", notNullValue()));
    }

    @Test
    @DisplayName("测试事件类型列表内容正确性")
    void testEventTypesContent() throws Exception {
        mvc.perform(get("/common/event-types")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("测试未授权访问事件类型列表")
    void testFindEventTypesWithoutAuth() throws Exception {
        mvc.perform(get("/common/event-types"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试事件类型值为完整类名")
    void testEventTypeValueFormat() throws Exception {
        mvc.perform(get("/common/event-types")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].value", matchesPattern(".*\\..*")));  // 包含点号，表示完整类名
    }


}

