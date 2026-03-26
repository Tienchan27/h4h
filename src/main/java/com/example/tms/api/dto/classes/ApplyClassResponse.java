package com.example.tms.api.dto.classes;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplyClassResponse(
        UUID applicationId,
        UUID classId,
        String status,
        LocalDateTime appliedAt
) {
}
