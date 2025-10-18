package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.StorageConfig;

/**
 * @author wzq
 */
public record StorageConfigMarkedAsDefault(StorageConfig config) implements DomainEvent {
}
