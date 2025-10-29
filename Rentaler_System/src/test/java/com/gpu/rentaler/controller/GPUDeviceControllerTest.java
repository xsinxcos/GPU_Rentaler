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
 * GPU设备控制器测试类
 *
 * @author wzq
 */
@DisplayName("GPU设备管理接口测试")
public class GPUDeviceControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询GPU设备列表")
    void testFindGPUDevices() throws Exception {
        mvc.perform(get("/gpu/devices")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询GPU设备")
    void testFindGPUDevicesWithPagination() throws Exception {
        mvc.perform(get("/gpu/devices")
                .param("page", "0")
                .param("size", "10")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按状态查询GPU设备")
    void testFindGPUDevicesByStatus() throws Exception {
        mvc.perform(get("/gpu/devices")
                .param("status", "AVAILABLE")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试按服务器ID查询GPU设备")
    void testFindGPUDevicesByServerId() throws Exception {
        mvc.perform(get("/gpu/devices")
                .param("serverId", "1")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试更新GPU设备")
    void testUpdateGPUDevice() throws Exception {
        mvc.perform(post("/gpu/{deviceId}/devices", "DEV-4090-001")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "deviceIndex": 0,
                      "brand": "NVIDIA",
                      "model": "RTX 4090",
                      "memoryTotal": 100000,
                      "architecture": "Ada Lovelace",
                      "memoryType": "GDDR6X",
                      "status": "AVAILABLE",
                      "isRentable": true,
                      "hourlyRate": "5.00",
                      "totalRuntimeHours": "100.0",
                      "totalRevenue": "500.00"
                    }
                    """))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("测试释放GPU设备")
    void testReleaseGPUDevice() throws Exception {
        mvc.perform(delete("/gpu/{deviceId}/release", "DEV-4090-001")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("测试未授权访问GPU设备列表")
    void testFindGPUDevicesWithoutAuth() throws Exception {
        mvc.perform(get("/gpu/devices"))
            .andExpect(status().isUnauthorized());
    }

}

