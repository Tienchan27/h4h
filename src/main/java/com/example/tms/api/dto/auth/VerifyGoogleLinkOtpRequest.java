package com.example.tms.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyGoogleLinkOtpRequest(
        @NotBlank @Email String email,
        @NotBlank String idToken,
        @NotBlank @Pattern(regexp = "\\d{6}") String otp
) {
}
