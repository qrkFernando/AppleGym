-- AppleGym Database Schema
-- Script de creación de base de datos para el sistema AppleGym
-- Versión: 1.0.0

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS applegym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE applegym;

-- Tabla de categorías
CREATE TABLE categoria (
    id_categoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME,
    INDEX idx_categoria_nombre (nombre_categoria),
    INDEX idx_categoria_activo (activo)
);

-- Tabla de clientes
CREATE TABLE cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_cliente VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(15),
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME,
    INDEX idx_cliente_email (email),
    INDEX idx_cliente_activo (activo),
    INDEX idx_cliente_fecha_registro (fecha_registro)
);

-- Tabla de productos
CREATE TABLE producto (
    id_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    imagen_url VARCHAR(500),
    id_categoria BIGINT,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    INDEX idx_producto_nombre (nombre),
    INDEX idx_producto_categoria (id_categoria),
    INDEX idx_producto_activo (activo),
    INDEX idx_producto_precio (precio),
    INDEX idx_producto_stock (stock)
);

-- Tabla de servicios
CREATE TABLE servicio (
    id_servicio BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10,2) NOT NULL,
    duracion INT NOT NULL COMMENT 'Duración en días',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    imagen_url VARCHAR(500),
    capacidad_maxima INT,
    id_categoria BIGINT,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    INDEX idx_servicio_nombre (nombre),
    INDEX idx_servicio_categoria (id_categoria),
    INDEX idx_servicio_activo (activo),
    INDEX idx_servicio_precio (precio)
);

-- Tabla de promociones
CREATE TABLE promocion (
    id_promocion BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    descuento DECIMAL(5,2) NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    tipo ENUM('PORCENTAJE', 'MONTO_FIJO', 'DOS_POR_UNO', 'TRES_POR_DOS', 'ENVIO_GRATIS', 'PRIMERA_COMPRA', 'CLIENTE_FRECUENTE', 'TEMPORADA') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    monto_minimo DECIMAL(10,2),
    cantidad_maxima_uso INT,
    cantidad_usada INT NOT NULL DEFAULT 0,
    codigo_promocional VARCHAR(50) UNIQUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME,
    INDEX idx_promocion_nombre (nombre),
    INDEX idx_promocion_tipo (tipo),
    INDEX idx_promocion_fechas (fecha_inicio, fecha_fin),
    INDEX idx_promocion_activa (activo),
    INDEX idx_promocion_codigo (codigo_promocional)
);

-- Tabla de carritos
CREATE TABLE carrito (
    id_carrito BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ACTIVO', 'CONFIRMADO', 'ABANDONADO', 'CONVERTIDO_A_VENTA', 'PROCESADO') NOT NULL DEFAULT 'ACTIVO',
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    fecha_actualizacion DATETIME,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    INDEX idx_carrito_cliente (id_cliente),
    INDEX idx_carrito_fecha (fecha),
    INDEX idx_carrito_estado (estado)
);

-- Tabla de detalle de carritos
CREATE TABLE detalle_carrito (
    id_detalle_carrito BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_carrito BIGINT NOT NULL,
    tipo ENUM('PRODUCTO', 'SERVICIO') NOT NULL,
    id_producto BIGINT,
    id_servicio BIGINT,
    id_promocion BIGINT,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento_aplicado DECIMAL(10,2) DEFAULT 0.00,
    fecha_agregado DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio),
    FOREIGN KEY (id_promocion) REFERENCES promocion(id_promocion),
    INDEX idx_detalle_carrito_carrito (id_carrito),
    INDEX idx_detalle_carrito_tipo (tipo),
    INDEX idx_detalle_carrito_producto (id_producto),
    INDEX idx_detalle_carrito_servicio (id_servicio)
);

-- Tabla de ventas
CREATE TABLE venta (
    id_venta BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    fecha_venta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADO', 'CANCELADO', 'REEMBOLSADO', 'COMPLETADO', 'PROCESANDO') NOT NULL DEFAULT 'PENDIENTE',
    numero_venta VARCHAR(50) UNIQUE NOT NULL,
    observaciones TEXT,
    fecha_actualizacion DATETIME,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE RESTRICT,
    INDEX idx_venta_cliente (id_cliente),
    INDEX idx_venta_fecha (fecha_venta),
    INDEX idx_venta_estado (estado),
    INDEX idx_venta_numero (numero_venta)
);

-- Tabla de detalle de ventas
CREATE TABLE detalle_venta (
    id_detalle BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venta BIGINT NOT NULL,
    id_producto_servicio BIGINT NOT NULL,
    tipo VARCHAR(50),
    nombre VARCHAR(255),
    id_producto BIGINT,
    id_servicio BIGINT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento_aplicado DECIMAL(10,2) DEFAULT 0.00,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE SET NULL,
    FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio) ON DELETE SET NULL,
    INDEX idx_detalle_venta_venta (id_venta),
    INDEX idx_detalle_venta_producto (id_producto),
    INDEX idx_detalle_venta_servicio (id_servicio)
);

-- Tabla de pagos
CREATE TABLE pago (
    id_pago BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venta BIGINT NOT NULL,
    tipo_pago ENUM('EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'PAYPAL', 'MERCADO_PAGO', 'YAPE', 'PLIN') NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_pago ENUM('PENDIENTE', 'PROCESANDO', 'APROBADO', 'RECHAZADO', 'CANCELADO', 'REEMBOLSADO', 'ERROR', 'COMPLETADO') NOT NULL DEFAULT 'PENDIENTE',
    referencia_externa VARCHAR(100) UNIQUE,
    numero_transaccion VARCHAR(100) NOT NULL,
    detalles_pago VARCHAR(500),
    fecha_procesamiento DATETIME,
    codigo_autorizacion VARCHAR(50),
    fecha_actualizacion DATETIME,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    INDEX idx_pago_venta (id_venta),
    INDEX idx_pago_fecha (fecha_pago),
    INDEX idx_pago_estado (estado_pago),
    INDEX idx_pago_referencia (referencia_externa)
);

-- Tabla de comprobantes
CREATE TABLE comprobante (
    id_comprobante BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venta BIGINT NOT NULL UNIQUE,
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    numero_comprobante VARCHAR(50) UNIQUE NOT NULL,
    serie_comprobante VARCHAR(10),
    archivo_comprobante VARCHAR(500),
    tipo_comprobante ENUM('BOLETA', 'FACTURA', 'NOTA_CREDITO', 'NOTA_DEBITO', 'COMPROBANTE_PAGO', 'RECIBO') NOT NULL,
    estado_comprobante ENUM('GENERADO', 'ENVIADO', 'DESCARGADO', 'ERROR', 'ANULADO') NOT NULL DEFAULT 'GENERADO',
    hash_documento VARCHAR(256),
    url_descarga VARCHAR(500),
    fecha_envio DATETIME,
    observaciones TEXT,
    fecha_actualizacion DATETIME,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    INDEX idx_comprobante_venta (id_venta),
    INDEX idx_comprobante_fecha (fecha_emision),
    INDEX idx_comprobante_tipo (tipo_comprobante),
    INDEX idx_comprobante_numero (numero_comprobante)
);

-- Crear usuario para la aplicación (opcional)
-- CREATE USER 'applegym_user'@'localhost' IDENTIFIED BY 'AppleGym2024!';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON applegym.* TO 'applegym_user'@'localhost';
-- FLUSH PRIVILEGES;