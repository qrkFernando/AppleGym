# ⚡ Inicio Rápido - AppleGym

**¿Primera vez? Sigue estos pasos para tener AppleGym corriendo en 5 minutos.**

---

## 📋 Pre-requisitos

✅ Java 17+  
✅ Maven 3.8+  
✅ MySQL 8.0+  
✅ Git  

---

## 🚀 Instalación en 5 Pasos

### 1️⃣ Clonar Repositorio

```bash
git clone https://github.com/tu-usuario/AppleGym.git
cd AppleGym
```

### 2️⃣ Crear Base de Datos

```bash
# Conectar a MySQL
mysql -u root -p

# Ejecutar script
source database_setup.sql
```

### 3️⃣ Configurar Contraseña

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.password=TU_PASSWORD_MYSQL_AQUI
```

### 4️⃣ Iniciar Aplicación

```bash
mvn spring-boot:run
```

Espera a ver: `Started AppleGymApplication`

### 5️⃣ Abrir Navegador

```
http://localhost:8080
```

---

## 👤 Crear Usuario Admin

### En la Aplicación:

1. Click en "Registrarse"
2. Usar email: `applegym@admin.com`
3. Password: `applegymadmin`
4. Completar registro

### En MySQL:

```sql
UPDATE cliente SET rol = 'ADMIN' WHERE email = 'applegym@admin.com';
```

### Iniciar Sesión:

- Email: `applegym@admin.com`
- Password: `applegymadmin`
- Automáticamente redirige al Dashboard ✅

---

## ✅ Verificar que Funciona

### Cliente:
- ✅ Catálogo carga productos
- ✅ Carrito funciona
- ✅ Compra exitosa

### Admin:
- ✅ Dashboard muestra estadísticas
- ✅ Gráficos cargan
- ✅ Exportar Excel funciona

---

## 🆘 ¿Problemas?

### Error de conexión MySQL
```properties
# Verifica en application.properties:
spring.datasource.username=root
spring.datasource.password=tu_password_correcta
```

### Puerto 8080 ocupado
```properties
# Cambia el puerto:
server.port=8081
```

### Productos no aparecen
```sql
-- Verifica que haya productos:
SELECT COUNT(*) FROM producto WHERE activo = 1;
```

---

## 📚 Más Información

- [README completo](README.md)
- [Guía de instalación detallada](INSTALLATION.md)
- [Documentación](docs/)

---

<div align="center">

**¡Listo para usar AppleGym! 🎉**

</div>
