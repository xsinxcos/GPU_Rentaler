package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wzq
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Query("from Organization org where org.parent.id=:parentId")
    List<Organization> findByParentId(Long parentId);
}
