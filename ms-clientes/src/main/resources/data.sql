-- Insertar clientes
INSERT INTO clientes (nombre, genero, edad, identificacion, direccion, telefono, estado) VALUES
                                                                                             ('José Lema', 'M', 30, '123456789', 'Calle Principal 123', '0962548793', true),
                                                                                             ('Marianela Montaño', 'F', 28, '987654321', 'Avenida Secundaria 456', '0975748953', true),
                                                                                             ('Juan Osorio', 'M', 35, '456789123', 'Calle Tercera 789', '0987654321', true);

-- Insertar cuentas
INSERT INTO cuentas (numero_cuenta, tipo, saldo_inicial, saldo, estado, fecha_creacion, cliente_id) VALUES
                                                                                                        ('478758', 'Ahorros', 2000.00, 2000.00, true, NOW(), 1),
                                                                                                        ('225487', 'Corriente', 100.00, 100.00, true, NOW(), 2),
                                                                                                        ('495878', 'Ahorros', 0.00, 0.00, true, NOW(), 3),
                                                                                                        ('496825', 'Ahorros', 540.00, 540.00, true, NOW(), 2);