package com.gpu.rentaler.controller;

import com.gpu.rentaler.common.Constants;
import com.gpu.rentaler.common.FileUtils;
import com.gpu.rentaler.common.SessionItemHolder;
import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.SessionService;
import com.gpu.rentaler.sys.service.StorageService;
import com.gpu.rentaler.sys.service.dto.PageDTO;
import com.gpu.rentaler.sys.service.dto.StorageFileDTO;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/docker/image")
public class DockerImageController {

    @Resource
    private StorageService storageService;

    @Resource
    private SessionService sessionService;

    @PostMapping("/upload")
    @RequiresPermissions("dockerImage:upload")
    public ResponseEntity<UploadImageDTO> uploadImage(@RequestParam("files") MultipartFile[] files) throws IOException {

        List<StorageFileDTO> ok = new ArrayList<>();
        List<StorageFail> fails = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            boolean dockerImage = FileUtils.isDockerImage(file.getInputStream());
            if (dockerImage) {
                StorageFileDTO storageFile = storageService.store(null, file.getInputStream(), file.getSize(), file.getContentType(), originalFilename);
                ok.add(storageFile);
            } else {
                StorageFail fail = new StorageFail(file.getName(), "请检查格式，非 DockerImage 文件");
                fails.add(fail);
            }
        }
        UploadImageDTO uploadImageDTO = new UploadImageDTO(ok, fails);
        return ResponseEntity.ok(uploadImageDTO);
    }

    @GetMapping("/me")
    @RequiresPermissions("dockerImage:me")
    public ResponseEntity<PageDTO<StorageFileDTO>> getMyImage(Pageable page) {
        UserinfoDTO item = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        String username = item.username();
        PageDTO<StorageFileDTO> dto = storageService.getImageFileByCreateName(page, username);
        return ResponseEntity.ok(dto);
    }


    public record UploadImageDTO(List<StorageFileDTO> ok, List<StorageFail> fails) {
    }

    public record StorageFail(String filename, String reason) {
    }
}
