-- Migración: FK image_id en category (catálogo product_image)
ALTER TABLE category
    ADD COLUMN IF NOT EXISTS image_id INTEGER REFERENCES product_image(id);

CREATE INDEX IF NOT EXISTS idx_category_image_id ON category(image_id);
