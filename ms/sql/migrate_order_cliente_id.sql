ALTER TABLE pos_order
    ADD COLUMN IF NOT EXISTS cliente_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE pos_order
    ADD COLUMN IF NOT EXISTS customer_document VARCHAR(50);

UPDATE pos_order SET cliente_id = 1 WHERE cliente_id IS NULL;
