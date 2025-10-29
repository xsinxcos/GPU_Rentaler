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
 * 资源控制器测试类
 *
 * @author wzq
 */
@DisplayName("资源管理接口测试")
public class ResourceControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("测试创建资源")
    void testCreateResource() throws Exception {
        String resourceName = "测试资源_" + System.currentTimeMillis();
        mvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "permission": "test:resource",
                      "type": 1,
                      "url": "/test",
                      "icon": "TestIcon",
                      "parentId": 1
                    }
                    """, resourceName)))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("name", is(resourceName)));
    }

    @Test
    @DisplayName("测试创建资源-缺少必填字段")
    void testCreateResourceWithMissingFields() throws Exception {
        mvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "name": "测试资源"
                    }
                    """))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("测试更新资源")
    void testUpdateResource() throws Exception {
        mvc.perform(put("/resources/{resourceId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "name": "更新的资源",
                      "permission": "updated:resource",
                      "type": 1,
                      "url": "/updated",
                      "icon": "UpdatedIcon",
                      "parentId": 1
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试删除资源")
    void testDeleteResource() throws Exception {
        // 先创建一个资源
        String resourceName = "待删除资源_" + System.currentTimeMillis();
        mvc.perform(post("/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "permission": "delete:test",
                      "type": 1,
                      "parentId": 1
                    }
                    """, resourceName)))
            .andExpect(status().isCreated());

        // 删除资源
        mvc.perform(delete("/resources/{resourceId}", 4)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isNoContent());
    }


}
