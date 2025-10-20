-- Script de actualización de base de datos AppleGym
-- Este script actualiza la BD existente sin perder datos

USE applegym;

-- 1. Actualizar tabla venta para agregar estado PROCESANDO
ALTER TABLE venta 
MODIFY COLUMN estado ENUM('PENDIENTE', 'PAGADO', 'CANCELADO', 'REEMBOLSADO', 'COMPLETADO', 'PROCESANDO') NOT NULL DEFAULT 'PENDIENTE';

-- 2. Actualizar tabla carrito para agregar estado PROCESADO
ALTER TABLE carrito 
MODIFY COLUMN estado ENUM('ACTIVO', 'CONFIRMADO', 'ABANDONADO', 'CONVERTIDO_A_VENTA', 'PROCESADO') NOT NULL DEFAULT 'ACTIVO';

-- 3. Actualizar tabla pago para agregar estado COMPLETADO
ALTER TABLE pago 
MODIFY COLUMN estado_pago ENUM('PENDIENTE', 'PROCESANDO', 'APROBADO', 'RECHAZADO', 'CANCELADO', 'REEMBOLSADO', 'ERROR', 'COMPLETADO') NOT NULL DEFAULT 'PENDIENTE';

-- 4. Verificar si existen las columnas necesarias en detalle_venta
-- Agregar columnas si no existen
SET @dbname = DATABASE();
SET @tablename = 'detalle_venta';
SET @columnname = 'id_producto_servicio';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE (TABLE_SCHEMA = @dbname)
     AND (TABLE_NAME = @tablename)
     AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' BIGINT NOT NULL AFTER id_venta')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Agregar columna tipo
SET @columnname = 'tipo';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE (TABLE_SCHEMA = @dbname)
     AND (TABLE_NAME = @tablename)
     AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(50) AFTER id_producto_servicio')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Agregar columna nombre
SET @columnname = 'nombre';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE (TABLE_SCHEMA = @dbname)
     AND (TABLE_NAME = @tablename)
     AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER tipo')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 5. Verificar y recrear claves foráneas con ON DELETE CASCADE
-- Nota: Se verifica primero si existe para evitar errores

-- Para tabla pago
SET @constraint_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'applegym' AND TABLE_NAME = 'pago' AND CONSTRAINT_NAME = 'pago_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE pago DROP FOREIGN KEY pago_ibfk_1', 'SELECT "pago_ibfk_1 no existe"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE pago ADD CONSTRAINT pago_ibfk_1 
  FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE;

-- Para tabla comprobante
SET @constraint_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'applegym' AND TABLE_NAME = 'comprobante' AND CONSTRAINT_NAME = 'comprobante_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE comprobante DROP FOREIGN KEY comprobante_ibfk_1', 'SELECT "comprobante_ibfk_1 no existe"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE comprobante ADD CONSTRAINT comprobante_ibfk_1 
  FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE;

-- Para tabla detalle_venta
SET @constraint_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'applegym' AND TABLE_NAME = 'detalle_venta' AND CONSTRAINT_NAME = 'detalle_venta_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE detalle_venta DROP FOREIGN KEY detalle_venta_ibfk_1', 'SELECT "detalle_venta_ibfk_1 no existe"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE detalle_venta ADD CONSTRAINT detalle_venta_ibfk_1 
  FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE;

-- Para tabla carrito
SET @constraint_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'applegym' AND TABLE_NAME = 'carrito' AND CONSTRAINT_NAME = 'carrito_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE carrito DROP FOREIGN KEY carrito_ibfk_1', 'SELECT "carrito_ibfk_1 no existe"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE carrito ADD CONSTRAINT carrito_ibfk_1 
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE;

-- Para tabla venta
SET @constraint_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'applegym' AND TABLE_NAME = 'venta' AND CONSTRAINT_NAME = 'venta_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE venta DROP FOREIGN KEY venta_ibfk_1', 'SELECT "venta_ibfk_1 no existe"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE venta ADD CONSTRAINT venta_ibfk_1 
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE RESTRICT;

SELECT 'Base de datos actualizada exitosamente' AS mensaje;
