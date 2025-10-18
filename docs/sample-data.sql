-- AppleGym Sample Data
-- Script de datos de prueba para el sistema AppleGym
-- Versión: 1.0.0

USE applegym;

-- Insertar categorías
INSERT INTO categoria (nombre_categoria, descripcion, activo) VALUES
('Suplementos', 'Suplementos nutricionales y proteínas', TRUE),
('Ropa Deportiva', 'Ropa y accesorios para ejercicio', TRUE),
('Equipos', 'Equipos de entrenamiento y accesorios', TRUE),
('Membresías', 'Planes de membresía del gimnasio', TRUE),
('Entrenamientos', 'Servicios de entrenamiento personalizado', TRUE),
('Clases Grupales', 'Clases dirigidas y actividades grupales', TRUE),
('Wellness', 'Servicios de bienestar y relajación', TRUE);

-- Insertar productos
INSERT INTO producto (nombre, descripcion, precio, stock, activo, id_categoria) VALUES
-- Suplementos (id_categoria = 1)
('Proteína Whey Gold Standard', 'Proteína de suero de alta calidad, 5lb', 89.99, 25, TRUE, 1),
('Creatina Monohidrato', 'Creatina pura para aumento de fuerza, 300g', 45.50, 40, TRUE, 1),
('Pre-Entreno Explosive', 'Pre-entreno con cafeína y beta-alanina', 65.00, 15, TRUE, 1),
('BCAA 2:1:1', 'Aminoácidos esenciales para recuperación', 55.75, 30, TRUE, 1),
('Multivitamínico Premium', 'Complejo vitamínico completo, 60 cápsulas', 35.20, 50, TRUE, 1),

-- Ropa Deportiva (id_categoria = 2)
('Camiseta Deportiva Hombre', 'Camiseta de entrenamiento con tecnología Dri-FIT', 29.99, 100, TRUE, 2),
('Leggings Mujer', 'Leggings de alta compresión para yoga y fitness', 45.00, 75, TRUE, 2),
('Short de Entrenamiento', 'Short transpirable para hombre', 25.50, 60, TRUE, 2),
('Top Deportivo Mujer', 'Top de soporte medio para ejercicio', 35.75, 80, TRUE, 2),
('Sudadera con Capucha', 'Sudadera unisex para calentamiento', 55.00, 40, TRUE, 2),

-- Equipos (id_categoria = 3)
('Mancuernas Ajustables', 'Set de mancuernas de 10-50lb por mancuerna', 299.99, 10, TRUE, 3),
('Banda Elástica Set', 'Set de 5 bandas de resistencia diferentes', 25.99, 50, TRUE, 3),
('Esterilla de Yoga', 'Esterilla antideslizante premium', 45.00, 35, TRUE, 3),
('Guantes de Entrenamiento', 'Guantes acolchados para levantamiento', 18.75, 70, TRUE, 3),
('Botella de Agua 750ml', 'Botella deportiva con medidor', 15.50, 120, TRUE, 3);

-- Insertar servicios
INSERT INTO servicio (nombre, descripcion, precio, duracion, activo, capacidad_maxima, id_categoria) VALUES
-- Membresías (id_categoria = 4)
('Membresía Básica Mensual', 'Acceso completo al gimnasio por 1 mes', 49.99, 30, TRUE, NULL, 4),
('Membresía Premium Trimestral', 'Acceso + clases grupales por 3 meses', 129.99, 90, TRUE, NULL, 4),
('Membresía VIP Anual', 'Acceso completo + entrenador personal', 499.99, 365, TRUE, NULL, 4),

-- Entrenamientos (id_categoria = 5)
('Entrenamiento Personal 1 Sesión', 'Sesión individual con entrenador certificado', 75.00, 1, TRUE, 1, 5),
('Pack 4 Entrenamientos Personales', 'Paquete de 4 sesiones individuales', 270.00, 30, TRUE, 1, 5),
('Evaluación Física Completa', 'Análisis corporal y plan de entrenamiento', 85.00, 1, TRUE, 1, 5),

-- Clases Grupales (id_categoria = 6)
('Clase de Yoga Mensual', 'Acceso ilimitado a clases de yoga por 1 mes', 65.00, 30, TRUE, 20, 6),
('CrossFit Mensual', 'Clases de CrossFit ilimitadas por 1 mes', 89.00, 30, TRUE, 15, 6),
('Zumba Pack 8 Clases', 'Paquete de 8 clases de Zumba', 55.00, 60, TRUE, 25, 6),
('Spinning Mensual', 'Clases de spinning ilimitadas por 1 mes', 75.00, 30, TRUE, 20, 6),

-- Wellness (id_categoria = 7)
('Masaje Relajante', 'Masaje terapéutico de 60 minutos', 95.00, 1, TRUE, 1, 7),
('Sauna y Spa Mensual', 'Acceso ilimitado a sauna y spa por 1 mes', 125.00, 30, TRUE, 10, 7);

-- Insertar promociones
INSERT INTO promocion (nombre, descripcion, descuento, fecha_inicio, fecha_fin, tipo, activo, monto_minimo, cantidad_maxima_uso, codigo_promocional) VALUES
('Descuento Nuevos Miembros', '20% de descuento para nuevos clientes', 20.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'PORCENTAJE', TRUE, 50.00, NULL, 'NUEVO20'),
('Black Friday 2024', '30% de descuento en productos seleccionados', 30.00, '2024-11-24 00:00:00', '2024-11-30 23:59:59', 'PORCENTAJE', TRUE, 100.00, 1000, 'BLACKFRIDAY30'),
('Descuento por Volumen', '$25 de descuento en compras mayores a $200', 25.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'MONTO_FIJO', TRUE, 200.00, NULL, 'VOLUMEN25'),
('Primera Compra', '15% de descuento en tu primera compra', 15.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'PRIMERA_COMPRA', TRUE, 30.00, NULL, 'PRIMERA15'),
('Promoción de Verano', '2x1 en servicios de wellness', 50.00, '2024-12-01 00:00:00', '2025-03-31 23:59:59', 'DOS_POR_UNO', TRUE, NULL, 500, 'VERANO2X1');

-- Insertar cliente de prueba (password: "password123" - BCrypt encoded)
INSERT INTO cliente (nombre_cliente, email, password, telefono, direccion, activo) VALUES
('María García López', 'maria.garcia@email.com', '$2a$12$LQbouGJwm7yGK8K8VWQo.OH1X8.RhZQl8rjYSGP4nj.3RzwQ5rH8G', '987654321', 'Av. Principal 123, Lima', TRUE),
('Carlos Rodríguez Pérez', 'carlos.rodriguez@email.com', '$2a$12$LQbouGJwm7yGK8K8VWQo.OH1X8.RhZQl8rjYSGP4nj.3RzwQ5rH8G', '912345678', 'Jr. Los Olivos 456, Lima', TRUE),
('Ana Martínez Silva', 'ana.martinez@email.com', '$2a$12$LQbouGJwm7yGK8K8VWQo.OH1X8.RhZQl8rjYSGP4nj.3RzwQ5rH8G', '998877665', 'Calle Las Flores 789, Lima', TRUE);

-- Insertar algunos carritos de ejemplo
INSERT INTO carrito (id_cliente, fecha, estado, total) VALUES
(1, '2024-01-15 10:30:00', 'ACTIVO', 0.00),
(2, '2024-01-15 14:20:00', 'CONFIRMADO', 145.49),
(3, '2024-01-16 09:15:00', 'ACTIVO', 0.00);

-- Insertar detalles de carrito
INSERT INTO detalle_carrito (id_carrito, tipo, id_producto, id_servicio, cantidad, precio_unitario, subtotal) VALUES
-- Carrito 1 (María García)
(1, 'PRODUCTO', 1, NULL, 2, 89.99, 179.98),
(1, 'SERVICIO', NULL, 1, 1, 49.99, 49.99),

-- Carrito 2 (Carlos Rodríguez) 
(2, 'PRODUCTO', 3, NULL, 1, 65.00, 65.00),
(2, 'PRODUCTO', 6, NULL, 2, 29.99, 59.98),
(2, 'SERVICIO', NULL, 4, 1, 75.00, 75.00),

-- Carrito 3 (Ana Martínez)
(3, 'PRODUCTO', 11, NULL, 1, 25.99, 25.99),
(3, 'SERVICIO', NULL, 7, 1, 65.00, 65.00);

-- Actualizar totales de carritos
UPDATE carrito SET total = (SELECT SUM(subtotal) FROM detalle_carrito WHERE id_carrito = 1) WHERE id_carrito = 1;
UPDATE carrito SET total = (SELECT SUM(subtotal) FROM detalle_carrito WHERE id_carrito = 2) WHERE id_carrito = 2;
UPDATE carrito SET total = (SELECT SUM(subtotal) FROM detalle_carrito WHERE id_carrito = 3) WHERE id_carrito = 3;

-- Insertar una venta de ejemplo
INSERT INTO venta (id_cliente, fecha_venta, total, estado, numero_venta, observaciones) VALUES
(2, '2024-01-15 15:30:00', 199.98, 'PAGADO', 'VT1705339800001', 'Venta completada exitosamente');

-- Insertar detalles de venta
INSERT INTO detalle_venta (id_venta, id_producto, id_servicio, cantidad, precio_unitario, subtotal) VALUES
(1, 3, NULL, 1, 65.00, 65.00),
(1, 6, NULL, 2, 29.99, 59.98),
(1, NULL, 4, 1, 75.00, 75.00);

-- Insertar pago de ejemplo
INSERT INTO pago (id_venta, tipo_pago, monto, fecha_pago, estado_pago, numero_transaccion, detalles_pago) VALUES
(1, 'TARJETA_CREDITO', 199.98, '2024-01-15 15:32:00', 'APROBADO', 'TXN1705339920001', 'Pago procesado con Visa terminada en 4532');

-- Insertar comprobante de ejemplo
INSERT INTO comprobante (id_venta, fecha_emision, numero_comprobante, serie_comprobante, tipo_comprobante, estado_comprobante) VALUES
(1, '2024-01-15 15:33:00', 'B-001-00000001', '001', 'BOLETA', 'GENERADO');

-- Verificar datos insertados
SELECT 'Categorías insertadas' as Tabla, COUNT(*) as Total FROM categoria
UNION ALL
SELECT 'Productos insertados', COUNT(*) FROM producto
UNION ALL
SELECT 'Servicios insertados', COUNT(*) FROM servicio
UNION ALL
SELECT 'Promociones insertadas', COUNT(*) FROM promocion
UNION ALL
SELECT 'Clientes insertados', COUNT(*) FROM cliente
UNION ALL
SELECT 'Carritos insertados', COUNT(*) FROM carrito
UNION ALL
SELECT 'Detalles carrito insertados', COUNT(*) FROM detalle_carrito
UNION ALL
SELECT 'Ventas insertadas', COUNT(*) FROM venta
UNION ALL
SELECT 'Pagos insertados', COUNT(*) FROM pago
UNION ALL
SELECT 'Comprobantes insertados', COUNT(*) FROM comprobante;