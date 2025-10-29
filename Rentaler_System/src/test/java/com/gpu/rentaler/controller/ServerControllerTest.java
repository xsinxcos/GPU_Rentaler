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
 * 服务器控制器测试类
 *
 * @author wzq
 */
@DisplayName("服务器管理接口测试")
public class ServerControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询服务器列表")
    void testFindServers() throws Exception {
        mvc.perform(get("/servers")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询服务器")
    void testFindServersWithPagination() throws Exception {
        mvc.perform(get("/servers")
                .param("page", "0")
                .param("size", "10")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试修改服务器信息")
    void testModifyServer() throws Exception {
        mvc.perform(post("/{serverId}/modify", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "hostname": "gpu-server-01",
                      "ipAddress": "192.168.1.100",
                      "location": "北京机房A区",
                      "cpuModel": "Intel Xeon Gold 6248R",
                      "cpuCores": 48,
                      "ramTotalGb": 256,
                      "storageTotalGb": 4000,
                      "gpuSlots": 8,
                      "status": "ONLINE",
                      "datacenter": "Beijing-DC1",
                      "region": "cn-north-1"
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试修改服务器-缺少必填字段")
    void testModifyServerWithMissingFields() throws Exception {
        mvc.perform(post("/{serverId}/modify", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "hostname": "gpu-server-01"
                    }
                    """));
    }

    @Test
    @DisplayName("测试修改不存在的服务器")
    void testModifyNonExistentServer() throws Exception {
        mvc.perform(post("/{serverId}/modify", 999999)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "hostname": "gpu-server-01",
                      "ipAddress": "192.168.1.100",
                      "location": "北京机房A区",
                      "cpuModel": "Intel Xeon",
                      "cpuCores": 48,
                      "ramTotalGb": 256,
                      "storageTotalGb": 4000,
                      "gpuSlots": 8,
                      "status": "ONLINE",
                      "datacenter": "Beijing-DC1",
                      "region": "cn-north-1"
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试删除服务器")
    void testDeleteServer() throws Exception {
        mvc.perform(delete("/{serverId}/delete", 1)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("测试删除不存在的服务器")
    void testDeleteNonExistentServer() throws Exception {
        mvc.perform(delete("/{serverId}/delete", 999999)
                .header(TOKEN_HEADER_NAME, TOKEN));
    }

    @Test
    @DisplayName("测试按状态查询服务器")
    void testFindServersByStatus() throws Exception {
        mvc.perform(get("/servers")
                .param("status", "ONLINE")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按数据中心查询服务器")
    void testFindServersByDatacenter() throws Exception {
        mvc.perform(get("/servers")
                .param("datacenter", "Beijing-DC1")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按区域查询服务器")
    void testFindServersByRegion() throws Exception {
        mvc.perform(get("/servers")
                .param("region", "cn-north-1")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试未授权访问服务器列表")
    void testFindServersWithoutAuth() throws Exception {
        mvc.perform(get("/servers"))
            .andExpect(status().isUnauthorized());
    }

}

