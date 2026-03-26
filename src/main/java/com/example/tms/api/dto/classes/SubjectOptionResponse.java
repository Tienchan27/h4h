package com.example.tms.api.dto.classes;

import java.math.BigDecimal;
import java.util.UUID;

public record SubjectOptionResponse(
        UUID id,
        String name,
        BigDecimal defaultPricePerHour
) {
}
