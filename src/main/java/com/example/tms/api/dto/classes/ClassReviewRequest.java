package com.example.tms.api.dto.classes;

import jakarta.validation.constraints.Size;

public record ClassReviewRequest(
        @Size(max = 1000)
        String reason
) {
}
