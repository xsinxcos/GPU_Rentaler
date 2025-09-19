package com.gpu.rentaler.sys.service.dto;

import java.util.List;

/**
 * @author cjbi
 */
public record RoleDTO(Long id, String name, String description, boolean available, List<Long> resourceIds) {
}
