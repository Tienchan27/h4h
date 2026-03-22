package com.example.tms.api.dto.dashboard;

import java.math.BigDecimal;

public record TutorDashboardResponse(
        int year,
        int month,
        BigDecimal grossRevenue,
        BigDecimal netSalary,
        String status
) {
}
