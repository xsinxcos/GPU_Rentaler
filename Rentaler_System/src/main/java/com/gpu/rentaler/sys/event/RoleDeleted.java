package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Role;

/**
 * @author wzq
 */
public record RoleDeleted(Role role) implements DomainEvent {
}
