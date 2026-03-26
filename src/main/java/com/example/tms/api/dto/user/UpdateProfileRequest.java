package com.example.tms.api.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 100)
        String name,

        @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Invalid Vietnamese phone number format")
        @Size(max = 20)
        String phoneNumber,

        @Pattern(regexp = "^https?://(www\\.)?(facebook|fb)\\.com/.+$", message = "Invalid Facebook URL")
        @Size(max = 255)
        String facebookUrl,

        @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Invalid Vietnamese phone number format")
        @Size(max = 20)
        String parentPhone,

        @Size(max = 500)
        String address
) {
}
