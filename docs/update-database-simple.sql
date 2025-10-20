-- Script de actualización de base de datos AppleGym
-- Versión SIMPLIFICADA - Compatible con todas las versiones de MySQL
-- Este script actualiza la BD existente sin perder datos

USE applegym;

-- ========================================
-- PASO 1: Actualizar ENUMs
-- ========================================

-- 1.1 Tabla venta - Agregar estado PROCESANDO
ALTER TABLE venta 
MODIFY COLUMN estado ENUM('PENDIENTE', 'PAGADO', 'CANCELADO', 'REEMBOLSADO', 'COMPLETADO', 'PROCESANDO') NOT NULL DEFAULT 'PENDIENTE';

-- 1.2 Tabla carrito - Agregar estado PROCESADO
ALTER TABLE carrito 
MODIFY COLUMN estado ENUM('ACTIVO', 'CONFIRMADO', 'ABANDONADO', 'CONVERTIDO_A_VENTA', 'PROCESADO') NOT NULL DEFAULT 'ACTIVO';

-- 1.3 Tabla pago - Agregar estado COMPLETADO
ALTER TABLE pago 
MODIFY COLUMN estado_pago ENUM('PENDIENTE', 'PROCESANDO', 'APROBADO', 'RECHAZADO', 'CANCELADO', 'REEMBOLSADO', 'ERROR', 'COMPLETADO') NOT NULL DEFAULT 'PENDIENTE';

SELECT 'Paso 1 completado: ENUMs actualizados' AS mensaje;

-- ========================================
-- PASO 2: Agregar columnas a detalle_venta
-- ========================================

-- Verificar y agregar columna id_producto_servicio
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'applegym' 
    AND TABLE_NAME = 'detalle_venta' 
    AND COLUMN_NAME = 'id_producto_servicio'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE detalle_venta ADD COLUMN id_producto_servicio BIGINT AFTER id_venta', 
    'SELECT "Columna id_producto_servicio ya existe" AS info'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columna tipo
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'applegym' 
    AND TABLE_NAME = 'detalle_venta' 
    AND COLUMN_NAME = 'tipo'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE detalle_venta ADD COLUMN tipo VARCHAR(50) AFTER id_producto_servicio', 
    'SELECT "Columna tipo ya existe" AS info'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columna nombre
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'applegym' 
    AND TABLE_NAME = 'detalle_venta' 
    AND COLUMN_NAME = 'nombre'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE detalle_venta ADD COLUMN nombre VARCHAR(255) AFTER tipo', 
    'SELECT "Columna nombre ya existe" AS info'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Poblar las columnas nuevas si están vacías
UPDATE detalle_venta dv
LEFT JOIN producto p ON dv.id_producto = p.id_producto
LEFT JOIN servicio s ON dv.id_servicio = s.id_servicio
SET 
    dv.id_producto_servicio = COALESCE(dv.id_producto, dv.id_servicio),
    dv.tipo = CASE 
        WHEN dv.id_producto IS NOT NULL THEN 'PRODUCTO'
        WHEN dv.id_servicio IS NOT NULL THEN 'SERVICIO'
        ELSE NULL
    END,
    dv.nombre = COALESCE(p.nombre, s.nombre)
WHERE dv.id_producto_servicio IS NULL OR dv.tipo IS NULL OR dv.nombre IS NULL;

SELECT 'Paso 2 completado: Columnas agregadas a detalle_venta' AS mensaje;

-- ========================================
-- PASO 3: Mensaje de finalización
-- ========================================

SELECT '✅ Base de datos actualizada exitosamente' AS mensaje;
SELECT 'Ahora puedes compilar y ejecutar la aplicación' AS siguiente_paso;
