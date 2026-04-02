package com.example.tms.messaging;

import com.example.tms.entity.NotificationEventConsumption;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.NotificationType;
import com.example.tms.repository.NotificationEventConsumptionRepository;
import com.example.tms.repository.UserRepository;
import com.example.tms.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NotificationEmailConsumer {
    private static final String CONSUMER_NAME = "EMAIL";
    private static final Set<NotificationType> EMAIL_TYPES = Set.of(
            NotificationType.PAYOUT_PAID,
            NotificationType.CLASS_APPLICATION_APPROVED,
            NotificationType.CLASS_APPLICATION_REJECTED,
            NotificationType.TUTOR_ROLE_REVOKED
    );

    private final ObjectMapper objectMapper;
    private final NotificationEventConsumptionRepository consumptionRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public NotificationEmailConsumer(
            ObjectMapper objectMapper,
            NotificationEventConsumptionRepository consumptionRepository,
            UserRepository userRepository,
            MailService mailService
    ) {
        this.objectMapper = objectMapper;
        this.consumptionRepository = consumptionRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.notifications}",
            groupId = "tms-notifications-email"
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

            NotificationType type = NotificationType.valueOf(payload.eventType());
            if (!EMAIL_TYPES.contains(type)) {
                markProcessed(payload.eventId());
                return;
            }

            User recipient = userRepository.findById(payload.recipientUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

            mailService.sendNotificationEmail(recipient.getEmail(), payload.title().trim(), payload.content().trim());
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

