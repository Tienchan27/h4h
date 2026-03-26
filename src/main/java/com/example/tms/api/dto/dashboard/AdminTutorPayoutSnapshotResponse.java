package com.example.tms.api.dto.dashboard;

import java.math.BigDecimal;

public record AdminTutorPayoutSnapshotResponse(
        Integer year,
        Integer month,
        BigDecimal grossRevenue,
        BigDecimal netSalary,
        String status
) {
}
