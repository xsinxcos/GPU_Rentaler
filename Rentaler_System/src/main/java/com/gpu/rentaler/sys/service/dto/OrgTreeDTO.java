package com.gpu.rentaler.sys.service.dto;

import com.gpu.rentaler.sys.model.Organization;

import java.util.List;

/**
 * @author wzq
 */
public class OrgTreeDTO {
    private final Long id;
    private final String name;
    private final Organization.Type type;
    private final List<OrgTreeDTO> children;

    public OrgTreeDTO(Organization organization) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.type = organization.getType();
        this.children = organization.getChildren().stream().map(OrgTreeDTO::new).toList();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Organization.Type getType() {
        return type;
    }

    public List<OrgTreeDTO> getChildren() {
        return children;
    }

    public boolean getIsLeaf() {
        return getChildren().isEmpty();
    }

}
