# 🏋️ AppleGym - Sistema de Gestión de Gimnasio

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**Sistema web completo para la gestión integral de un gimnasio con E-commerce y Dashboard Administrativo**

[Características](#-características-principales) • [Instalación](#-instalación-rápida) • [Uso](#-uso) • [Documentación](#-documentación)

</div>

---

## 📋 Descripción

**AppleGym** es un sistema completo de gestión para gimnasios que implementa una arquitectura robusta Cliente-Servidor con API REST. El sistema permite:

- 🛒 **E-commerce** completo para productos y servicios del gimnasio
- 👥 **Gestión de clientes** con autenticación JWT
- 📊 **Dashboard administrativo** con reportes y estadísticas en tiempo real
- 💳 **Proceso de compra** completo con generación de comprobantes PDF
- 📈 **Exportación de reportes** a Excel y PDF
- 📦 **Control de inventario** automático

---

## ✨ Características Principales

### Para Clientes
- ✅ Registro y autenticación segura con JWT
- ✅ Catálogo de productos y servicios desde base de datos
- ✅ Carrito de compras persistente
- ✅ Proceso de compra completo
- ✅ Generación automática de comprobantes PDF
- ✅ Filtros y búsqueda de productos
- ✅ Visualización de stock en tiempo real

### Para Administradores
- ✅ Dashboard con estadísticas en tiempo real
- ✅ Gráficos interactivos (Chart.js)
  - Ventas por fecha
  - Top 10 productos más vendidos
  - Top 10 servicios más solicitados
- ✅ Reportes con filtros por fecha
- ✅ Exportación a Excel (Apache POI)
- ✅ Exportación a PDF (preparado)
- ✅ Gestión de stock automática
- ✅ Control de ventas y clientes

### Técnicas
- ✅ Arquitectura Cliente-Servidor con API REST
- ✅ Patrón MVC (Model-View-Controller)
- ✅ Principios SOLID
- ✅ Patrón DAO con Spring Data JPA
- ✅ Seguridad JWT con Spring Security
- ✅ Validación de datos con Bean Validation
- ✅ Sistema de roles (ADMIN/CLIENTE)
- ✅ Logging con Logback

---

## 🛠️ Tecnologías Utilizadas

### Backend
| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.0 | Framework |
| Spring Security | 3.2.0 | Autenticación JWT |
| Spring Data JPA | 3.2.0 | ORM |
| MySQL | 8.0+ | Base de datos |
| Maven | 3.8+ | Gestión de dependencias |

### Frontend
| Tecnología | Propósito |
|-----------|-----------|
| HTML5/CSS3 | Estructura y estilos |
| JavaScript ES6+ | Lógica del cliente |
| Chart.js | Gráficos interactivos |
| Font Awesome | Iconos |

### Librerías Principales
- **Apache POI** - Generación de archivos Excel
- **iText 7** - Generación de PDFs
- **Google Guava** - Utilidades
- **Apache Commons** - Funciones comunes
- **ModelMapper** - Mapeo de objetos
- **Jackson** - Procesamiento JSON
- **Logback** - Sistema de logging

---

## 🚀 Instalación Rápida

### Requisitos Previos

```bash
☑️ Java 17+
☑️ Maven 3.8+
☑️ MySQL 8.0+
☑️ Git
```

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/AppleGym.git
cd AppleGym
```

### Paso 2: Configurar Base de Datos

```sql
-- En MySQL Workbench o línea de comandos
mysql -u root -p

-- Ejecutar el script de instalación
source database_setup.sql
```

O ejecutar manualmente:
```sql
CREATE DATABASE applegym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Luego importar el archivo `database_setup.sql`

### Paso 3: Configurar Variables de Entorno

Editar `src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/applegym
spring.datasource.username=root
spring.datasource.password=tu_password

# JWT
jwt.secret=tu_clave_secreta_aqui_minimo_32_caracteres
jwt.expiration=86400000

# Email (opcional)
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_password
```

### Paso 4: Compilar y Ejecutar

```bash
# Limpiar y compilar
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

### Paso 5: Crear Usuario Administrador

1. **Registrarse en la aplicación** con:
   - Email: `applegym@admin.com`
   - Password: `applegymadmin`
   - Nombre: `Administrador AppleGym`

2. **En MySQL, ejecutar:**
   ```sql
   UPDATE cliente SET rol = 'ADMIN' WHERE email = 'applegym@admin.com';
   ```

3. **Iniciar sesión** con las credenciales de admin

---

## 📖 Uso

### Como Cliente

1. **Abrir navegador** en `http://localhost:8080`
2. **Registrarse** o **Iniciar sesión**
3. **Explorar catálogo** de productos y servicios
4. **Agregar al carrito** los items deseados
5. **Realizar compra** y descargar comprobante PDF

### Como Administrador

1. **Iniciar sesión** con email `applegym@admin.com`
2. Automáticamente redirige al **Dashboard**
3. **Explorar estadísticas** en tiempo real
4. **Filtrar ventas** por fecha
5. **Exportar reportes** a Excel
6. **Ver top productos/servicios**

---

## 📂 Estructura del Proyecto

```
AppleGym/
├── src/
│   ├── main/
│   │   ├── java/com/applegym/
│   │   │   ├── config/              # Configuraciones
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # Entidades JPA
│   │   │   ├── exception/           # Excepciones personalizadas
│   │   │   ├── repository/          # Repositorios DAO
│   │   │   ├── security/            # JWT y Seguridad
│   │   │   ├── service/             # Lógica de negocio
│   │   │   │   └── impl/           # Implementaciones
│   │   │   └── util/               # Utilidades
│   │   └── resources/
│   │       ├── static/              # Frontend
│   │       │   ├── css/            # Estilos
│   │       │   ├── js/             # JavaScript
│   │       │   ├── images/         # Imágenes
│   │       │   ├── index.html      # Página principal
│   │       │   ├── catalogo.html   # Catálogo
│   │       │   └── admin-dashboard.html  # Dashboard admin
│   │       ├── application.properties    # Configuración
│   │       └── logback-spring.xml       # Logging
│   └── test/                        # Tests
├── docs/                            # Documentación adicional
├── database_setup.sql               # Script de instalación de BD
├── pom.xml                         # Dependencias Maven
└── README.md                       # Este archivo
```

---

## 🔐 API Endpoints

### Autenticación
```
POST   /api/auth/register          - Registrar nuevo usuario
POST   /api/auth/login             - Iniciar sesión
POST   /api/auth/validate-token    - Validar token JWT
```

### Catálogo (Público)
```
GET    /api/catalogo               - Obtener catálogo completo
GET    /api/catalogo/productos     - Listar productos
GET    /api/catalogo/servicios     - Listar servicios
```

### Carrito (Autenticado)
```
GET    /api/carrito                - Obtener carrito actual
POST   /api/carrito/agregar        - Agregar item
PUT    /api/carrito/actualizar     - Actualizar cantidad
DELETE /api/carrito/eliminar/{id}  - Eliminar item
DELETE /api/carrito/limpiar        - Vaciar carrito
```

### Ventas (Autenticado)
```
POST   /api/ventas/procesar        - Procesar venta
GET    /api/ventas/{id}            - Obtener detalle de venta
GET    /api/ventas/{id}/comprobante - Descargar comprobante PDF
```

### Reportes (Admin)
```
GET    /api/reportes/resumen                 - Resumen general
GET    /api/reportes/productos-top           - Top productos
GET    /api/reportes/servicios-top           - Top servicios
GET    /api/reportes/ventas-por-fecha        - Ventas por fecha
GET    /api/reportes/export/excel            - Exportar Excel
GET    /api/reportes/export/pdf              - Exportar PDF
```

---

## 🗄️ Modelo de Base de Datos

### Tablas Principales

| Tabla | Descripción |
|-------|-------------|
| `cliente` | Usuarios del sistema (clientes y admin) |
| `categoria` | Categorías de productos/servicios |
| `producto` | Productos disponibles |
| `servicio` | Servicios del gimnasio |
| `carrito` | Carritos de compra |
| `detalle_carrito` | Items del carrito |
| `venta` | Ventas procesadas |
| `detalle_venta` | Detalle de cada venta |
| `pago` | Información de pagos |
| `comprobante` | Comprobantes generados |

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Tests con cobertura
mvn test jacoco:report

# Tests específicos
mvn test -Dtest=ClienteServiceTest
```

---

## 📊 Características del Dashboard

### Estadísticas en Tiempo Real
- 💰 Ventas totales
- 🛒 Número de ventas
- 👥 Clientes activos
- 📦 Total de productos

### Gráficos Dinámicos
- 📈 Gráfico de línea: Ventas por fecha
- 📊 Gráfico de barras: Top 10 productos
- 📊 Gráfico de barras: Top 10 servicios

### Funcionalidades
- 🔍 Filtros por rango de fechas
- 📥 Exportación a Excel
- 📄 Exportación a PDF (preparado)
- 🔄 Actualización automática
- 📋 Tablas detalladas

---

## 🔒 Seguridad

### Implementada
- ✅ Autenticación JWT con tokens de 24 horas
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Validación de datos de entrada
- ✅ Protección contra SQL Injection
- ✅ Sistema de roles (ADMIN/CLIENTE)
- ✅ CORS configurado
- ✅ Headers de seguridad

### Recomendaciones Adicionales
- 🔐 Configurar HTTPS en producción
- 🚦 Implementar rate limiting
- 📝 Monitoreo de seguridad con logs
- 🔄 Rotación regular de secrets

---

## 📝 Logging

Los logs se generan en `logs/applegym.log` con:
- Rotación diaria
- Máximo 30 días de retención
- Niveles: ERROR, WARN, INFO, DEBUG

---

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

---

## 📄 Licencia

Este proyecto es desarrollado con fines educativos.

---

## 👥 Autores

- **Equipo AppleGym** - Desarrollo completo del sistema

---

## 🙏 Agradecimientos

- Spring Framework Team
- Chart.js contributors
- Apache POI contributors
- Comunidad de desarrolladores Java

---

## 📞 Soporte

Si encuentras algún problema o tienes preguntas:

1. Revisa la [documentación](docs/)
2. Abre un [Issue](https://github.com/tu-usuario/AppleGym/issues)
3. Contacta al equipo de desarrollo

---

<div align="center">

**⭐ Si te gusta este proyecto, dale una estrella en GitHub ⭐**

Hecho con ❤️ por el equipo de AppleGym

</div>
