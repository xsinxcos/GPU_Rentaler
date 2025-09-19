package com.gpu.rentaler.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.gpu.rentaler.Admin3Properties;
import com.gpu.rentaler.Admin3Properties.Event;
import com.gpu.rentaler.common.CollectionUtils;
import com.gpu.rentaler.common.JsonUtils;
import com.gpu.rentaler.common.StringUtils;
import com.gpu.rentaler.sys.model.StoredEvent;
import com.gpu.rentaler.sys.repository.StoredEventRepository;
import com.gpu.rentaler.sys.service.dto.LogDTO;
import com.gpu.rentaler.sys.service.dto.PageDTO;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cjbi
 */
@Service
public class LogService {

  private final StoredEventRepository storedEventRepository;

  private final Admin3Properties admin3Properties;

  public LogService(StoredEventRepository storedEventRepository, Admin3Properties admin3Properties) {
    this.storedEventRepository = storedEventRepository;
    this.admin3Properties = admin3Properties;
  }

  public PageDTO<LogDTO> findLogs(Set<String> typeNames, Pageable pageable) {
    Map<String, Event> eventProps = admin3Properties.getEvents();
    Page<StoredEvent> page = storedEventRepository.findByTypeNameInOrderByOccurredOnDesc(
      CollectionUtils.isEmpty(typeNames) ? eventProps.keySet() : typeNames,
      pageable
    );
    return new PageDTO<>(page.getContent().stream()
      .map(e -> new LogDTO(e.getId(),
          StringUtils.simpleRenderTemplate(eventProps.get(e.getTypeName()).getLogTemplate(), JsonUtils.parseToMap(e.getEventBody())),
          e.getEventBody(),
          e.getTypeName(),
          e.getOccurredOn(),
          e.getUser()
        )
      )
      .collect(Collectors.toList()), page.getTotalElements());
  }

  public void cleanLogs() {
    storedEventRepository.deleteAll();
  }
}
