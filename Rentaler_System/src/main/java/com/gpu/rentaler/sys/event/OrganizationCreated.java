package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Organization;

/**
 * @author cjbi
 */
public record OrganizationCreated(Organization organization) implements DomainEvent {
}
