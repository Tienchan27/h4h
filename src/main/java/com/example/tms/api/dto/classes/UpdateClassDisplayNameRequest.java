package com.example.tms.api.dto.classes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClassDisplayNameRequest(
        @NotBlank
        @Size(max = 255)
        String displayName
) {
}
