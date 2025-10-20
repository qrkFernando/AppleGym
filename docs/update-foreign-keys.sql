-- Script OPCIONAL para actualizar claves foráneas
-- Solo ejecutar si quieres agregar ON DELETE CASCADE
-- Este script es OPCIONAL - la aplicación funciona sin ejecutarlo

USE applegym;

-- IMPORTANTE: Este script puede fallar si hay datos que violan las restricciones
-- Es recomendable hacer un backup antes de ejecutarlo

-- ========================================
-- Actualizar Foreign Keys con ON DELETE CASCADE
-- ========================================

-- Mostrar advertencia
SELECT '⚠️ ADVERTENCIA: Este script modificará las claves foráneas' AS mensaje;
SELECT 'Se recomienda hacer backup antes de continuar' AS recomendacion;

-- Para tabla pago
-- Buscar el nombre real de la constraint
SELECT CONSTRAINT_NAME 
INTO @fk_name
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'applegym' 
  AND TABLE_NAME = 'pago' 
  AND REFERENCED_TABLE_NAME = 'venta'
LIMIT 1;

-- Solo si existe, eliminar y recrear
SET @sql = IF(@fk_name IS NOT NULL, 
    CONCAT('ALTER TABLE pago DROP FOREIGN KEY ', @fk_name),
    'SELECT "No hay FK para pago" AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Recrear con ON DELETE CASCADE
ALTER TABLE pago 
ADD CONSTRAINT fk_pago_venta 
FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE;

SELECT 'FK de pago actualizada' AS mensaje;

-- Para tabla comprobante
SELECT CONSTRAINT_NAME 
INTO @fk_name
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'applegym' 
  AND TABLE_NAME = 'comprobante' 
  AND REFERENCED_TABLE_NAME = 'venta'
LIMIT 1;

SET @sql = IF(@fk_name IS NOT NULL, 
    CONCAT('ALTER TABLE comprobante DROP FOREIGN KEY ', @fk_name),
    'SELECT "No hay FK para comprobante" AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE comprobante 
ADD CONSTRAINT fk_comprobante_venta 
FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE;

SELECT 'FK de comprobante actualizada' AS mensaje;

-- Para tabla detalle_venta
SELECT CONSTRAINT_NAME 
INTO @fk_name
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'applegym' 
  AND TABLE_NAME = 'detalle_venta' 
  AND REFERENCED_TABLE_NAME = 'venta'
LIMIT 1;

SET @sql = IF(@fk_name IS NOT NULL, 
    CONCAT('ALTER TABLE detalle_venta DROP FOREIGN KEY ', @fk_name),
    'SELECT "No hay FK para detalle_venta" AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE detalle_venta 
ADD CONSTRAINT fk_detalle_venta_venta 
FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE;

SELECT 'FK de detalle_venta actualizada' AS mensaje;

-- Para tabla carrito
SELECT CONSTRAINT_NAME 
INTO @fk_name
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'applegym' 
  AND TABLE_NAME = 'carrito' 
  AND REFERENCED_TABLE_NAME = 'cliente'
LIMIT 1;

SET @sql = IF(@fk_name IS NOT NULL, 
    CONCAT('ALTER TABLE carrito DROP FOREIGN KEY ', @fk_name),
    'SELECT "No hay FK para carrito" AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE carrito 
ADD CONSTRAINT fk_carrito_cliente 
FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE;

SELECT 'FK de carrito actualizada' AS mensaje;

-- Para tabla venta
SELECT CONSTRAINT_NAME 
INTO @fk_name
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'applegym' 
  AND TABLE_NAME = 'venta' 
  AND REFERENCED_TABLE_NAME = 'cliente'
LIMIT 1;

SET @sql = IF(@fk_name IS NOT NULL, 
    CONCAT('ALTER TABLE venta DROP FOREIGN KEY ', @fk_name),
    'SELECT "No hay FK para venta" AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE venta 
ADD CONSTRAINT fk_venta_cliente 
FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE RESTRICT;

SELECT 'FK de venta actualizada' AS mensaje;

SELECT '✅ Claves foráneas actualizadas exitosamente' AS mensaje_final;
