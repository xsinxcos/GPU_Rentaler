package com.gpu.rentaler.infra;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import com.gpu.rentaler.common.DomainEvent;
import com.gpu.rentaler.common.DomainEventPublisher;
import com.gpu.rentaler.common.EventStore;
import com.gpu.rentaler.sys.event.ResourceDeleted;
import com.gpu.rentaler.sys.event.ResourceUpdated;
import com.gpu.rentaler.sys.event.RoleDeleted;
import com.gpu.rentaler.sys.event.RoleUpdated;
import com.gpu.rentaler.sys.service.SessionService;

/**
 * 通用事件处理拦截器，
 *
 * @author cjbi
 */
public class EventSubscribesInterceptor implements HandlerInterceptor {
  private final EventStore eventStore;
  private final SessionService sessionService;

  public EventSubscribesInterceptor(EventStore eventStore, SessionService sessionService) {
    this.eventStore = eventStore;
    this.sessionService = sessionService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    DomainEventPublisher.instance().reset();
    DomainEventPublisher.instance().subscribe(DomainEvent.class, eventStore::append);
    //发生以下事件, 刷新会话
    DomainEventPublisher.instance().subscribe(RoleUpdated.class, event -> sessionService.refresh());
    DomainEventPublisher.instance().subscribe(RoleDeleted.class, event -> sessionService.refresh());
    DomainEventPublisher.instance().subscribe(ResourceUpdated.class, event -> sessionService.refresh());
    DomainEventPublisher.instance().subscribe(ResourceDeleted.class, event -> sessionService.refresh());
    return true;
  }


}
