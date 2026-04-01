package com.example.tms.repository;

import com.example.tms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Slice<Notification> findByUserId(UUID userId, Pageable pageable);
}
