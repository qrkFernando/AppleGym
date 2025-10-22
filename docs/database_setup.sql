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
INSERT INTO categoria (nombre_categoria, descripcion) VALUES
('Suplementos', 'Suplementos nutricionales y proteínas'),
('Ropa', 'Ropa deportiva y accesorios de vestir'),
('Accesorios', 'Accesorios para entrenamiento'),
('Entrenamiento Personal', 'Servicios de entrenamiento personalizado'),
('Clases Grupales', 'Clases grupales de diferentes disciplinas'),
('Nutrición', 'Servicios de asesoría nutricional');

-- =====================================================
-- INSERTAR PRODUCTOS
-- =====================================================
INSERT INTO producto (nombre, descripcion, precio, stock, id_categoria, categoria) VALUES
-- Suplementos
('Proteína Whey Pro', 'Proteína de suero de leche de alta calidad, 2kg', 89.99, 50, 1, 'Suplementos'),
('Creatina Monohidrato', 'Creatina pura para aumentar fuerza y masa muscular, 500g', 45.00, 75, 1, 'Suplementos'),
('BCAA Complex', 'Aminoácidos ramificados para recuperación muscular', 55.00, 60, 1, 'Suplementos'),
('Pre-Workout Extreme', 'Fórmula pre-entreno con cafeína y beta-alanina', 65.00, 40, 1, 'Suplementos'),
('Omega 3 Fish Oil', 'Aceite de pescado con EPA y DHA, 90 cápsulas', 35.00, 100, 1, 'Suplementos'),
('Multivitamínico Sport', 'Complejo vitamínico para deportistas, 60 tabletas', 28.00, 80, 1, 'Suplementos'),

-- Ropa
('Camiseta Dry-Fit', 'Camiseta deportiva con tecnología de secado rápido', 35.00, 100, 2, 'Ropa'),
('Short de Entrenamiento', 'Short cómodo para todo tipo de ejercicio', 42.00, 80, 2, 'Ropa'),
('Leggins Deportivos', 'Leggins de compresión para mujer', 55.00, 70, 2, 'Ropa'),
('Sudadera con Capucha', 'Sudadera cómoda para calentamiento', 75.00, 50, 2, 'Ropa'),
('Top Deportivo Mujer', 'Top con soporte para entrenamiento intenso', 38.00, 60, 2, 'Ropa'),

-- Accesorios
('Guantes de Gimnasio', 'Guantes con soporte de muñeca para levantamiento', 25.00, 90, 3, 'Accesorios'),
('Shaker con Compartimentos', 'Shaker de 700ml con compartimentos para suplementos', 18.00, 120, 3, 'Accesorios'),
('Banda de Resistencia Set', 'Set de 5 bandas con diferentes resistencias', 32.00, 85, 3, 'Accesorios'),
('Colchoneta Yoga Pro', 'Colchoneta antideslizante de 6mm de grosor', 48.00, 40, 3, 'Accesorios'),
('Botella Térmica 1L', 'Botella de acero inoxidable, mantiene temperatura 24hrs', 45.00, 65, 3, 'Accesorios');

-- =====================================================
-- INSERTAR SERVICIOS
-- =====================================================
INSERT INTO servicio (nombre, descripcion, precio, duracion, id_categoria, categoria) VALUES
-- Entrenamiento Personal
('Entrenamiento Personal 1 Sesión', 'Sesión individual con entrenador certificado', 45.00, 60, 4, 'Entrenamiento Personal'),
('Plan Mensual Personal', 'Plan de 12 sesiones mensuales personalizadas', 480.00, 60, 4, 'Entrenamiento Personal'),
('Evaluación Física Completa', 'Análisis de composición corporal y evaluación física', 35.00, 45, 4, 'Entrenamiento Personal'),

-- Clases Grupales
('Clase de Spinning', 'Clase grupal de ciclismo indoor de alta intensidad', 15.00, 45, 5, 'Clases Grupales'),
('Clase de Yoga', 'Clase de yoga para todos los niveles', 18.00, 60, 5, 'Clases Grupales'),
('CrossFit WOD', 'Workout of the Day - Entrenamiento funcional', 20.00, 60, 5, 'Clases Grupales'),
('Zumba Fitness', 'Clase de baile aeróbico con ritmos latinos', 15.00, 50, 5, 'Clases Grupales'),
('Body Combat', 'Clase de artes marciales coreografiadas', 18.00, 55, 5, 'Clases Grupales'),

-- Nutrición
('Consulta Nutricional', 'Consulta con nutricionista deportivo', 60.00, 60, 6, 'Nutrición'),
('Plan Nutricional Mensual', 'Plan alimenticio personalizado con seguimiento', 250.00, 60, 6, 'Nutrición');

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


