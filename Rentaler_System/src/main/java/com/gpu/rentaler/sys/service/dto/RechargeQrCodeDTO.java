package com.gpu.rentaler.sys.service.dto;

public record RechargeQrCodeDTO(
    String base64Image,
    Long orderId,
    String rechargeWay
) {
}
