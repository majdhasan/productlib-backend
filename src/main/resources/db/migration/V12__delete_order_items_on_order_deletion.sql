ALTER TABLE order_items
    DROP CONSTRAINT fk_order;
ALTER TABLE order_items
    ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE;