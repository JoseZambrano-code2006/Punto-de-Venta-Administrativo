-- =========================
-- DATOS: account
-- =========================
INSERT INTO account (name, mail, password) 
VALUES ('Administrador', 'admin@gmail.com', '$2a$10$password_encriptado_demo');


-- =========================
-- DATOS: category
-- =========================
INSERT INTO category (name, image_url) 
VALUES ('Hamburguesas', 'category_burger.png');

INSERT INTO category (name, image_url) 
VALUES ('Bebidas', 'category_drink.png');

INSERT INTO category (name, image_url) 
VALUES ('Extras', 'category_fries.png');


-- =========================
-- DATOS: product
-- =========================

-- Hamburguesas
INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (1, 'Hamburguesa Clásica', 'Hamburguesa simple con carne, queso y vegetales', 15.00, 'clasica.png');

INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (1, 'Doble con Queso', 'Hamburguesa doble carne con queso', 22.50, 'doble_queso.png');

INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (1, 'Hamburguesa Veggie', 'Hamburguesa vegetariana con vegetales frescos', 18.00, 'veggie.png');


-- Bebidas
INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (2, 'Cerveza Artesanal IPA', 'Cerveza artesanal tipo IPA', 12.00, 'ipa.png');

INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (2, 'Limonada de Menta', 'Limonada natural con menta', 8.00, 'limonada.png');

INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (2, 'Gaseosa Cola', 'Gaseosa sabor cola', 5.00, 'cola.png');


-- Extras
INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (3, 'Papas Fritas Medianas', 'Porción mediana de papas fritas', 6.00, 'papas_m.png');

INSERT INTO product (id_category, name, description, price, image_url) 
VALUES (3, 'Aros de Cebolla', 'Aros de cebolla crocantes', 8.50, 'aros.png');


-- =========================
-- DATOS: general_cash_box
-- =========================
INSERT INTO general_cash_box (
    id_account, 
    saldo_inicial, 
    estado
)
VALUES (
    1,
    100.00,
    'OPEN'
);


-- =========================
-- DATOS: pos_order
-- =========================
INSERT INTO pos_order (
    id_caja_general,
    cliente_id,
    customer_name,
    customer_document,
    total_amount,
    order_number,
    order_type
)
VALUES (
    1,
    1,
    'Cliente Mostrador',
    '00000000',
    37.50,
    1,
    'SALE'
);


-- =========================
-- DATOS: pos_order_detail
-- =========================

-- Doble con Queso
INSERT INTO pos_order_detail (
    id_product,
    id_order,
    quantity,
    kitchen_notes,
    unit_price,
    subtotal
)
VALUES (2, 1, 1, 'Sin cebolla', 22.50, 22.50);

-- Papas Fritas
INSERT INTO pos_order_detail (
    id_product,
    id_order,
    quantity,
    kitchen_notes,
    unit_price,
    subtotal
)
VALUES (7, 1, 1, 'Con mayonesa', 6.00, 6.00);

-- Limonada
INSERT INTO pos_order_detail (
    id_product,
    id_order,
    quantity,
    unit_price,
    subtotal
)
VALUES (5, 1, 1, 8.00, 8.00);


-- =========================
-- DATOS: cash_movement
-- =========================

INSERT INTO cash_movement (id_cash_box, type, amount, description)
VALUES (1, 'INCOME', 50.00, 'Ingreso adicional para cambio');

INSERT INTO cash_movement (id_cash_box, type, amount, description)
VALUES (1, 'EXPENSE', 10.00, 'Compra de bolsas');

INSERT INTO user_info (username, password) 
VALUES ('admin', '$2b$10$iFyuKgtnbdtpq4zFW59cQeNRhF33J90qsgvIYCZRI2SP/QWridn1.');