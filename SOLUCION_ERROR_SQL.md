# 🔧 SOLUCIÓN AL ERROR DE ACTUALIZACIÓN DE BASE DE DATOS

## ❌ Error Encontrado

```
ERROR 1064 (42000) at line 70: You have an error in your SQL syntax
```

## ✅ Solución

He creado **DOS scripts** para actualizar la base de datos:

### Opción 1: Script Simplificado (RECOMENDADO) ⭐

Este script solo actualiza lo esencial sin tocar las claves foráneas:

```bash
mysql -u root -p applegym < docs/update-database-simple.sql
```

**Este script:**
- ✅ Actualiza los ENUMs (agrega estados PROCESANDO, PROCESADO, COMPLETADO)
- ✅ Agrega columnas faltantes a `detalle_venta`
- ✅ Es compatible con todas las versiones de MySQL
- ✅ No requiere permisos especiales
- ✅ Es SUFICIENTE para que la aplicación funcione

### Opción 2: Script de Claves Foráneas (OPCIONAL)

Solo si quieres agregar ON DELETE CASCADE (no es necesario para la funcionalidad):

```bash
mysql -u root -p applegym < docs/update-foreign-keys.sql
```

---

## 📋 Pasos Correctos de Instalación

### 1. Actualizar Base de Datos

```bash
cd D:\ATAHUALPA\AppleGym
mysql -u root -p applegym < docs/update-database-simple.sql
```

Cuando te pida password, ingresa tu contraseña de MySQL root.

### 2. Verificar la Actualización

```sql
-- Conectarse a MySQL
mysql -u root -p applegym

-- Verificar que los ENUMs están actualizados
SHOW COLUMNS FROM venta LIKE 'estado';
SHOW COLUMNS FROM carrito LIKE 'estado';
SHOW COLUMNS FROM pago LIKE 'estado_pago';

-- Verificar que las columnas nuevas existen
SHOW COLUMNS FROM detalle_venta;

-- Salir
exit;
```

**Resultados esperados:**
- `venta.estado` debe incluir: 'PROCESANDO'
- `carrito.estado` debe incluir: 'PROCESADO'
- `pago.estado_pago` debe incluir: 'COMPLETADO'
- `detalle_venta` debe tener columnas: `id_producto_servicio`, `tipo`, `nombre`

### 3. Compilar el Proyecto

```bash
mvn clean install
```

### 4. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

---

## 🐛 Solución de Problemas

### Si el script falla con error de sintaxis:

**Opción A:** Ejecutar manualmente los comandos uno por uno

```sql
-- Conectarse a MySQL
mysql -u root -p

-- Cambiar a la base de datos
USE applegym;

-- Ejecutar comandos uno por uno:
ALTER TABLE venta MODIFY COLUMN estado ENUM('PENDIENTE', 'PAGADO', 'CANCELADO', 'REEMBOLSADO', 'COMPLETADO', 'PROCESANDO') NOT NULL DEFAULT 'PENDIENTE';

ALTER TABLE carrito MODIFY COLUMN estado ENUM('ACTIVO', 'CONFIRMADO', 'ABANDONADO', 'CONVERTIDO_A_VENTA', 'PROCESADO') NOT NULL DEFAULT 'ACTIVO';

ALTER TABLE pago MODIFY COLUMN estado_pago ENUM('PENDIENTE', 'PROCESANDO', 'APROBADO', 'RECHAZADO', 'CANCELADO', 'REEMBOLSADO', 'ERROR', 'COMPLETADO') NOT NULL DEFAULT 'PENDIENTE';

-- Salir
exit;
```

**Opción B:** Usar una herramienta visual (MySQL Workbench, phpMyAdmin, etc.)

1. Abre MySQL Workbench o phpMyAdmin
2. Conecta a tu base de datos `applegym`
3. Copia y pega el contenido de `docs/update-database-simple.sql`
4. Ejecuta el script

### Si tienes problemas de permisos:

```bash
# Asegúrate de usar el usuario correcto de MySQL
mysql -u root -p

# O si tienes otro usuario administrador:
mysql -u tu_usuario -p
```

---

## ✅ Verificación Final

Después de ejecutar el script, verifica que todo esté bien:

```sql
mysql -u root -p applegym

-- Verificar tabla venta
SELECT COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'venta' 
  AND COLUMN_NAME = 'estado';
-- Debe mostrar: enum('PENDIENTE','PAGADO','CANCELADO','REEMBOLSADO','COMPLETADO','PROCESANDO')

-- Verificar tabla detalle_venta
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'detalle_venta'
ORDER BY ORDINAL_POSITION;
-- Debe incluir: id_producto_servicio, tipo, nombre

exit;
```

---

## 📝 Notas Importantes

1. **El script simplificado es suficiente** - No necesitas ejecutar el script de claves foráneas para que la aplicación funcione.

2. **No perderás datos** - Ambos scripts están diseñados para actualizar la estructura sin borrar información existente.

3. **Backup recomendado** (opcional pero buena práctica):
   ```bash
   mysqldump -u root -p applegym > backup_antes_actualizacion.sql
   ```

4. **Si ya ejecutaste parte del script original** - No hay problema, puedes ejecutar el script simplificado, detectará qué ya está actualizado.

---

## 🎯 Resumen Rápido

**Lo que DEBES hacer:**
```bash
# 1. Actualizar BD
mysql -u root -p applegym < docs/update-database-simple.sql

# 2. Compilar
mvn clean install

# 3. Ejecutar
mvn spring-boot:run
```

**Y listo!** 🎉

---

## 📞 Si Sigues Teniendo Problemas

Si después de seguir estos pasos sigues teniendo errores:

1. Revisa la versión de MySQL: `mysql --version`
2. Verifica que puedes conectarte: `mysql -u root -p`
3. Verifica que la base de datos existe: `SHOW DATABASES;`
4. Comprueba los permisos del usuario

O simplemente ejecuta los comandos ALTER TABLE manualmente uno por uno en la consola de MySQL.
