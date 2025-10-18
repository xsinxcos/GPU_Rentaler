package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.GPURealDevices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface GPURealDevicesRepository extends JpaRepository<GPURealDevices, Long> {
    @Query("select g from GPURealDevices g where g.serverId = ?1")
    List<GPURealDevices> findByServerId(Long serverId);

    @Query("select g from GPURealDevices g where g.deviceId in ?1")
    List<GPURealDevices> findByDeviceIdIn(Collection<String> deviceIds);

    @Transactional
    @Modifying
    @Query("update GPURealDevices g set g.isRentable = ?1 where g.deviceId in ?2")
    void updateIsRentableByDeviceIdIn(Boolean isRentable, Collection<String> deviceIds);

    @Query("select g from GPURealDevices g where g.serverId = ?1 and g.status = ?2")
    Page<GPURealDevices> findByServerIdAndStatus(@Nullable Long serverId, @Nullable String status, Pageable pageable);

    @Query("select g from GPURealDevices g where g.status = ?1 and g.isRentable = ?2")
    List<GPURealDevices> findByStatusAndIsRentable(String status, Boolean isRentable);

    @Query("select g from GPURealDevices g where g.status = ?1 or g.isRentable = ?2")
    List<GPURealDevices> findByStatusOrIsRentable(String status, Boolean isRentable);
}
