package com.example.tms.api.dto.notification;

import com.example.tms.entity.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String content,
        boolean read,
        LocalDateTime createdAt
) {
}

