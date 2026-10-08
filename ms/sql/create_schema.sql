-- =========================
-- TABLA: account
-- =========================
CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    mail VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_account_mail ON account(mail);


-- =========================
-- TABLA: product_image
-- =========================
CREATE TABLE IF NOT EXISTS product_image (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    content_type VARCHAR(64) NOT NULL,
    data BYTEA NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =========================
-- TABLA: imagen_cat
-- =========================
CREATE TABLE IF NOT EXISTS imagen_cat (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    content_type VARCHAR(64) NOT NULL,
    data BYTEA NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =========================
-- TABLA: category
-- =========================
CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(128),
    imagen_cat_id INTEGER REFERENCES imagen_cat(id),
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_imagen_cat_id ON category(imagen_cat_id);


-- =========================
-- TABLA: product
-- =========================
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    id_category BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    image_url VARCHAR(128),
    image_id BIGINT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_category 
        FOREIGN KEY (id_category) 
        REFERENCES category(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_product_image
        FOREIGN KEY (image_id)
        REFERENCES product_image(id)
);

CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_product_category ON product(id_category);


-- =========================
-- TABLA: general_cash_box
-- =========================
CREATE TABLE IF NOT EXISTS general_cash_box (
    id SERIAL PRIMARY KEY,
    id_account BIGINT NOT NULL,
    opening TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closing TIMESTAMP,
    saldo_inicial NUMERIC(10,2) NOT NULL DEFAULT 0,
    saldo_final NUMERIC(10,2),
    estado VARCHAR(20) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cash_box_account
        FOREIGN KEY (id_account)
        REFERENCES account(id)
);

CREATE INDEX idx_cash_box_account ON general_cash_box(id_account);
CREATE INDEX idx_cash_box_estado ON general_cash_box(estado);


-- =========================
-- TABLA: pos_order
-- =========================
CREATE TABLE IF NOT EXISTS pos_order (
    id SERIAL PRIMARY KEY,
    id_caja_general BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL DEFAULT 1,
    customer_name VARCHAR(128) NOT NULL,
    customer_document VARCHAR(50),
    total_amount NUMERIC(10,2) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_number INT,
    order_type VARCHAR(128) NOT NULL,

    CONSTRAINT fk_order_cash_box
        FOREIGN KEY (id_caja_general)
        REFERENCES general_cash_box(id)
);

CREATE INDEX idx_order_customer ON pos_order(customer_name);
CREATE INDEX idx_order_cash_box ON pos_order(id_caja_general);
CREATE INDEX idx_order_type ON pos_order(order_type);


-- =========================
-- TABLA: pos_order_detail
-- =========================
CREATE TABLE IF NOT EXISTS pos_order_detail (
    id SERIAL PRIMARY KEY,
    id_product BIGINT NOT NULL,
    id_order BIGINT NOT NULL,
    quantity INT NOT NULL,
    kitchen_notes TEXT,
    unit_price NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_detail_product
        FOREIGN KEY (id_product)
        REFERENCES product(id),

    CONSTRAINT fk_detail_order
        FOREIGN KEY (id_order)
        REFERENCES pos_order(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_detail_product ON pos_order_detail(id_product);
CREATE INDEX idx_detail_order ON pos_order_detail(id_order);


-- =========================
-- TABLA: cash_movement
-- =========================
CREATE TABLE IF NOT EXISTS cash_movement (
    id SERIAL PRIMARY KEY,
    id_cash_box BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    description VARCHAR(255),
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_movement_cash_box
        FOREIGN KEY (id_cash_box)
        REFERENCES general_cash_box(id)
        ON DELETE CASCADE
);

-- =========================
-- TABLA: user_info (Mapeada por UserEntity)
-- =========================
CREATE TABLE IF NOT EXISTS user_info (
    id SERIAL PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL -- Se recomienda 255 por si usas BCrypt para encriptar
);

CREATE INDEX idx_user_info_username ON user_info(username);

CREATE INDEX idx_movement_cash_box ON cash_movement(id_cash_box);
CREATE INDEX idx_movement_type ON cash_movement(type);