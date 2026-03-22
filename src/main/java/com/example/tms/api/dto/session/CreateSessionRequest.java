package com.example.tms.api.dto.session;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateSessionRequest(
        @NotNull UUID classId,
        @NotNull LocalDate date,
        @NotNull @DecimalMin("0.25") BigDecimal durationHours,
        @NotNull @DecimalMin("0.00") BigDecimal tuitionAtLog,
        @NotNull @DecimalMin("0.00") BigDecimal salaryRateAtLog,
        String payrollMonth,
        String note
) {
}
