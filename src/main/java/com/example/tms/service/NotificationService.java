package com.example.tms.service;

import com.example.tms.api.dto.notification.NotificationResponse;
import com.example.tms.entity.Notification;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.NotificationType;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MailService mailService;
    private static final Duration DEDUPE_WINDOW = Duration.ofMinutes(5);
    private static final Set<NotificationType> EMAIL_TYPES = Set.of(
            NotificationType.PAYOUT_PAID,
            NotificationType.CLASS_APPLICATION_APPROVED,
            NotificationType.CLASS_APPLICATION_REJECTED,
            NotificationType.TUTOR_ROLE_REVOKED
    );

    public NotificationService(NotificationRepository notificationRepository, MailService mailService) {
        this.notificationRepository = notificationRepository;
        this.mailService = mailService;
    }

    public void notifyUser(User user, NotificationType type, String title, String content) {
        if (user == null || user.getId() == null) {
            throw new ApiException("Notification recipient is required");
        }
        if (type == null) {
            throw new ApiException("Notification type is required");
        }
        if (title == null || title.isBlank()) {
            throw new ApiException("Notification title is required");
        }
        if (content == null || content.isBlank()) {
            throw new ApiException("Notification content is required");
        }

        // Anti-noise: avoid duplicate notifications within a short window.
        LocalDateTime after = LocalDateTime.now().minus(DEDUPE_WINDOW);
        boolean recentlyExists = notificationRepository
                .findTopByUserIdAndTypeAndTitleAndContentAndCreatedAtAfterOrderByCreatedAtDesc(
                        user.getId(),
                        type,
                        title.trim(),
                        content.trim(),
                        after
                )
                .isPresent();
        if (recentlyExists) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title.trim());
        notification.setContent(content.trim());
        notification.setRead(false);
        notificationRepository.save(notification);

        if (EMAIL_TYPES.contains(type)) {
            mailService.sendNotificationEmail(user.getEmail(), title.trim(), content.trim());
        }
    }

    public Notification markRead(UUID userId, UUID notificationId) {
        if (userId == null) {
            throw new ApiException("User is required");
        }
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        if (n.getUser() == null || n.getUser().getId() == null || !n.getUser().getId().equals(userId)) {
            throw new ApiException("Not authorized to mark this notification");
        }
        n.setRead(true);
        return notificationRepository.save(n);
    }

    public NotificationResponse markReadResponse(UUID userId, UUID notificationId) {
        Notification saved = markRead(userId, notificationId);
        return toResponse(saved);
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
