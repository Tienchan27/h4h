package com.example.tms.api.dto.classes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublishClassStudentRequest(
        @NotBlank
        @Email(message = "Invalid student email format")
        @Size(max = 255)
        String email,

        @Size(max = 100)
        String name
) {
}
