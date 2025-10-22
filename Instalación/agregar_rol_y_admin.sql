-- Script para agregar columna rol y crear usuario administrador
-- AppleGym - Sistema de Roles

USE applegym;

-- Agregar columna rol a la tabla cliente si no existe
ALTER TABLE cliente 
ADD COLUMN IF NOT EXISTS rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE' AFTER activo;

-- Actualizar clientes existentes para que tengan rol CLIENTE
UPDATE cliente 
SET rol = 'CLIENTE' 
WHERE rol IS NULL OR rol = '';

-- ===========================================================================
-- CREAR ADMINISTRADOR - OPCIÓN 1: Si ya te registraste como admin
-- ===========================================================================
-- Si ya creaste una cuenta con el email applegym@admin.com, solo actualiza el rol:

UPDATE cliente 
SET rol = 'ADMIN' 
WHERE email = 'applegym@admin.com';

-- ===========================================================================
-- CREAR ADMINISTRADOR - OPCIÓN 2: Registro manual
-- ===========================================================================
-- Si NO te has registrado aún:
-- 1. Ve a la aplicación web
-- 2. Regístrate con:
--    - Email: applegym@admin.com
--    - Password: applegymadmin
--    - Nombre: Administrador AppleGym
--    - Teléfono: 999999999
-- 3. Luego ejecuta el UPDATE de arriba para cambiar el rol a ADMIN

SELECT 'Columna rol agregada exitosamente!' AS mensaje;
SELECT 'Si el admin existe, su rol ha sido actualizado' AS mensaje;
SELECT * FROM cliente WHERE email = 'applegym@admin.com';
