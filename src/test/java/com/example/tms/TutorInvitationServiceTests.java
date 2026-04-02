package com.example.tms;

import com.example.tms.api.dto.admin.InviteTutorResponse;
import com.example.tms.entity.TutorInvitation;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.RoleName;
import com.example.tms.entity.enums.TutorInvitationStatus;
import com.example.tms.repository.TutorInvitationRepository;
import com.example.tms.repository.UserRepository;
import com.example.tms.security.RoleGuard;
import com.example.tms.service.MailService;
import com.example.tms.service.NotificationOutboxService;
import com.example.tms.service.TutorInvitationService;
import com.example.tms.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorInvitationServiceTests {
    @Mock
    private TutorInvitationRepository tutorInvitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private RoleGuard roleGuard;
    @Mock
    private MailService mailService;
    @Mock
    private NotificationOutboxService notificationOutboxService;

    private TutorInvitationService tutorInvitationService;

    @BeforeEach
    void setUp() {
        tutorInvitationService = new TutorInvitationService(
                tutorInvitationRepository,
                userRepository,
                userRoleService,
                roleGuard,
                mailService,
                notificationOutboxService
        );
    }

    @Test
    void inviteTutorAssignsRoleForExistingUser() {
        User admin = user("admin@example.com");
        User tutor = user("teacher@example.com");
        when(tutorInvitationRepository.findByEmail("teacher@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("teacher@example.com")).thenReturn(Optional.of(tutor));

        InviteTutorResponse response = tutorInvitationService.inviteTutor(admin, "teacher@example.com");

        ArgumentCaptor<TutorInvitation> invitationCaptor = ArgumentCaptor.forClass(TutorInvitation.class);
        verify(tutorInvitationRepository).save(invitationCaptor.capture());
        TutorInvitation saved = invitationCaptor.getValue();

        assertEquals(TutorInvitationStatus.ACCEPTED, saved.getStatus());
        assertEquals(tutor.getEmail(), saved.getEmail());
        assertEquals(tutor.getId(), saved.getInvitedUser().getId());
        assertTrue(response.existingUser());
        assertTrue(response.tutorRoleAssigned());
        assertEquals("ACCEPTED", response.status());

        verify(userRoleService).ensureActiveRole(tutor, RoleName.TUTOR, admin);
        verify(mailService, never()).sendTutorInvitationEmail(any());
    }

    @Test
    void inviteTutorCreatesPendingInvitationForUnknownEmail() {
        User admin = user("admin@example.com");
        when(tutorInvitationRepository.findByEmail("newtutor@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newtutor@example.com")).thenReturn(Optional.empty());

        InviteTutorResponse response = tutorInvitationService.inviteTutor(admin, "newtutor@example.com");

        assertFalse(response.existingUser());
        assertFalse(response.tutorRoleAssigned());
        assertEquals("PENDING", response.status());
        verify(mailService).sendTutorInvitationEmail("newtutor@example.com");
    }

    @Test
    void acceptPendingInvitationPromotesUserToTutor() {
        User invitedBy = user("admin@example.com");
        User tutor = user("newtutor@example.com");
        TutorInvitation invitation = new TutorInvitation();
        invitation.setEmail("newtutor@example.com");
        invitation.setStatus(TutorInvitationStatus.PENDING);
        invitation.setInvitedBy(invitedBy);

        when(tutorInvitationRepository.findByEmailAndStatus("newtutor@example.com", TutorInvitationStatus.PENDING))
                .thenReturn(Optional.of(invitation));

        tutorInvitationService.acceptPendingInvitation(tutor);

        verify(userRoleService).ensureActiveRole(tutor, RoleName.TUTOR, invitedBy);
        verify(tutorInvitationRepository).save(eq(invitation));
        assertEquals(TutorInvitationStatus.ACCEPTED, invitation.getStatus());
        assertEquals(tutor.getId(), invitation.getInvitedUser().getId());
    }

    private User user(String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        return user;
    }
}
