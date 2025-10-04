package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.GPUTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface GPUTaskRepository extends JpaRepository<GPUTask, Long> {
    @Query("select g from GPUTask g where g.userId = ?1 and (?2 is null or g.status = ?2) order by g.id desc ")
    Page<GPUTask> findPageByUserIdAndStatus(Long userId, @Nullable String status, Pageable pageable);

    @Query("select g from GPUTask g where (?1 is null or g.status = ?1) order by g.id desc ")
    Page<GPUTask> findPageByStatus(@Nullable String status, Pageable pageable);
}
