package com.example.tms.service;

import com.example.tms.api.dto.auth.AuthResponse;
import com.example.tms.api.dto.auth.LoginRequest;
import com.example.tms.api.dto.auth.RegisterRequest;
import com.example.tms.api.dto.auth.VerifyOtpRequest;
import com.example.tms.entity.*;
import com.example.tms.entity.enums.*;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.*;
import com.example.tms.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            OtpVerificationRepository otpVerificationRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ApiException("Email already exists");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user = userRepository.save(user);
        assignRole(user, RoleName.STUDENT);
        issueOtp(request.email());
    }

    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ApiException("User not found"));
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new ApiException("User already verified");
        }
        issueOtp(email.toLowerCase());
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        OtpVerification otp = otpVerificationRepository
                .findTopByEmailAndPurposeAndStatusOrderByCreatedAtDesc(
                        request.email().toLowerCase(),
                        OtpPurpose.REGISTER,
                        OtpStatus.ACTIVE
                )
                .orElseThrow(() -> new ApiException("OTP not found"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setStatus(OtpStatus.EXPIRED);
            throw new ApiException("OTP expired");
        }
        otp.setAttemptCount(otp.getAttemptCount() + 1);
        if (otp.getAttemptCount() > 5) {
            otp.setStatus(OtpStatus.EXPIRED);
            throw new ApiException("Too many attempts");
        }

        String hash = hashOtp(request.otp());
        if (!hash.equals(otp.getOtpHash())) {
            throw new ApiException("Invalid OTP");
        }
        otp.setStatus(OtpStatus.VERIFIED);

        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ApiException("User not found"));
        user.setStatus(UserStatus.ACTIVE);

        String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        return new AuthResponse(user.getId(), user.getEmail(), access, refresh);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ApiException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException("Invalid credentials");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException("Account not active");
        }
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        return new AuthResponse(user.getId(), user.getEmail(), access, refresh);
    }

    private void issueOtp(String email) {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtpHash(hashOtp(otp));
        verification.setPurpose(OtpPurpose.REGISTER);
        verification.setStatus(OtpStatus.ACTIVE);
        verification.setAttemptCount(0);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(verification);
        mailService.sendOtpEmail(email, otp);
    }

    private void assignRole(User user, RoleName roleName) {
        Role role = roleRepository.findByName(roleName).orElseGet(() -> {
            Role created = new Role();
            created.setName(roleName);
            return roleRepository.save(created);
        });
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setStatus(UserRoleStatus.ACTIVE);
        userRole.setUpdatedBy(user);
        userRoleRepository.save(userRole);
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(otp.getBytes()));
        } catch (Exception ex) {
            throw new ApiException("Failed to hash OTP");
        }
    }
}
