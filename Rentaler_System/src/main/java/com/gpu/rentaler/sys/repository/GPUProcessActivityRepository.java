package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.GPUProcessActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface GPUProcessActivityRepository extends JpaRepository<GPUProcessActivity, Long> {

    @Query("select g from GPUProcessActivity g where g.deviceId = ?1 and g.containerId = ?2 and g.time > ?3")
    List<GPUProcessActivity> findByDeviceIdAndContainerIdAndTimeAfter(String deviceId, String containerId, Instant time);
}
