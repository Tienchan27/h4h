package com.example.tms.repository;

import com.example.tms.entity.TutorInvitation;
import com.example.tms.entity.enums.TutorInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TutorInvitationRepository extends JpaRepository<TutorInvitation, UUID> {
    Optional<TutorInvitation> findByEmail(String email);

    Optional<TutorInvitation> findByEmailAndStatus(String email, TutorInvitationStatus status);
}
