package com.example.tms.api.dto.session;

import java.math.BigDecimal;
import java.util.UUID;

public record TutorSessionClassOptionResponse(
        UUID id,
        String subjectName,
        BigDecimal pricePerHour,
        BigDecimal defaultSalaryRate
) {
}
