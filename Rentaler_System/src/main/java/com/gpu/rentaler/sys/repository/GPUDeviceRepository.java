package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.GPUDevice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GPUDeviceRepository extends JpaRepository<GPUDevice, Long> {
    List<GPUDevice> findAllByServerId(@NotNull Long serverId);
}
