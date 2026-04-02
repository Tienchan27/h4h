package com.example.tms.messaging;

import com.example.tms.entity.Notification;
import com.example.tms.entity.NotificationEventConsumption;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.NotificationType;
import com.example.tms.repository.NotificationEventConsumptionRepository;
import com.example.tms.repository.NotificationRepository;
import com.example.tms.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NotificationInAppConsumer {
    private static final String CONSUMER_NAME = "IN_APP";
    private static final Duration DEDUPE_WINDOW = Duration.ofMinutes(5);

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationEventConsumptionRepository consumptionRepository;
    private final UserRepository userRepository;

    public NotificationInAppConsumer(
            ObjectMapper objectMapper,
            NotificationRepository notificationRepository,
            NotificationEventConsumptionRepository consumptionRepository,
            UserRepository userRepository
    ) {
        this.objectMapper = objectMapper;
        this.notificationRepository = notificationRepository;
        this.consumptionRepository = consumptionRepository;
        this.userRepository = userRepository;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.notifications}",
            groupId = "tms-notifications-inapp"
    )
    public void consume(String messageJson) throws Exception {
        NotificationKafkaPayload payload = objectMapper.readValue(messageJson, NotificationKafkaPayload.class);
        withCorrelation(payload.correlationId());
        try {
            if (payload.eventId() == null) {
                throw new IllegalArgumentException("eventId is required");
            }
            if (alreadyProcessed(payload.eventId())) {
                return;
            }

            User recipient = userRepository.findById(payload.recipientUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));
            NotificationType type = NotificationType.valueOf(payload.eventType());

            // Anti-noise dedupe (best-effort) in case of replaying old messages.
            LocalDateTime after = LocalDateTime.now().minus(DEDUPE_WINDOW);
            boolean recentlyExists = notificationRepository
                    .findTopByUserIdAndTypeAndTitleAndContentAndCreatedAtAfterOrderByCreatedAtDesc(
                            recipient.getId(),
                            type,
                            payload.title().trim(),
                            payload.content().trim(),
                            after
                    )
                    .isPresent();
            if (!recentlyExists) {
                Notification n = new Notification();
                n.setUser(recipient);
                n.setType(type);
                n.setTitle(payload.title().trim());
                n.setContent(payload.content().trim());
                n.setRead(false);
                notificationRepository.save(n);
            }

            markProcessed(payload.eventId());
        } finally {
            MDC.remove("correlationId");
        }
    }

    private boolean alreadyProcessed(UUID eventId) {
        return consumptionRepository.existsByEventIdAndConsumerName(eventId, CONSUMER_NAME);
    }

    private void markProcessed(UUID eventId) {
        NotificationEventConsumption c = new NotificationEventConsumption();
        c.setId(UUID.randomUUID());
        c.setEventId(eventId);
        c.setConsumerName(CONSUMER_NAME);
        c.setProcessedAt(LocalDateTime.now());
        try {
            consumptionRepository.save(c);
        } catch (DataIntegrityViolationException ignored) {
            // Duplicate delivery: unique constraint (event_id, consumer_name) makes it idempotent.
        }
    }

    private static void withCorrelation(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            MDC.remove("correlationId");
            return;
        }
        MDC.put("correlationId", correlationId);
    }
}

