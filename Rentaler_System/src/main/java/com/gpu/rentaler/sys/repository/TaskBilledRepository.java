package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.TaskBilled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskBilledRepository extends JpaRepository<TaskBilled, Long> {

    Optional<TaskBilled> findFirstByTaskIdOrderByEndBillTimeDesc(Long taskId);
}
