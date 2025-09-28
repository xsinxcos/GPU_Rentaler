package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Organization;
import com.gpu.rentaler.sys.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author cjbi
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("from User where id in (:userIds)")
    Set<User> findByIds(Set<Long> userIds);

    @Query("""
        from User user where (user.organization=:organization or user.organization.parentIds like concat(:orgParentIds, '%'))
        and (:username is null or user.username=:username)
        and (:state is null or user.state=:state)
        """)
    Page<User> findOrgUsers(Pageable pageable, String username, User.State state, Organization organization, String orgParentIds);

    @Query("select count(user.id) from User user where user.organization=:organization or user.organization.parentIds like concat(:orgParentIds, '%')")
    long countOrgUsers(Organization organization, String orgParentIds);
}
