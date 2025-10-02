package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.GPUDevice;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GPUDeviceRepository extends JpaRepository<GPUDevice, Long> {
    List<GPUDevice> findAllByServerId(@NotNull Long serverId);

    void deleteByDeviceId(String deviceId);


    @Query("""
    select gd from GPUDevice gd where (:serverId is null or gd.serverId = :serverId)
    and (:status is null or gd.status = :status)
""")
    Page<GPUDevice> findGPUDevices(@Param("serverId") Long serverId, @Param("status") String status, Pageable pageable);

    @Query("select g from GPUDevice g where g.status = ?1 and g.isRentable = ?2")
    Page<GPUDevice> findRentableGPUCanDevice(String status, Boolean isRentable, Pageable pageable);

    List<GPUDevice> findByDeviceId(String deviceId);

    @Transactional
    @Modifying
    @Query("update GPUDevice g set g.isRentable = ?1 where g.deviceId = ?2")
    int updateIsRentableByDeviceId(Boolean isRentable, String deviceId);
}
