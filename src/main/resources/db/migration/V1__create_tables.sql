-- Users Table
CREATE TABLE users
(
    id                 SERIAL PRIMARY KEY,
    first_name         VARCHAR(255)        NOT NULL,
    last_name          VARCHAR(255)        NOT NULL,
    phone_number       VARCHAR(255)        NOT NULL,
    date_of_birth      DATE,
    email              VARCHAR(100) UNIQUE NOT NULL,
    password           VARCHAR(255),
    is_registered      BOOLEAN     DEFAULT FALSE,
    is_verified        BOOLEAN     DEFAULT FALSE,
    preferred_language VARCHAR(10) DEFAULT 'en',
    created_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- Products Table
CREATE TABLE products
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    image       TEXT,
    description TEXT,
    category    TEXT           NOT NULL,
    cost        DECIMAL(10, 2) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cart Table
CREATE TABLE cart
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    status     TEXT      DEFAULT 'PENDING', -- Added status to differentiate cart states
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Cart Items Table
CREATE TABLE cart_items
(
    id            SERIAL PRIMARY KEY,
    cart_id       BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    product_price DECIMAL(10, 2), -- Nullable, filled when the order is created
    notes         TEXT,
    quantity      INT    NOT NULL DEFAULT 1,
    created_at    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE TABLE orders
(
    id                 SERIAL PRIMARY KEY,
    notes              TEXT,
    is_paid            BOOLEAN              DEFAULT FALSE,
    customer_id        BIGINT      NOT NULL,
    cart_id            BIGINT      NOT NULL,
    phone              VARCHAR(15) NOT NULL,
    first_name         VARCHAR(50) NOT NULL,
    last_name          VARCHAR(50) NOT NULL,
    address            TEXT        NOT NULL,
    wished_pickup_time TIMESTAMP NULL,
    status             VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    type               VARCHAR(20) NOT NULL DEFAULT 'PICKUP',
    created_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE
);
