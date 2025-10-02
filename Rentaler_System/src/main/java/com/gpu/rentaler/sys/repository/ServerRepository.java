package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ServerRepository extends JpaRepository<Server, Long> {
    @Query("select s from Server s")
    Page<Server> findServers(Pageable pageable);

}
