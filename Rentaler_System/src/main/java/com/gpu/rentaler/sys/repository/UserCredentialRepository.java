package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author wzq
 */
@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    @Query("from UserCredential authCredential where authCredential.identifier=:identifier and authCredential.identityType=:identityType")
    Optional<UserCredential> findCredential(String identifier, UserCredential.IdentityType identityType);

}
