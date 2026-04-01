package com.example.tms.service;

import com.example.tms.api.dto.notification.NotificationResponse;
import com.example.tms.entity.Notification;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.NotificationType;
import com.example.tms.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void notifyUser(User user, NotificationType type, String title, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public Notification markRead(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        n.setRead(true);
        return notificationRepository.save(n);
    }

    public Slice<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getContent(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
