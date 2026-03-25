package com.example.tms.api.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteTutorRequest(
        @NotBlank
        @Email(message = "Invalid email format")
        @Size(max = 255)
        String email
) {
}
