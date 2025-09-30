package com.gpu.rentaler.controller;


import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.dto.RechargeQrCodeDTO;
import com.gpu.rentaler.sys.service.dto.WalletDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/wallet")
@RestController
public class WalletController {

    @RequiresPermissions("wallet:show")
    @GetMapping("/show")
    public ResponseEntity<WalletDTO> show() {
        //todo
        // 查询钱包信息的逻辑
        return ResponseEntity.ok(new WalletDTO(1L ,1L ,"1" ,0 , Instant.now()));
    }

    @RequiresPermissions("wallet:rechargeQrcode")
    @PostMapping("/recharge")
    public ResponseEntity<RechargeQrCodeDTO> rechargeQrCode() {
        //todo
        // 充值的逻辑
        return ResponseEntity.ok(new RechargeQrCodeDTO("11" ,1L ,"test"));
    }

    @RequiresPermissions("wallet:list")
    @GetMapping("/list")
    public ResponseEntity<List<WalletDTO>> list(Pageable pageable) {
        //todo
        // 查询充值记录的逻辑
        return ResponseEntity.noContent().build();
    }

}
