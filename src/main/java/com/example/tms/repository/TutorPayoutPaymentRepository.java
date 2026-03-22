package com.example.tms.repository;

import com.example.tms.entity.TutorPayoutPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TutorPayoutPaymentRepository extends JpaRepository<TutorPayoutPayment, UUID> {
    Optional<TutorPayoutPayment> findByQrRef(String qrRef);
    Optional<TutorPayoutPayment> findTopByTutorPayoutIdOrderByCreatedAtDesc(UUID tutorPayoutId);
}
