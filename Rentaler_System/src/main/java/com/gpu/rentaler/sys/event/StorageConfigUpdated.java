package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.model.StorageConfig;

/**
 * @author cjbi
 */
public record StorageConfigUpdated(StorageConfig config) implements DomainEvent {
}
