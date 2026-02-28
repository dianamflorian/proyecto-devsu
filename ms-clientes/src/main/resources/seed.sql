CREATE TABLE personas (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          genero VARCHAR(10),
                          edad INT,
                          identificacion VARCHAR(20) NOT NULL UNIQUE,
                          direccion VARCHAR(200),
                          telefono VARCHAR(15)
);

CREATE TABLE clientes (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          genero VARCHAR(10),
                          edad INT,
                          identificacion VARCHAR(20) NOT NULL UNIQUE,
                          direccion VARCHAR(200),
                          telefono VARCHAR(15),
                          contrasena VARCHAR(20) NOT NULL,
                          estado BOOLEAN NOT NULL
);

CREATE TABLE cuentas (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
                         tipo VARCHAR(20),
                         saldo_inicial DECIMAL(10,2),
                         saldo DECIMAL(10,2),
                         estado BOOLEAN NOT NULL,
                         fecha_creacion DATE,
                         cliente_id BIGINT,
                         FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);                                                                                              ('496825', 'Ahorros', 540.00, 540.00, true, NOW(), 2);