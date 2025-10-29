package com.gpu.rentaler.controller;

import com.gpu.rentaler.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static com.gpu.rentaler.Constants.TOKEN;
import static com.gpu.rentaler.Constants.TOKEN_HEADER_NAME;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Docker镜像控制器测试类
 *
 * @author wzq
 */
@DisplayName("Docker镜像管理接口测试")
public class DockerImageControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("测试查询我的镜像列表")
    void testGetMyImages() throws Exception {
        mvc.perform(get("/docker/image/me")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试分页查询我的镜像")
    void testGetMyImagesWithPagination() throws Exception {
        mvc.perform(get("/docker/image/me")
                .param("page", "0")
                .param("size", "10")
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试上传Docker镜像")
    void testUploadDockerImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "test-image.tar",
            "application/x-tar",
            "fake docker image content".getBytes()
        );

        mvc.perform(multipart("/docker/image/upload")
                .file(file)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("ok", notNullValue()))
            .andExpect(jsonPath("fails", notNullValue()));
    }

    @Test
    @DisplayName("测试上传多个Docker镜像")
    void testUploadMultipleDockerImages() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
            "files",
            "test-image1.tar",
            "application/x-tar",
            "fake docker image 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "files",
            "test-image2.tar",
            "application/x-tar",
            "fake docker image 2".getBytes()
        );

        mvc.perform(multipart("/docker/image/upload")
                .file(file1)
                .file(file2)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("ok", isA(Iterable.class)))
            .andExpect(jsonPath("fails", isA(Iterable.class)));
    }

    @Test
    @DisplayName("测试上传非Docker镜像文件")
    void testUploadNonDockerImageFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "test.txt",
            "text/plain",
            "not a docker image".getBytes()
        );

        mvc.perform(multipart("/docker/image/upload")
                .file(file)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("fails", hasSize(greaterThan(0))))
            .andExpect(jsonPath("fails[0].reason", containsString("非 DockerImage 文件")));
    }

    @Test
    @DisplayName("测试上传空文件")
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "empty.tar",
            "application/x-tar",
            new byte[0]
        );

        mvc.perform(multipart("/docker/image/upload")
                .file(file)
                .header(TOKEN_HEADER_NAME, TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("测试未授权访问我的镜像")
    void testGetMyImagesWithoutAuth() throws Exception {
        mvc.perform(get("/docker/image/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试未授权上传镜像")
    void testUploadImageWithoutAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "test-image.tar",
            "application/x-tar",
            "fake docker image".getBytes()
        );

        mvc.perform(multipart("/docker/image/upload")
                .file(file))
            .andExpect(status().isUnauthorized());
    }

}

