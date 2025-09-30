package com.gpu.rentaler.sys.service.dto;

import java.time.Instant;

public record WalletDTO(Long id, Long userId,
                        String balance, Integer status, Instant lastTransactionTime) {
}
