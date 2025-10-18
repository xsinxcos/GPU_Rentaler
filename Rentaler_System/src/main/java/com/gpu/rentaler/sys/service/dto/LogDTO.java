package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.User;

import java.time.LocalDateTime;

/**
 * @author wzq
 */
public record LogDTO(Long id, String content, String eventBody, String typeName, LocalDateTime occurredOn, User user) {
}
