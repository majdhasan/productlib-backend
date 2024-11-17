CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    email         VARCHAR(100) UNIQUE,
    password      VARCHAR(255),
    is_registered BOOLEAN   DEFAULT FALSE,
    is_verified   BOOLEAN   DEFAULT FALSE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE services
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    location    TEXT,
    category    TEXT           NOT NULL,
    cost        DECIMAL(10, 2) NOT NULL,
    duration    INT            NOT NULL,
    user_id     BIGINT         NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_services_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE bookings
(
    id          SERIAL PRIMARY KEY,
    notes       TEXT,
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    location    VARCHAR(255),
    is_paid     BOOLEAN   DEFAULT FALSE,
    customer_id BIGINT    NOT NULL,
    service_id  BIGINT    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_bookings_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);
