package com.example.tms.api.dto.session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SessionListItemResponse(
        UUID id,
        UUID classId,
        LocalDate date,
        BigDecimal durationHours,
        Long tuitionAtLog,
        BigDecimal salaryRateAtLog,
        String payrollMonth,
        String note
) {
}

