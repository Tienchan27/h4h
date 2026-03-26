package com.example.tms.api.dto.dashboard;

import java.util.List;
import java.util.UUID;

public record AdminTutorDetailResponse(
        UUID tutorId,
        String name,
        String email,
        String phoneNumber,
        String facebookUrl,
        String address,
        AdminTutorPayoutSnapshotResponse payout,
        List<AdminTutorBankAccountResponse> bankAccounts,
        List<TutorClassOverviewResponse> managedClasses
) {
}
