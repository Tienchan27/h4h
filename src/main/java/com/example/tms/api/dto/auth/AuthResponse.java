package com.example.tms.api.dto.auth;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        String accessToken,
        String refreshToken,
        boolean needsTutorOnboarding
) {
}
