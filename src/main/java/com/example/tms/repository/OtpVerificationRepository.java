package com.example.tms.repository;

import com.example.tms.entity.OtpVerification;
import com.example.tms.entity.enums.OtpPurpose;
import com.example.tms.entity.enums.OtpStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OtpVerification> findTopByEmailAndPurposeAndStatusOrderByCreatedAtDesc(
            String email,
            OtpPurpose purpose,
            OtpStatus status
    );
}
