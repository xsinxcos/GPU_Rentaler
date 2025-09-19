package com.gpu.rentaler.sys.event;

import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;

/**
 * @author cjbi
 */
public record UserLoggedIn(UserinfoDTO userinfo, String ip) implements DomainEvent {
}
