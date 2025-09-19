package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Resource;

/**
 * @author cjbi
 */
public record ResourceUpdated(Resource resource) implements DomainEvent {
}
