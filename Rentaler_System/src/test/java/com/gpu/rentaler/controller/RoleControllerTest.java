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
 * 角色控制器测试类
 *
 * @author wzq
 */
@DisplayName("角色管理接口测试")
public class RoleControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询角色列表")
    void testFindRoles() throws Exception {
        mvc.perform(get("/roles")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询角色")
    void testFindRolesWithPagination() throws Exception {
        mvc.perform(get("/roles")
                .param("page", "0")
                .param("size", "10")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试创建角色")
    void testCreateRole() throws Exception {
        String roleName = "测试角色_" + System.currentTimeMillis();
        mvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "description": "这是一个测试角色",
                      "available": true
                    }
                    """, roleName)))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试创建角色-角色名已存在")
    void testCreateRoleWithDuplicateName() throws Exception {
        mvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "name": "管理员",
                      "description": "重复角色",
                      "available": true
                    }
                    """))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("测试更新角色")
    void testUpdateRole() throws Exception {
        mvc.perform(put("/roles/{roleId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "name": "管理员更新",
                      "description": "更新的描述",
                      "available": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试删除角色")
    void testDeleteRole() throws Exception {
        // 先创建一个角色
        String roleName = "待删除角色_" + System.currentTimeMillis();
        mvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content(String.format("""
                    {
                      "name": "%s",
                      "description": "待删除",
                      "available": true
                    }
                    """, roleName)))
            .andExpect(status().isCreated());

        // 删除角色
        mvc.perform(delete("/roles/{roleId}", 2)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("测试分配角色权限")
    void testAssignPermissions() throws Exception {
        mvc.perform(put("/roles/{roleId}/resources", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TOKEN_HEADER_NAME, TOKEN)
                .content("""
                    {
                      "resourceIds": [1, 2, 3, 4]
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试查询角色的权限资源")
    void testGetRoleResources() throws Exception {
        mvc.perform(get("/roles", 1)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @DisplayName("测试未授权访问角色列表")
    void testFindRolesWithoutAuth() throws Exception {
        mvc.perform(get("/roles"))
            .andExpect(status().isUnauthorized());
    }
}
