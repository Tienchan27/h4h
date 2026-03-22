package com.example.tms.api;

import com.example.tms.api.dto.auth.AuthResponse;
import com.example.tms.api.dto.auth.LoginRequest;
import com.example.tms.api.dto.auth.RegisterRequest;
import com.example.tms.api.dto.auth.VerifyOtpRequest;
import com.example.tms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Map.of("message", "Registered. Please verify OTP sent to email.");
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public Map<String, String> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return Map.of("message", "OTP resent");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/google")
    public Map<String, String> googlePlaceholder() {
        return Map.of("message", "Google OAuth login placeholder endpoint");
    }

    @PostMapping("/refresh")
    public Map<String, String> refreshPlaceholder() {
        return Map.of("message", "Refresh token endpoint placeholder");
    }
}
