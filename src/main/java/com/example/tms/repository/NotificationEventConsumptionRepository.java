package com.example.tms.repository;

import com.example.tms.entity.NotificationEventConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationEventConsumptionRepository extends JpaRepository<NotificationEventConsumption, UUID> {
    boolean existsByEventIdAndConsumerName(UUID eventId, String consumerName);
}

