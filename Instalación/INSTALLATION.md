# 📦 Guía de Instalación Completa - AppleGym

Esta guía te llevará paso a paso para instalar y configurar AppleGym en tu máquina local.

---

## ⚙️ Requisitos del Sistema

### Software Requerido

| Software | Versión Mínima | Descarga |
|----------|---------------|----------|
| Java JDK | 17+ | [Download](https://www.oracle.com/java/technologies/downloads/) |
| Maven | 3.8+ | [Download](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [Download](https://dev.mysql.com/downloads/mysql/) |
| Git | Latest | [Download](https://git-scm.com/downloads) |

### Verificar Instalaciones

```bash
# Verificar Java
java -version
# Debe mostrar: java version "17" o superior

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.8.x o superior

# Verificar MySQL
mysql --version
# Debe mostrar: mysql Ver 8.0.x o superior

# Verificar Git
git --version
# Debe mostrar: git version 2.x.x o superior
```

---

## 📥 Paso 1: Clonar el Repositorio

```bash
# Clonar desde GitHub
git clone https://github.com/tu-usuario/AppleGym.git

# Entrar al directorio
cd AppleGym

# Verificar que tienes todos los archivos
ls -la
```

Deberías ver:
```
📁 src/
📁 docs/
📄 database_setup.sql
📄 pom.xml
📄 README.md
📄 INSTALLATION.md
```

---

## 🗄️ Paso 2: Configurar Base de Datos

### Opción A: Usando MySQL Workbench (Recomendado)

1. **Abrir MySQL Workbench**
2. **Conectar a tu servidor local**
3. **Abrir el archivo** `database_setup.sql`
4. **Ejecutar todo el script** (⚡ icono de rayo o Ctrl+Shift+Enter)
5. **Verificar creación:**
   ```sql
   SHOW DATABASES;
   USE applegym;
   SHOW TABLES;
   ```

### Opción B: Usando Línea de Comandos

```bash
# Conectar a MySQL
mysql -u root -p

# En el prompt de MySQL, ejecutar:
source /ruta/completa/a/database_setup.sql

# O en Windows:
source C:/ruta/completa/a/database_setup.sql

# Verificar
SHOW DATABASES;
USE applegym;
SHOW TABLES;
SELECT COUNT(*) FROM producto;
```

Deberías ver:
- ✅ Base de datos `applegym` creada
- ✅ 10 tablas creadas
- ✅ 6 categorías insertadas
- ✅ ~15 productos insertados
- ✅ ~10 servicios insertados

---

## ⚙️ Paso 3: Configurar Variables de la Aplicación

### 3.1 Editar application.properties

Abrir: `src/main/resources/application.properties`

```properties
# ===========================================
# CONFIGURACIÓN DE BASE DE DATOS
# ===========================================
spring.datasource.url=jdbc:mysql://localhost:3306/applegym?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI

# ===========================================
# CONFIGURACIÓN DE JPA/HIBERNATE
# ===========================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# ===========================================
# CONFIGURACIÓN DE JWT
# ===========================================
jwt.secret=AppleGym_Secret_Key_2024_Minimo_32_Caracteres_Aqui
jwt.expiration=86400000

# ===========================================
# CONFIGURACIÓN DE SERVIDOR
# ===========================================
server.port=8080
server.error.include-message=always
server.error.include-stacktrace=never

# ===========================================
# CONFIGURACIÓN DE EMAIL (OPCIONAL)
# ===========================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ===========================================
# LOGGING
# ===========================================
logging.level.root=INFO
logging.level.com.applegym=DEBUG
logging.file.name=logs/applegym.log
```

### 3.2 Notas Importantes

- **spring.datasource.password**: Cambia `TU_PASSWORD_AQUI` por tu contraseña de MySQL
- **jwt.secret**: Puedes dejarlo así o cambiar a tu propia clave (mínimo 32 caracteres)
- **Email (Opcional)**: Solo necesario si quieres enviar emails. Para Gmail, necesitas crear una [App Password](https://support.google.com/accounts/answer/185833)

---

## 🔨 Paso 4: Compilar el Proyecto

```bash
# Limpiar builds anteriores
mvn clean

# Compilar el proyecto
mvn compile

# Deberías ver:
# [INFO] BUILD SUCCESS
```

Si hay errores:
- Verifica que Java 17+ esté instalado
- Verifica que Maven esté configurado correctamente
- Revisa que application.properties tenga los datos correctos

---

## 🚀 Paso 5: Ejecutar la Aplicación

```bash
# Iniciar la aplicación
mvn spring-boot:run

# Espera a ver:
# Started AppleGymApplication in X.XXX seconds
```

La aplicación estará disponible en: **http://localhost:8080**

Para detener: `Ctrl + C`

---

## 👤 Paso 6: Crear Usuario Administrador

### 6.1 Registrar Cuenta

1. **Abrir navegador** en `http://localhost:8080`
2. **Hacer clic** en "Registrarse"
3. **Llenar formulario** con:
   ```
   Nombre: Administrador AppleGym
   Email: applegym@admin.com
   Password: applegymadmin
   Teléfono: 999999999
   Dirección: AppleGym HQ
   ```
4. **Click** en "Registrarse"

### 6.2 Asignar Rol de Administrador

**En MySQL ejecutar:**

```sql
USE applegym;

UPDATE cliente 
SET rol = 'ADMIN' 
WHERE email = 'applegym@admin.com';

-- Verificar
SELECT email, nombre_cliente, rol, activo 
FROM cliente 
WHERE email = 'applegym@admin.com';
```

Deberías ver:
```
email: applegym@admin.com
rol: ADMIN
activo: 1
```


## ✅ Paso 7: Verificar Instalación

### Verificar Frontend

1. **Página Principal**: `http://localhost:8080`
   - ✅ Debe cargar sin errores
   - ✅ Debe mostrar navbar
   - ✅ Botones de login/registro funcionan

2. **Catálogo**: `http://localhost:8080/catalogo.html`
   - ✅ Debe mostrar productos y servicios
   - ✅ Filtros funcionan
   - ✅ Búsqueda funciona

3. **Dashboard Admin**: Login como admin
   - ✅ Redirige a `admin-dashboard.html`
   - ✅ Muestra estadísticas
   - ✅ Gráficos se cargan
   - ✅ Exportar Excel funciona

### Verificar Backend (APIs)

Usar Postman o curl:

```bash
# Test 1: Catálogo
curl http://localhost:8080/api/catalogo


### Verificar Base de Datos

```sql
USE applegym;

-- Verificar datos
SELECT COUNT(*) AS total_categorias FROM categoria;
SELECT COUNT(*) AS total_productos FROM producto;
SELECT COUNT(*) AS total_servicios FROM servicio;
SELECT COUNT(*) AS total_clientes FROM cliente;

-- Deberías ver:
-- total_categorias: 6
-- total_productos: 15-20
-- total_servicios: 10-15
-- total_clientes: 1 (el admin)
```

---

## 🎯 Paso 8: Probar Funcionalidades

### Como Cliente

1. **Registrar nueva cuenta** de cliente normal
2. **Explorar catálogo**
3. **Agregar productos al carrito**
4. **Realizar una compra**
5. **Descargar comprobante PDF**

### Como Admin

1. **Login como admin**
2. **Ver estadísticas** en tiempo real
3. **Aplicar filtros** de fecha
4. **Ver gráficos** de ventas
5. **Exportar reporte** a Excel
6. **Ver top productos** y servicios

---

## 🔧 Solución de Problemas Comunes

### Problema 1: Error al conectar a MySQL

```
Error: Access denied for user 'root'@'localhost'
```

**Solución:**
- Verifica tu contraseña en `application.properties`
- Verifica que MySQL esté corriendo: `mysql -u root -p`

### Problema 2: Puerto 8080 en uso

```
Error: Web server failed to start. Port 8080 was already in use.
```

**Solución:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID_NUMBER] /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

O cambiar el puerto en `application.properties`:
```properties
server.port=8081
```

### Problema 3: BUILD FAILURE en Maven

**Solución:**
```bash
# Limpiar completamente
mvn clean

# Forzar actualización de dependencias
mvn clean install -U

# Si persiste, eliminar .m2/repository y reinstalar
```

### Problema 4: No carga el Dashboard Admin

**Solución:**
1. Limpiar localStorage del navegador (F12 → Application → Clear storage)
2. Verificar que el rol sea 'ADMIN' en la base de datos
3. Verificar en consola del navegador (F12) si hay errores

### Problema 5: Productos no aparecen en catálogo

**Solución:**
```sql
-- Verificar que los productos estén activos
SELECT * FROM producto WHERE activo = 1;

-- Si están desactivados, activar:
UPDATE producto SET activo = TRUE;
```

---

## 📚 Siguientes Pasos

Una vez instalado:

1. ✅ Lee el [README.md](README.md) para entender el proyecto
2. ✅ Revisa la [documentación](docs/) para más detalles
3. ✅ Explora el código en `src/main/java`
4. ✅ Revisa los tests en `src/test/java`
5. ✅ Personaliza productos y servicios en la BD

---

## 🆘 Obtener Ayuda

Si tienes problemas:

1. **Revisa logs**: `logs/applegym.log`
2. **Consola del navegador**: F12 para ver errores de frontend
3. **Stack trace**: Si hay error en backend, copia el stack trace
4. **Abre un Issue**: En GitHub con detalles del error

---

## ✅ Checklist de Instalación Completa

- [ ] Java 17+ instalado y verificado
- [ ] Maven 3.8+ instalado y verificado
- [ ] MySQL 8.0+ instalado y corriendo
- [ ] Git instalado
- [ ] Repositorio clonado
- [ ] Base de datos creada (`applegym`)
- [ ] Tablas creadas (10 tablas)
- [ ] Datos iniciales cargados
- [ ] application.properties configurado
- [ ] Proyecto compilado exitosamente
- [ ] Aplicación corriendo en puerto 8080
- [ ] Usuario admin creado y configurado
- [ ] Login admin funciona
- [ ] Dashboard admin carga correctamente
- [ ] Catálogo muestra productos
- [ ] Compra de prueba exitosa

---

<div align="center">

**¡Instalación completada! 🎉**

¿Necesitas ayuda? Abre un [Issue en GitHub](https://github.com/tu-usuario/AppleGym/issues)

</div>
