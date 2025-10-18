package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.User;

import java.time.LocalDateTime;

/**
 * @author wzq
 */
public record OrgUserDTO(Long id,
                         String username,
                         String avatar,
                         User.Gender gender,
                         User.State state,
                         String orgFullName,
                         LocalDateTime createdTime) {
}
