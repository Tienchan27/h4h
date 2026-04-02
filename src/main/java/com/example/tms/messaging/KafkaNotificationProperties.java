package com.example.tms.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaNotificationProperties(
        boolean enabled,
        Topic topic,
        Outbox outbox
) {
    public record Topic(
            String notifications,
            String notificationsDlq
    ) {
    }

    public record Outbox(
            int maxAttempts,
            Publisher publisher
    ) {
    }

    public record Publisher(
            long delayMs
    ) {
    }
}

