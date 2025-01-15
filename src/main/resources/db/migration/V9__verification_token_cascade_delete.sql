ALTER TABLE verification_tokens
    DROP CONSTRAINT fk_user;

ALTER TABLE verification_tokens
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;