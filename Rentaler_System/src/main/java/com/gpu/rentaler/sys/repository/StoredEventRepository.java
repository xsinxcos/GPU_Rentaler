package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.StoredEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author wzq
 */
@Repository
public interface StoredEventRepository extends JpaRepository<StoredEvent, Long> {

    Page<StoredEvent> findByTypeNameInOrderByOccurredOnDesc(Set<String> typeNames, Pageable pageable);

}
