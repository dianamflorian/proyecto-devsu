-- BaseDatos.sql
-- Script para PostgreSQL ejecutable con psql.
-- Crea las bases de datos y las tablas principales para:
-- - devsu_clientes (clientes, cuentas)
-- - devsu_movimientos (movimientos)

-- =========================
-- 1) Crear bases de datos
-- =========================
CREATE DATABASE devsu_clientes;
CREATE DATABASE devsu_movimientos;

-- =========================
-- 2) devsu_clientes
-- =========================
\c devsu_clientes;

CREATE TABLE IF NOT EXISTS clientes (
                                        id BIGSERIAL PRIMARY KEY,
                                        nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(20),
    edad INTEGER,
    identificacion VARCHAR(50),
    direccion VARCHAR(255),
    telefono VARCHAR(50),
    contrasena VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS cuentas (
                                       id BIGSERIAL PRIMARY KEY,
                                       numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    saldo_inicial NUMERIC(19,2) NOT NULL DEFAULT 0,
    saldo NUMERIC(19,2) NOT NULL DEFAULT 0,
    estado BOOLEAN DEFAULT TRUE,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_cuentas_clientes
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
    );

CREATE INDEX IF NOT EXISTS idx_cuentas_cliente_id ON cuentas(cliente_id);

-- =========================
-- 3) devsu_movimientos
-- =========================
\c devsu_movimientos;

CREATE TABLE IF NOT EXISTS movimientos (
                                           id BIGSERIAL PRIMARY KEY,
                                           numero_cuenta VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    valor NUMERIC(19,2) NOT NULL,
    saldo NUMERIC(19,2) NOT NULL,
    descripcion VARCHAR(255)
    );

CREATE INDEX IF NOT EXISTS idx_movimientos_numero_cuenta ON movimientos(numero_cuenta);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos(fecha);                                                                                     ('496825', 'Ahorros', 540, 0, true, '2022-02-08', 2);                                                                                                   ('496825', 'Ahorros', 540, 0, true, '2022-02-08', 2);