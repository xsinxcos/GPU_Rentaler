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
 * 用户控制器测试类
 *
 * @author wzq
 */
@DisplayName("用户管理接口测试")
public class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询用户列表")
    void testFindUsers() throws Exception {
        mvc.perform(get("/users")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询用户")
    void testFindUsersWithPagination() throws Exception {
        mvc.perform(get("/users")
                .param("page", "0")
                .param("size", "5")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试创建用户")
    void testCreateUser() throws Exception {
        String username = "testuser_" + System.currentTimeMillis();
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "username": "%s",
                      "gender": "MALE",
                      "avatar": "avatar.jpg",
                      "organizationId": 1
                    }
                    """, username)))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("username", is(username)))
            .andExpect(jsonPath("gender", is("MALE")))
            .andExpect(jsonPath("id", notNullValue()));
    }

    @Test
    @DisplayName("测试创建用户-用户名已存在")
    void testCreateUserWithDuplicateUsername() throws Exception {
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "username": "testadmin",
                      "gender": "MALE",
                      "avatar": "avatar.jpg",
                      "organizationId": 1
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试创建用户-缺少必填字段")
    void testCreateUserWithMissingFields() throws Exception {
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "gender": "MALE"
                    }
                    """))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("测试更新用户")
    void testUpdateUser() throws Exception {
        mvc.perform(put("/users/{userId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "gender": "FEMALE",
                      "avatar": "new_avatar.jpg",
                      "organizationId": 1
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id", is(1)));
    }

    @Test
    @DisplayName("测试更新不存在的用户")
    void testUpdateNonExistentUser() throws Exception {
        mvc.perform(put("/users/{userId}", 999999)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "gender": "MALE",
                      "avatar": "avatar.jpg",
                      "organizationId": 1
                    }
                    """))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("测试禁用用户")
    void testDisableUser() throws Exception {
        mvc.perform(post("/users/{userId}:disable", 2)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试启用用户")
    void testEnableUser() throws Exception {
        mvc.perform(post("/users/{userId}:enable", 2)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试未授权访问用户列表")
    void testFindUsersWithoutAuth() throws Exception {
        mvc.perform(get("/users"))
            .andExpect(status().isUnauthorized());
    }



}
