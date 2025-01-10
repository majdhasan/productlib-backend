ALTER TABLE cart_items
    DROP COLUMN product_price;
ALTER TABLE products
    DROP COLUMN category;
ALTER TABLE products
    RENAME COLUMN cost TO price;
ALTER TABLE orders
    DROP COLUMN cart_id;
ALTER TABLE cart
    drop column status;

CREATE TABLE order_items
(
    id                  SERIAL PRIMARY KEY,
    order_id            BIGINT         NOT NULL,
    product_id          BIGINT         NOT NULL,
    product_name        VARCHAR(255)   NOT NULL,
    product_description TEXT,
    product_price       DECIMAL(10, 2) NOT NULL,
    quantity            INT            NOT NULL,
    notes               TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders (id)
);