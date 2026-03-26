package com.example.tms.api.dto.session;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record TutorSessionClassOptionResponse(
        UUID id,
        String className,
        String subjectName,
        BigDecimal pricePerHour,
        BigDecimal defaultSalaryRate,
        List<String> studentNames
) {
}
