-- Migración: catálogo imagen_cat para categorías
CREATE TABLE IF NOT EXISTS imagen_cat (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    content_type VARCHAR(64) NOT NULL,
    data BYTEA NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE category ADD COLUMN IF NOT EXISTS imagen_cat_id INTEGER REFERENCES imagen_cat(id);

ALTER TABLE category DROP CONSTRAINT IF EXISTS category_image_id_fkey;
ALTER TABLE category DROP COLUMN IF EXISTS image_id;

CREATE INDEX IF NOT EXISTS idx_category_imagen_cat_id ON category(imagen_cat_id);
