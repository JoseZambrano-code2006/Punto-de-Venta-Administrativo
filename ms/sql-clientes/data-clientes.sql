-- 1. Creamos el Schema primero
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(255),
    tipo_documento VARCHAR(50),
    numero_documento VARCHAR(50),
    direccion VARCHAR(255),
    correo VARCHAR(255)
);

-- 2. Insertamos el cliente por defecto
INSERT INTO clientes (id, nombre_completo, tipo_documento, numero_documento, direccion, correo) 
VALUES (1, 'Clientes Varios', 'S/D', '00000000', 'Sin Dirección', NULL) 
ON CONFLICT (id) DO NOTHING;

-- 3. (Opcional pero recomendado) Ajustar la secuencia del ID para que Spring Boot no intente usar el ID 1 en el siguiente registro
SELECT setval(pg_get_serial_sequence('clientes', 'id'), (SELECT MAX(id) FROM clientes));