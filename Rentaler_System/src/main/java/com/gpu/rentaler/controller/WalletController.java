package com.gpu.rentaler.controller;


import com.gpu.rentaler.common.authz.RequiresPermissions;
import com.gpu.rentaler.sys.service.SessionService;
import com.gpu.rentaler.sys.service.WalletService;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;
import com.gpu.rentaler.sys.service.dto.WalletDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/wallet")
@RestController
public class WalletController {
    @Resource
    private SessionService sessionService;

    @Resource
    private WalletService walletService;

    @RequiresPermissions("wallet:my")
    @GetMapping("/my")
    public ResponseEntity<WalletDTO> show(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer", "").trim();
        UserinfoDTO userInfo = sessionService.getLoginUserInfo(token);
        WalletDTO walletInfo = walletService.getWalletInfo(userInfo.userId());
        return ResponseEntity.ok(walletInfo);
    }

//    @RequiresPermissions("wallet:rechargeQrcode")
//    @PostMapping("/rechargeQrcode")
//    public ResponseEntity<RechargeQrCodeDTO> rechargeQrCode(HttpServletRequest request) {
//        //todo
//        // 充值二维码生成的逻辑
//        return ResponseEntity.ok(new RechargeQrCodeDTO("11", 1L, "test"));
//    }
//
//    @RequiresPermissions("wallet:list")
//    @GetMapping("/list")
//    public ResponseEntity<List<WalletDTO>> list(Pageable pageable) {
//        //todo
//        // 查询充值记录的逻辑
//        return ResponseEntity.noContent().build();
//    }


    @RequiresPermissions("wallet:sandbox:recharge")
    @PostMapping("/sandbox/recharge")
    public ResponseEntity<Void> rechargeSandbox(HttpServletRequest request , @RequestBody ChargeSandboxRequest chargeSandboxRequest) {
        String token = request.getHeader("Authorization").replace("Bearer", "").trim();
        UserinfoDTO userInfo = sessionService.getLoginUserInfo(token);
        walletService.recharge(userInfo.userId() ,chargeSandboxRequest.amount);
        return ResponseEntity.ok().build();
    }

    public record ChargeSandboxRequest(String amount){}
}
