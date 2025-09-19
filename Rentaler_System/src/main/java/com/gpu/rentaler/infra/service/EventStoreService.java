package com.gpu.rentaler.infra.service;

import com.gpu.rentaler.common.*;
import org.springframework.stereotype.Service;
import com.gpu.rentaler.common.*;
import com.gpu.rentaler.sys.model.StoredEvent;
import com.gpu.rentaler.sys.repository.StoredEventRepository;
import com.gpu.rentaler.sys.repository.UserRepository;
import com.gpu.rentaler.sys.service.dto.UserinfoDTO;

/**
 * @author cjbi
 */
@Service
public class EventStoreService implements EventStore {

  private final StoredEventRepository storedEventRepository;
  private final UserRepository userRepository;

  public EventStoreService(StoredEventRepository storedEventRepository, UserRepository userRepository) {
    this.storedEventRepository = storedEventRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void append(DomainEvent aDomainEvent) {
    StoredEvent storedEvent = new StoredEvent();
    storedEvent.setEventBody(JsonUtils.stringify(aDomainEvent));
    storedEvent.setOccurredOn(aDomainEvent.occurredOn());
    storedEvent.setTypeName(aDomainEvent.getClass().getTypeName());
    UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
    if (userInfo != null) {
      storedEvent.setUser(userRepository.getReferenceById(userInfo.userId()));
    }
    storedEventRepository.save(storedEvent);
  }

}
