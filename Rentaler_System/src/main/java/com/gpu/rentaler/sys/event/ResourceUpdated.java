package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Resource;

/**
 * @author wzq
 */
public record ResourceUpdated(Resource resource) implements DomainEvent {
}
