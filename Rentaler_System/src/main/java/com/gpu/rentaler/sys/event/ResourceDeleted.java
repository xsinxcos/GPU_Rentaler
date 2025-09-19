package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.Resource;

/**
 * @author cjbi
 */
public record ResourceDeleted(Resource resource) implements DomainEvent {
}
