package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Resource;

/**
 * @author wzq
 */
public record ResourceCreated(Resource resource) implements DomainEvent {
}
