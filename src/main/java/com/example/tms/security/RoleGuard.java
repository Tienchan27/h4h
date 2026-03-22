package com.example.tms.security;

import com.example.tms.entity.User;
import com.example.tms.entity.enums.RoleName;
import com.example.tms.entity.enums.UserRoleStatus;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.UserRoleRepository;
import org.springframework.stereotype.Component;

@Component
public class RoleGuard {
    private final UserRoleRepository userRoleRepository;

    public RoleGuard(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public void requireRole(User user, RoleName roleName) {
        boolean hasRole = userRoleRepository.hasRole(user.getId(), roleName, UserRoleStatus.ACTIVE);
        if (!hasRole) {
            throw new ApiException("Forbidden for role " + roleName);
        }
    }
}
