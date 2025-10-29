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
 * 钱包控制器测试类
 *
 * @author wzq
 */
@DisplayName("钱包管理接口测试")
public class WalletControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询我的钱包")
    void testGetMyWallet() throws Exception {
        mvc.perform(get("/wallet/my")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @DisplayName("测试查询钱包余额")
    void testGetWalletBalance() throws Exception {
        mvc.perform(get("/wallet/my")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("balance", notNullValue()));
    }

    @Test
    @DisplayName("测试沙盒环境充值")
    void testSandboxRecharge() throws Exception {
        testGetWalletBalance();

        mvc.perform(post("/wallet/sandbox/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "amount": "100.00"
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试沙盒充值金额为零")
    void testSandboxRechargeWithZeroAmount() throws Exception {
        mvc.perform(post("/wallet/sandbox/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "amount": "0.00"
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试沙盒充值大额金额")
    void testSandboxRechargeWithLargeAmount() throws Exception {
        mvc.perform(post("/wallet/sandbox/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "amount": "10000.00"
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试沙盒充值负数金额")
    void testSandboxRechargeWithNegativeAmount() throws Exception {
        testGetWalletBalance();

        mvc.perform(post("/wallet/sandbox/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "amount": "-100.00"
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试沙盒充值缺少金额参数")
    void testSandboxRechargeWithoutAmount() throws Exception {
        mvc.perform(post("/wallet/sandbox/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试未授权访问钱包")
    void testGetMyWalletWithoutAuth() throws Exception {
        mvc.perform(get("/wallet/my"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试未授权进行充值")
    void testSandboxRechargeWithoutAuth() throws Exception {
        mvc.perform(post("/wallet/sandbox/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "amount": "100.00"
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }




}

