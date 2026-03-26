package com.example.tms.api.dto.classes;

import java.time.LocalDateTime;
import java.util.UUID;

public record TutorClassApplicationResponse(
        UUID applicationId,
        UUID tutorId,
        String tutorName,
        String tutorEmail,
        String status,
        LocalDateTime appliedAt,
        LocalDateTime reviewedAt,
        String rejectionReason
) {
}
