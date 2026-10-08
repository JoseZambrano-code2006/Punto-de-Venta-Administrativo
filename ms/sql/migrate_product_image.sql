CREATE TABLE IF NOT EXISTS product_image (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    content_type VARCHAR(64) NOT NULL,
    data BYTEA NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE product
    ADD COLUMN IF NOT EXISTS image_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_image'
    ) THEN
        ALTER TABLE product
            ADD CONSTRAINT fk_product_image
            FOREIGN KEY (image_id) REFERENCES product_image(id);
    END IF;
END $$;
