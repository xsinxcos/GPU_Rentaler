package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Organization;

/**
 * @author wzq
 */
public record OrganizationDeleted(Organization organization) implements DomainEvent {
}
