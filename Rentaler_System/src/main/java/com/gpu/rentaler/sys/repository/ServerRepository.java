package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Server;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    List<Server> getServerByServerId(@Size(max = 64) @NotNull String serverId);
}
