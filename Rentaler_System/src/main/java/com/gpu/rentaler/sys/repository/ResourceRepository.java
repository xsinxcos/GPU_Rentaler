package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author wzq
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("from Resource where id in (:resourceIds)")
    Set<Resource> findByIds(Set<Long> resourceIds);

}
