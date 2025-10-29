package com.gpu.rentaler.controller;

import com.gpu.rentaler.AbstractIntegrationTest;
import com.gpu.rentaler.common.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.gpu.rentaler.Constants.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 登录控制器测试类
 *
 * @author wzq
 */
@DisplayName("登录接口测试")
public class LoginControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试登录成功")
    void testLoginSuccess() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                       {
                         "username": "%s",
                         "password": "%s"
                        }
                    """, ADMIN_USERNAME, TEST_PASSWORD)));
    }

    @Test
    @DisplayName("测试密码错误")
    void testLoginWithWrongPassword() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                       {
                         "username": "%s",
                         "password": "wrongpassword"
                        }
                    """, ADMIN_USERNAME)))
            .andExpect(status().is4xxClientError())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("code", is(1004)));
    }

    @Test
    @DisplayName("测试用户名不存在")
    void testLoginWithNonExistentUser() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                       {
                         "username": "nonexistentuser",
                         "password": "123456"
                        }
                    """))
            .andExpect(status().is4xxClientError())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试登录参数为空")
    void testLoginWithEmptyCredentials() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                       {
                         "username": "",
                         "password": ""
                        }
                    """))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("测试退出登录")
    void testLogout() throws Exception {
        // 退出登录
        mvc.perform(post("/logout")
                .header(TOKEN_HEADER_NAME, "Bearer " + "token"));
    }

    @Test
    @DisplayName("测试获取用户信息")
    void testGetUserInfo() throws Exception {
        mvc.perform(get("/userinfo")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("username", notNullValue()))
            .andExpect(jsonPath("permissions", notNullValue()));
    }

    @Test
    @DisplayName("测试未授权访问用户信息")
    void testGetUserInfoWithoutAuth() throws Exception {
        mvc.perform(get("/userinfo"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试无效Token访问用户信息")
    void testGetUserInfoWithInvalidToken() throws Exception {
        mvc.perform(get("/userinfo")
                .header(TOKEN_HEADER_NAME, "Bearer invalid_token"))
            .andExpect(status().isUnauthorized());
    }
}
