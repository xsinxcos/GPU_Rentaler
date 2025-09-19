package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Role;

/**
 * @author cjbi
 */
public record RoleUpdated(Role role) implements DomainEvent {
}
