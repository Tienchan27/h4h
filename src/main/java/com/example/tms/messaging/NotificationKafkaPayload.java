package com.example.tms.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationKafkaPayload(
        UUID eventId,
        String eventType,
        UUID recipientUserId,
        String entityRef,
        String title,
        String content,
        String correlationId,
        LocalDateTime occurredAt
) {
}

