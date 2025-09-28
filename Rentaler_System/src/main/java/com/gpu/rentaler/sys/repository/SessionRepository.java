package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author cjbi
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Transactional
    @Modifying
    @Query("delete from Session where expireTime <= now() ")
    void deleteExpiredSession();

    @Transactional
    @Modifying
    @Query("update Session set expireTime= :expireTime, lastModifiedTime = now() where token = :token")
    void updateExpireTime(String token, LocalDateTime expireTime);

    Optional<Session> findByToken(String token);

    @Query("from Session")
    Stream<Session> findAllStream();
}
