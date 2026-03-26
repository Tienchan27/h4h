package com.example.tms.api.dto.classes;

public record StudentLookupResponse(
        boolean exists,
        String email,
        String name
) {
}
