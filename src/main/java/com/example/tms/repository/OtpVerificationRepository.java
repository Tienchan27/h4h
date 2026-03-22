package com.example.tms.repository;

import com.example.tms.entity.OtpVerification;
import com.example.tms.entity.enums.OtpPurpose;
import com.example.tms.entity.enums.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {
    Optional<OtpVerification> findTopByEmailAndPurposeAndStatusOrderByCreatedAtDesc(
            String email,
            OtpPurpose purpose,
            OtpStatus status
    );
}
