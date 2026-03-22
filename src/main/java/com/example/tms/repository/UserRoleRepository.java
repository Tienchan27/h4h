package com.example.tms.repository;

import com.example.tms.entity.UserRole;
import com.example.tms.entity.enums.RoleName;
import com.example.tms.entity.enums.UserRoleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    @Query("""
           select ur from UserRole ur
           where ur.user.id = :userId and ur.status = :status
           """)
    List<UserRole> findByUserIdAndStatus(UUID userId, UserRoleStatus status);

    @Query("""
           select case when count(ur) > 0 then true else false end
           from UserRole ur
           where ur.user.id = :userId and ur.role.name = :role and ur.status = :status
           """)
    boolean hasRole(UUID userId, RoleName role, UserRoleStatus status);

    @Query("""
           select ur from UserRole ur
           where ur.role.name = :role and ur.status = :status
           """)
    List<UserRole> findByRoleAndStatus(RoleName role, UserRoleStatus status);
}
