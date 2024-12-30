CREATE TABLE verification_tokens
(
    id          SERIAL PRIMARY KEY,
    token       VARCHAR(255) NOT NULL,
    user_id     BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

ALTER Table users DROP COLUMN is_verified;
ALTER Table users
    add COLUMN email_verified BOOLEAN DEFAULT FALSE;
ALTER Table users
    add COLUMN phone_verified BOOLEAN DEFAULT FALSE;