ALTER TABLE product_translations
    DROP CONSTRAINT fk_product;
ALTER TABLE product_translations
    ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE;