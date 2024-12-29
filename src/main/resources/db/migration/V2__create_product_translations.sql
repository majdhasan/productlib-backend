CREATE TABLE product_translations
(
    id          SERIAL PRIMARY KEY,
    product_id  BIGINT       NOT NULL,
    language    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id)
);