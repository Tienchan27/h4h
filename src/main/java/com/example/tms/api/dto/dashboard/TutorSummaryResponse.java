package com.example.tms.api.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record TutorSummaryResponse(
        UUID tutorId,
        String tutorEmail,
        BigDecimal grossRevenue,
        BigDecimal netSalary,
        String payoutStatus
) {
}
