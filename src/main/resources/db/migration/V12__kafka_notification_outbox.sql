-- Kafka notification outbox (durable events) + idempotency tracking.

create table if not exists notification_events_outbox (
    id uuid primary key,
    event_type varchar(80) not null,
    recipient_user_id uuid not null references users(id),
    entity_ref varchar(120),
    payload_json text not null,
    correlation_id varchar(80),
    status varchar(20) not null,
    attempts integer not null default 0,
    next_attempt_at timestamp,
    created_at timestamp not null,
    published_at timestamp,
    last_error varchar(500)
);

create index if not exists idx_notif_outbox_status_next_attempt on notification_events_outbox(status, next_attempt_at);
create index if not exists idx_notif_outbox_recipient_created on notification_events_outbox(recipient_user_id, created_at);

create table if not exists notification_event_consumptions (
    id uuid primary key,
    event_id uuid not null,
    consumer_name varchar(80) not null,
    processed_at timestamp not null,
    constraint uk_event_consumer unique (event_id, consumer_name)
);

