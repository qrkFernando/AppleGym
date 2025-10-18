# AppleGym - Sistema de Gestión para Gimnasio

## Descripción

AppleGym es un sistema completo de gestión para gimnasios que implementa una arquitectura Cliente-Servidor robusta y segura. El sistema permite a los clientes navegar por el catálogo de productos y servicios, gestionar carritos de compra, realizar pagos y recibir comprobantes digitales.

## Características Principales

- ✅ **Arquitectura Cliente-Servidor** con API REST
- ✅ **Patrón MVC** (Model-View-Controller)
- ✅ **Principios SOLID** en el diseño
- ✅ **Patrón DAO** con Spring Data JPA
- ✅ **TDD** (Test Driven Development)
- ✅ **Seguridad JWT** con Spring Security
- ✅ **Validación de datos** con Bean Validation
- ✅ **Logging** con Logback
- ✅ **Librerías de apoyo**: Google Guava, Apache POI, Apache Commons

## Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven** (Gestión de dependencias)

### Librerías Principales
- **Google Guava** - Utilidades y colecciones
- **Apache POI** - Manipulación de documentos Office
- **Apache Commons** - Utilidades comunes
- **Logback** - Sistema de logging
- **ModelMapper** - Mapeo de objetos
- **Jackson** - Procesamiento JSON

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **H2 Database** (Tests)

## Casos de Uso Implementados

### CU01 - Registro de Cliente
- Registro de nuevos usuarios con validación de datos
- Verificación de email único
- Encriptación segura de contraseñas
- Generación automática de token JWT

### CU02 - Inicio de Sesión
- Autenticación con JWT
- Validación de credenciales
- Manejo de sesiones stateless

### CU03 - Explorar Productos y Servicios
- Catálogo público accesible sin autenticación
- Filtrado por categorías, precios y términos de búsqueda
- Paginación y ordenamiento
- Información de disponibilidad

### CU04 - Seleccionar Producto o Servicio
- Visualización detallada de productos/servicios
- Información de stock y disponibilidad
- Productos/servicios relacionados
- Validación de estado activo

### CU05 - Agregar al Carrito
- Gestión de carrito de compras por usuario
- Validación de disponibilidad
- Cálculo automático de totales
- Aplicación de promociones

### CU06-CU11 - Proceso de Compra Completo
- Confirmación de carrito
- Selección de método de pago
- Procesamiento de pagos
- Generación de comprobantes digitales
- Autenticación antes de compra
- Funcionalidad "Seguir comprando"

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/applegym/
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositorios DAO
│   │   ├── service/         # Lógica de negocio
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── config/         # Configuraciones
│   │   ├── security/       # Seguridad JWT
│   │   ├── exception/      # Excepciones personalizadas
│   │   └── util/           # Utilidades
│   └── resources/
│       ├── application.yml  # Configuración principal
│       └── logback-spring.xml # Configuración de logging
└── test/
    └── java/com/applegym/  # Tests unitarios e integración
```

## Modelo de Base de Datos

### Entidades Principales

- **Cliente** - Información de usuarios del sistema
- **Producto** - Productos disponibles (suplementos, ropa, etc.)
- **Servicio** - Servicios del gimnasio (membresías, entrenamientos)
- **Categoria** - Categorización de productos y servicios
- **Carrito** - Carritos de compra de clientes
- **Venta** - Transacciones completadas
- **Pago** - Información de pagos realizados
- **Comprobante** - Documentos generados
- **Promocion** - Ofertas y descuentos

## Configuración del Proyecto

### Requisitos Previos

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd Proyecto-Integrador-AppleGym
```

2. **Configurar base de datos MySQL**
```sql
CREATE DATABASE applegym;
CREATE USER 'applegym_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON applegym.* TO 'applegym_user'@'localhost';
```

3. **Configurar variables de entorno**
```bash
export DB_USERNAME=applegym_user
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key_here
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_email_password
```

4. **Compilar y ejecutar**
```bash
mvn clean install
mvn spring-boot:run
```

### Configuración de Desarrollo

La aplicación se ejecuta por defecto en `http://localhost:8080/api`

#### Endpoints Principales

- **Autenticación:**
  - `POST /api/auth/register` - Registro de usuario
  - `POST /api/auth/login` - Inicio de sesión
  - `POST /api/auth/validate-token` - Validar token

- **Catálogo:**
  - `GET /api/catalogo` - Catálogo completo
  - `GET /api/catalogo/productos/{id}` - Detalle de producto
  - `GET /api/catalogo/servicios/{id}` - Detalle de servicio
  - `GET /api/catalogo/buscar` - Búsqueda

- **Clientes (autenticado):**
  - `GET /api/clientes/perfil` - Perfil del cliente
  - `PUT /api/clientes/perfil` - Actualizar perfil

## Principios de Desarrollo Aplicados

### SOLID
- **S** - Single Responsibility: Cada clase tiene una responsabilidad específica
- **O** - Open/Closed: Extensible sin modificar código existente
- **L** - Liskov Substitution: Las implementaciones respetan los contratos
- **I** - Interface Segregation: Interfaces específicas por funcionalidad
- **D** - Dependency Inversion: Dependencias por abstracción, no implementación

### Patrón DAO
- Separación clara entre lógica de negocio y acceso a datos
- Repositories implementan operaciones específicas de base de datos
- Services manejan la lógica de negocio

### TDD (Test Driven Development)
- Tests unitarios para cada servicio
- Cobertura de casos de éxito y error
- Mocks para dependencias externas
- Tests de integración para endpoints

## Seguridad

### Implementada
- **JWT Authentication** - Tokens seguros para autenticación
- **Password Encoding** - BCrypt con factor 12
- **CORS Configuration** - Configuración de orígenes permitidos
- **Input Validation** - Validación exhaustiva de datos de entrada
- **SQL Injection Protection** - JPA/Hibernate previene inyección SQL
- **Security Headers** - Headers de seguridad configurados

### Recomendaciones Adicionales
- Configurar HTTPS en producción
- Implementar rate limiting
- Monitoreo de seguridad con logs
- Rotación regular de JWT secrets

## Testing

### Ejecutar Tests
```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=ClienteServiceImplTest

# Con cobertura
mvn test jacoco:report
```

### Tipos de Test
- **Unitarios** - Servicios y componentes individuales
- **Integración** - Controladores y repositorios
- **End-to-End** - Flujos completos de casos de uso

## Logging

### Configuración
- **Desarrollo**: Consola + archivo
- **Producción**: Solo archivo con rotación
- **Niveles**: ERROR, WARN, INFO, DEBUG, TRACE

### Archivos de Log
- `logs/applegym.log` - Log principal
- Rotación diaria con máximo 30 días
- Máximo 50MB por archivo

## Contribución

### Estándares de Código
- Seguir convenciones de Java
- Documentar métodos públicos con Javadoc
- Mantener cobertura de tests > 80%
- Validar con SonarQube antes de commit

### Proceso de Desarrollo
1. Crear branch feature desde develop
2. Implementar funcionalidad con tests
3. Ejecutar tests y validaciones
4. Pull request con revisión de código
5. Merge a develop tras aprobación

## Licencia

Este proyecto es desarrollado para fines educativos como parte del curso de Programación Orientada a Objetos.

## Contacto

- **Equipo AppleGym**
- **Institución**: [Tu Institución]
- **Profesor**: [Nombre del Profesor]
- **Período**: 2024

---

**Nota**: Este proyecto implementa todas las mejores prácticas de desarrollo Java moderno y está diseñado para ser un ejemplo completo de una aplicación empresarial robusta y escalable.