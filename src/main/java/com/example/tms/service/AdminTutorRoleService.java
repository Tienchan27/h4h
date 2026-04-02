package com.example.tms.service;

import com.example.tms.entity.User;
import com.example.tms.entity.UserRole;
import com.example.tms.entity.enums.NotificationType;
import com.example.tms.entity.enums.RoleName;
import com.example.tms.entity.enums.UserRoleStatus;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.UserRepository;
import com.example.tms.repository.UserRoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminTutorRoleService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final NotificationOutboxService notificationOutboxService;

    public AdminTutorRoleService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            NotificationOutboxService notificationOutboxService
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.notificationOutboxService = notificationOutboxService;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void revokeTutorRole(User admin, UUID tutorId, String reason) {
        // Validate tutor exists
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new ApiException("Tutor not found"));

        UserRole userRole = userRoleRepository.findByUserIdAndRole(tutorId, RoleName.TUTOR)
                .orElseThrow(() -> new ApiException("Tutor role not found"));

        // Revoke only role
        userRole.setStatus(UserRoleStatus.REVOKED);
        userRole.setRevokedReason(reason);
        userRole.setUpdatedBy(admin);
        userRoleRepository.save(userRole);

        notificationOutboxService.enqueue(
                tutor,
                NotificationType.TUTOR_ROLE_REVOKED,
                "Tutor access revoked",
                "Admin revoked your tutor access. " + (reason == null || reason.isBlank() ? "" : ("Reason: " + reason.trim())),
                "user:" + tutorId
        );
    }
}

