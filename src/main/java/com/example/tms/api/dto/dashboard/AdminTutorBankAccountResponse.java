package com.example.tms.api.dto.dashboard;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminTutorBankAccountResponse(
        UUID id,
        String bankName,
        String maskedAccountNumber,
        String accountHolderName,
        boolean primary,
        boolean verified,
        LocalDateTime verifiedAt
) {
}
