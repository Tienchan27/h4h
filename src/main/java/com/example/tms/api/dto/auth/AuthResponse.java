package com.example.tms.api.dto.auth;

import com.example.tms.entity.enums.RoleName;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        String name,
        String accessToken,
        String refreshToken,
        boolean needsProfileCompletion,
        boolean needsTutorOnboarding,
        List<RoleName> roles,
        RoleName activeRole
) {
}
