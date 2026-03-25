package com.example.tms.api;

import com.example.tms.api.dto.admin.InviteTutorRequest;
import com.example.tms.api.dto.admin.InviteTutorResponse;
import com.example.tms.security.CurrentUserResolver;
import com.example.tms.service.TutorInvitationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/tutors")
public class AdminTutorController {
    private final TutorInvitationService tutorInvitationService;
    private final CurrentUserResolver currentUserResolver;

    public AdminTutorController(TutorInvitationService tutorInvitationService, CurrentUserResolver currentUserResolver) {
        this.tutorInvitationService = tutorInvitationService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/invite")
    public InviteTutorResponse inviteTutor(@Valid @RequestBody InviteTutorRequest request) {
        return tutorInvitationService.inviteTutor(currentUserResolver.requireUser(), request.email());
    }
}
