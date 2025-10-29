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
 * 组织架构控制器测试类
 *
 * @author wzq
 */
@DisplayName("组织架构管理接口测试")
public class OrganizationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("测试创建组织架构")
    void testCreateOrganization() throws Exception {
        String orgName = "测试组织_" + System.currentTimeMillis();
        mvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "parentId": 1,
                      "type": 0
                    }
                    """, orgName)))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试创建根组织")
    void testCreateRootOrganization() throws Exception {
        String orgName = "根组织_" + System.currentTimeMillis();
        mvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "type": 0,
                      "parentId": 1
                    }
                    """, orgName)))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("name", is(orgName)));
    }

    @Test
    @DisplayName("测试创建组织-缺少必填字段")
    void testCreateOrganizationWithMissingFields() throws Exception {
        mvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "type": 0
                    }
                    """))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("测试更新组织架构")
    void testUpdateOrganization() throws Exception {
        mvc.perform(put("/organizations/{organizationId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "name": "更新的组织名称",
                      "type": 0,
                      "parentId": 1
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试删除组织架构")
    void testDeleteOrganization() throws Exception {
        // 先创建一个组织
        String orgName = "待删除组织_" + System.currentTimeMillis();
        mvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "parentId": 1,
                      "type": 0
                    }
                    """, orgName)))
            .andExpect(status().isCreated());

        // 删除组织
        mvc.perform(delete("/organizations/{organizationId}", 3)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("测试查询组织树")
    void testGetOrganizationTree() throws Exception {
        mvc.perform(get("/organizations/tree")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @DisplayName("测试查询组织下的用户")
    void testGetOrganizationUsers() throws Exception {
        mvc.perform(get("/organizations/{organizationId}/users", 1)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", notNullValue()));
    }
}
