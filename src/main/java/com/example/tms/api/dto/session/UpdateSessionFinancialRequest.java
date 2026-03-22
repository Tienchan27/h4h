package com.example.tms.api.dto.session;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateSessionFinancialRequest(
        @DecimalMin("0.00") BigDecimal tuitionAtLog,
        @DecimalMin("0.00") BigDecimal salaryRateAtLog,
        String payrollMonth,
        String note,
        @NotBlank String reason
) {
}
