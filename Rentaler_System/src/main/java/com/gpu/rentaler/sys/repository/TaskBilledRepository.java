package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.TaskBilled;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskBilledRepository extends JpaRepository<TaskBilled, Long> {

    Optional<TaskBilled> findFirstByTaskIdOrderByEndBillTimeAsc(Long taskId);
}
