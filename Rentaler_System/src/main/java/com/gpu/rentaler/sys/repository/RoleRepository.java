package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Role;
import com.gpu.rentaler.sys.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author wzq
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select distinct u FROM User u join u.roles r where r.id=:roleId")
    Page<User> findRoleUsers(Long roleId, Pageable pageable);

}
