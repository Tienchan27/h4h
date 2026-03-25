package com.example.tms.api.dto.admin;

public record InviteTutorResponse(
        String email,
        String status,
        boolean existingUser,
        boolean tutorRoleAssigned,
        String message
) {
}
