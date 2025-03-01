CREATE TABLE notifications
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    order_id    BIGINT,
    title      TEXT   NOT NULL,
    message    TEXT   NOT NULL,
    is_read    BOOLEAN   DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
