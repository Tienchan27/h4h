package com.example.tms.service;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CorrelationIdAccessor {
    public static final String MDC_KEY = "correlationId";

    public String getOrCreateCorrelationId() {
        String existing = MDC.get(MDC_KEY);
        if (existing != null && !existing.isBlank()) {
            return existing;
        }
        String requestId = MDC.get("requestId");
        if (requestId != null && !requestId.isBlank()) {
            MDC.put(MDC_KEY, requestId);
            return requestId;
        }
        String created = UUID.randomUUID().toString();
        MDC.put(MDC_KEY, created);
        MDC.put("requestId", created);
        return created;
    }
}

