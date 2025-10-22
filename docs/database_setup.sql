-- =====================================================
-- SCRIPT DE INSTALACIÓN COMPLETA - APPLEGYM
-- Base de datos para sistema de gestión de gimnasio
-- Versión: 2.0
-- Fecha: Octubre 2025
-- =====================================================

-- Crear base de datos
DROP DATABASE IF EXISTS applegym;
CREATE DATABASE applegym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE applegym;

-- =====================================================
-- TABLA: cliente
-- =====================================================
CREATE TABLE cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_cliente VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    rol VARCHAR(20) DEFAULT 'CLIENTE',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_activo (activo),
    INDEX idx_rol (rol)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: categoria
-- =====================================================
CREATE TABLE categoria (
    id_categoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: producto
-- =====================================================
CREATE TABLE producto (
    id_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    id_categoria BIGINT,
    categoria VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE SET NULL,
    INDEX idx_categoria (id_categoria),
    INDEX idx_activo (activo),
    INDEX idx_precio (precio)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: servicio
-- =====================================================
CREATE TABLE servicio (
    id_servicio BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    duracion INT,
    id_categoria BIGINT,
    categoria VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE SET NULL,
    INDEX idx_categoria (id_categoria),
    INDEX idx_activo (activo),
    INDEX idx_precio (precio)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: carrito
-- =====================================================
CREATE TABLE carrito (
    id_carrito BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    total DECIMAL(10, 2) DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    INDEX idx_cliente (id_cliente),
    INDEX idx_estado (estado)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: detalle_carrito
-- =====================================================
CREATE TABLE detalle_carrito (
    id_detalle_carrito BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_carrito BIGINT NOT NULL,
    id_item BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nombre_item VARCHAR(100),
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito) ON DELETE CASCADE,
    INDEX idx_carrito (id_carrito),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: venta
-- =====================================================
CREATE TABLE venta (
    id_venta BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    numero_venta VARCHAR(50) UNIQUE,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'COMPLETADO',
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE RESTRICT,
    INDEX idx_cliente (id_cliente),
    INDEX idx_fecha (fecha_venta),
    INDEX idx_estado (estado)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: detalle_venta
-- =====================================================
CREATE TABLE detalle_venta (
    id_detalle_venta BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venta BIGINT NOT NULL,
    id_producto_servicio BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    INDEX idx_venta (id_venta),
    INDEX idx_tipo (tipo),
    INDEX idx_producto_servicio (id_producto_servicio)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: pago
-- =====================================================
CREATE TABLE pago (
    id_pago BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venta BIGINT NOT NULL UNIQUE,
    tipo_pago VARCHAR(50) NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_pago VARCHAR(20) DEFAULT 'COMPLETADO',
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    INDEX idx_venta (id_venta),
    INDEX idx_fecha (fecha_pago)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: comprobante
-- =====================================================
CREATE TABLE comprobante (
    id_comprobante BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venta BIGINT NOT NULL UNIQUE,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_comprobante VARCHAR(20) DEFAULT 'BOLETA',
    serie_comprobante VARCHAR(20),
    estado_comprobante VARCHAR(20) DEFAULT 'GENERADO',
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    INDEX idx_venta (id_venta),
    INDEX idx_fecha (fecha_emision)
) ENGINE=InnoDB;

-- =====================================================
-- INSERTAR CATEGORÍAS
-- =====================================================
INSERT INTO categoria (activo,descripcion,fecha_actualizacion,fecha_creacion,nombre_categoria) VALUES
(1, 'Suplementos', NOW(), NOW(), 'Suplementos'),
(1, 'Ropa', NOW(), NOW(), 'Ropa'),
(1, 'Accesorios', NOW(), NOW(), 'Accesorios'),
(1, 'Entrenamiento Personal', NOW(), NOW(), 'Entrenamiento Personal'),
(1, 'Clases Grupales', NOW(), NOW(), 'Clases Grupales'),
(1, 'Nutrición', NOW(), NOW(), 'Nutrición');

-- =====================================================
-- INSERTAR PRODUCTOS
-- =====================================================
INSERT INTO producto (activo, descripcion,fecha_actualizacion, fecha_creacion, imagen_url, nombre, precio, stock, id_categoria) VALUES
-- Suplementos
(1, 'Proteína Whey Pro', NOW(), NOW(), 'url_imagen_1', 'Proteína de suero de leche de alta calidad, 2kg', 89.99, 50, 1),
(1, 'Creatina Monohidrato', NOW(), NOW(), 'url_imagen_2', 'Creatina pura para aumentar fuerza y masa muscular, 500g', 45.00, 75, 1),
(1, 'BCAA Complex', NOW(), NOW(), 'url_imagen_3', 'Aminoácidos ramificados para recuperación muscular', 55.00, 60, 1),
(1, 'Pre-Workout Extreme', NOW(), NOW(), 'url_imagen_4', 'Fórmula pre-entreno con cafeína y beta-alanina', 65.00, 40, 1),
(1, 'Omega 3 Fish Oil', NOW(), NOW(), 'url_imagen_5', 'Aceite de pescado con EPA y DHA, 90 cápsulas', 35.00, 100, 1),
(1, 'Multivitamínico Sport', NOW(), NOW(), 'url_imagen_6', 'Complejo vitamínico para deportistas, 60 tabletas', 28.00, 80, 1),

-- Ropa
(1, 'Camiseta Dry-Fit', NOW(), NOW(), 'url_imagen_7', 'Camiseta deportiva con tecnología de secado rápido', 35.00, 100, 2),
(1, 'Short de Entrenamiento', NOW(), NOW(), 'url_imagen_8', 'Short cómodo para todo tipo de ejercicio', 42.00, 80, 2),
(1, 'Leggins Deportivos', NOW(), NOW(), 'url_imagen_9', 'Leggins de compresión para mujer', 55.00, 70, 2),
(1, 'Sudadera con Capucha', NOW(), NOW(), 'url_imagen_10', 'Sudadera cómoda para calentamiento', 75.00, 50, 2),
(1, 'Top Deportivo Mujer', NOW(), NOW(), 'url_imagen_11', 'Top con soporte para entrenamiento intenso', 38.00, 60, 2),

-- Accesorios
(1, 'Guantes de Gimnasio', NOW(), NOW(), 'url_imagen_12', 'Guantes con soporte de muñeca para levantamiento', 25.00, 90, 3),
(1, 'Shaker con Compartimentos', NOW(), NOW(), 'url_imagen_13', 'Shaker de 700ml con compartimentos para suplementos', 18.00, 120, 3),
(1, 'Banda de Resistencia Set', NOW(), NOW(), 'url_imagen_14', 'Set de 5 bandas con diferentes resistencias', 32.00, 85, 3),
(1, 'Colchoneta Yoga Pro', NOW(), NOW(), 'url_imagen_15', 'Colchoneta antideslizante de 6mm de grosor', 48.00, 40, 3),
(1, 'Botella Térmica 1L', NOW(), NOW(), 'url_imagen_16', 'Botella de acero inoxidable, mantiene temperatura 24hrs', 45.00, 65, 3);

-- =====================================================
-- INSERTAR SERVICIOS
-- =====================================================
INSERT INTO servicio (activo,capacidad_maxima, descripcion, duracion, fecha_actualizacion, fecha_creacion, imagen_url, precio,id_categoria) VALUES
-- Entrenamiento Personal
(1, 1, 'Entrenamiento Personal 1 Sesión', 60, NOW(), NOW(), 'url_imagen_1', 45.00, 4),
(1, 12, 'Plan Mensual Personal', 60, NOW(), NOW(), 'url_imagen_2', 480.00, 4),
(1, 1, 'Evaluación Física Completa', 45, NOW(), NOW(), 'url_imagen_3', 35.00, 4),

-- Clases Grupales
(1, 1, 'Clase de Spinning', 45, NOW(), NOW(), 'url_imagen_4', 15.00, 5),
(1, 1, 'Clase de Yoga', 60, NOW(), NOW(), 'url_imagen_5', 18.00, 5),
(1, 1, 'CrossFit WOD', 60, NOW(), NOW(), 'url_imagen_6', 20.00, 5),
(1, 1, 'Zumba Fitness', 50, NOW(), NOW(), 'url_imagen_7', 15.00, 5),
(1, 1, 'Body Combat', 55, NOW(), NOW(), 'url_imagen_8', 18.00, 5),

-- Nutrición
(1, 1, 'Consulta Nutricional', 60, NOW(), NOW(), 'url_imagen_9', 60.00, 6),
(1, 1, 'Plan Nutricional Mensual', 60, NOW(), NOW(), 'url_imagen_10', 250.00, 6);

-- =====================================================
-- CREAR USUARIO ADMINISTRADOR
-- =====================================================
-- Password: applegymadmin
-- El hash BCrypt se genera automáticamente en la aplicación
-- Por seguridad, el admin debe registrarse primero en la app
-- y luego ejecutar este UPDATE para cambiar el rol

-- IMPORTANTE: Después de registrarse con email applegym@admin.com
-- Ejecutar este comando:
-- UPDATE cliente SET rol = 'ADMIN' WHERE email = 'applegym@admin.com';

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

-- Verificar instalación
SELECT 'Base de datos creada exitosamente!' AS Mensaje;
SELECT COUNT(*) AS Total_Categorias FROM categoria;
SELECT COUNT(*) AS Total_Productos FROM producto;
SELECT COUNT(*) AS Total_Servicios FROM servicio;


