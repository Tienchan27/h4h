package com.example.tms.messaging;

import com.example.tms.entity.NotificationOutboxEvent;
import com.example.tms.repository.NotificationOutboxRepository;
import com.example.tms.service.NotificationOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final NotificationOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaNotificationProperties props;

    public OutboxPublisher(
            NotificationOutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            KafkaNotificationProperties props
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.props = props;
    }

    @Scheduled(fixedDelayString = "${app.kafka.outbox.publisher.delay-ms:2000}")
    @Transactional
    public void publishPending() {
        List<NotificationOutboxEvent> batch = outboxRepository.findNextBatchForPublishing(
                NotificationOutboxService.STATUS_PENDING,
                LocalDateTime.now()
        );
        if (batch.isEmpty()) {
            return;
        }

        int limit = Math.min(batch.size(), 50);
        for (int i = 0; i < limit; i++) {
            NotificationOutboxEvent event = batch.get(i);
            publishOne(event);
        }
    }

    private void publishOne(NotificationOutboxEvent event) {
        String correlationId = event.getCorrelationId();
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put("correlationId", correlationId);
        }
        try {
            String topic = props.topic().notifications();
            String key = event.getRecipient() == null ? null : String.valueOf(event.getRecipient().getId());
            kafkaTemplate.send(topic, key, event.getPayloadJson()).get();

            event.setStatus(NotificationOutboxService.STATUS_PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
            event.setNextAttemptAt(null);
        } catch (Exception ex) {
            int attempts = event.getAttempts() + 1;
            event.setAttempts(attempts);
            event.setLastError(truncate(ex.getMessage(), 500));

            if (attempts >= props.outbox().maxAttempts()) {
                event.setStatus(NotificationOutboxService.STATUS_FAILED);
                event.setNextAttemptAt(null);
                log.warn("Outbox event {} failed after {} attempts", event.getId(), attempts);
                return;
            }

            Duration delay = Duration.ofSeconds(Math.min(60, attempts * 5L));
            event.setNextAttemptAt(LocalDateTime.now().plus(delay));
            log.debug("Outbox event {} publish failed attempt {}", event.getId(), attempts);
        } finally {
            MDC.remove("correlationId");
            outboxRepository.save(event);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }
}

