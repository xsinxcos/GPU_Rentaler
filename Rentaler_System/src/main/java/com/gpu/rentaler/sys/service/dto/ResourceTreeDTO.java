package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.Resource.Type;

import java.util.List;

/**
 * @author cjbi
 */
public record ResourceTreeDTO(Long id, String name, Type type, String permission, String url, String icon,
                              List<ResourceTreeDTO> children, Long parentId, String parentName) {

}
