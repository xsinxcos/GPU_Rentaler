package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.Role;
import com.gpu.rentaler.sys.model.User;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author cjbi
 */
public record RoleUserDTO(Long id,
                          String username,
                          String avatar,
                          User.Gender gender,
                          User.State state,
                          Set<Role> roles,
                          LocalDateTime createdTime) {

}
