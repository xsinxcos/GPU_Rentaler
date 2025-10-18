package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.User;

/**
 * @author wzq
 */
public record UserDeleted(User user) implements DomainEvent {
}
