package com.example.tms.service;

import com.example.tms.api.dto.admin.InviteTutorResponse;
import com.example.tms.entity.TutorInvitation;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.RoleName;
import com.example.tms.entity.enums.TutorInvitationStatus;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.TutorInvitationRepository;
import com.example.tms.repository.UserRepository;
import com.example.tms.security.RoleGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TutorInvitationService {
    private final TutorInvitationRepository tutorInvitationRepository;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final RoleGuard roleGuard;
    private final MailService mailService;
    private final NotificationService notificationService;

    public TutorInvitationService(
            TutorInvitationRepository tutorInvitationRepository,
            UserRepository userRepository,
            UserRoleService userRoleService,
            RoleGuard roleGuard,
            MailService mailService,
            NotificationService notificationService
    ) {
        this.tutorInvitationRepository = tutorInvitationRepository;
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.roleGuard = roleGuard;
        this.mailService = mailService;
        this.notificationService = notificationService;
    }

    @Transactional
    public InviteTutorResponse inviteTutor(User admin, String email) {
        roleGuard.requireRole(admin, RoleName.ADMIN);
        String normalizedEmail = normalizeEmail(email);

        TutorInvitation invitation = tutorInvitationRepository.findByEmail(normalizedEmail)
                .orElseGet(TutorInvitation::new);
        invitation.setEmail(normalizedEmail);
        invitation.setInvitedBy(admin);

        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRoleService.ensureActiveRole(user, RoleName.TUTOR, admin);
            invitation.setStatus(TutorInvitationStatus.ACCEPTED);
            invitation.setInvitedUser(user);
            invitation.setAcceptedAt(LocalDateTime.now());
            tutorInvitationRepository.save(invitation);

            return new InviteTutorResponse(
                    normalizedEmail,
                    TutorInvitationStatus.ACCEPTED.name(),
                    true,
                    true,
                    "Tutor role granted successfully"
            );
        }

        invitation.setStatus(TutorInvitationStatus.PENDING);
        invitation.setInvitedUser(null);
        invitation.setAcceptedAt(null);
        tutorInvitationRepository.save(invitation);
        mailService.sendTutorInvitationEmail(normalizedEmail);

        return new InviteTutorResponse(
                normalizedEmail,
                TutorInvitationStatus.PENDING.name(),
                false,
                false,
                "Invitation sent. Tutor role will be granted after registration."
        );
    }

    @Transactional
    public void acceptPendingInvitation(User user) {
        String normalizedEmail = normalizeEmail(user.getEmail());
        TutorInvitation invitation = tutorInvitationRepository
                .findByEmailAndStatus(normalizedEmail, TutorInvitationStatus.PENDING)
                .orElse(null);
        if (invitation == null) {
            return;
        }

        userRoleService.ensureActiveRole(user, RoleName.TUTOR, invitation.getInvitedBy());
        invitation.setStatus(TutorInvitationStatus.ACCEPTED);
        invitation.setInvitedUser(user);
        invitation.setAcceptedAt(LocalDateTime.now());
        tutorInvitationRepository.save(invitation);

        if (invitation.getInvitedBy() != null) {
            notificationService.notifyUser(
                    invitation.getInvitedBy(),
                    com.example.tms.entity.enums.NotificationType.TUTOR_INVITATION_ACCEPTED,
                    "Tutor invitation accepted",
                    "User " + user.getEmail() + " accepted tutor invitation."
            );
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ApiException("Email is required");
        }
        return email.trim().toLowerCase();
    }
}
