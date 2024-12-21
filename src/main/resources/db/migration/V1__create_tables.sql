CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    email         VARCHAR(100) UNIQUE,
    password      VARCHAR(255),
    is_registered BOOLEAN   DEFAULT FALSE,
    is_verified   BOOLEAN   DEFAULT FALSE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products Table (Updated: Removed undefined 'user_id')
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

-- Cart Table (New)
CREATE TABLE cart
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Cart_Items Table (New: Many-to-Many Relationship between Cart and Products)
CREATE TABLE cart_items
(
    id         SERIAL PRIMARY KEY,
    cart_id    BIGINT    NOT NULL,
    product_id BIGINT    NOT NULL,
    quantity   INT       NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- Orders Table (Updated: Linked to Cart)
CREATE TABLE orders
(
    id          SERIAL PRIMARY KEY,
    notes       TEXT,
    is_paid     BOOLEAN   DEFAULT FALSE,
    customer_id BIGINT    NOT NULL,
    cart_id     BIGINT    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE
);
