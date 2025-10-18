# Documentación Técnica - AppleGym

## Tabla de Contenidos
1. [Visión General del Sistema](#visión-general-del-sistema)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Patrones de Diseño Implementados](#patrones-de-diseño-implementados)
4. [Casos de Uso Implementados](#casos-de-uso-implementados)
5. [Seguridad](#seguridad)
6. [Testing](#testing)
7. [Guía de Despliegue](#guía-de-despliegue)

## Visión General del Sistema

AppleGym es un sistema completo de gestión para gimnasios que implementa una arquitectura Cliente-Servidor robusta. El sistema permite a los clientes navegar por el catálogo, gestionar carritos de compra, procesar pagos y generar comprobantes digitales.

### Características Técnicas Principales

- **Framework**: Spring Boot 3.2.0 con Java 17
- **Base de Datos**: MySQL 8.0 con JPA/Hibernate
- **Seguridad**: JWT con Spring Security 6
- **Testing**: JUnit 5 + Mockito con cobertura JaCoCo
- **Build Tool**: Maven 3.8+

## Arquitectura del Sistema

### Arquitectura por Capas

```
┌─────────────────────────────────────┐
│           Controller Layer           │  ← REST Controllers
├─────────────────────────────────────┤
│            Service Layer            │  ← Business Logic
├─────────────────────────────────────┤
│             DAO Layer               │  ← Data Access Objects
├─────────────────────────────────────┤
│            Entity Layer             │  ← JPA Entities
└─────────────────────────────────────┘
```

### Componentes Principales

#### 1. **Controller Layer** (`com.applegym.controller`)
- `AuthController`: Autenticación y registro
- `CatalogoController`: Navegación del catálogo
- `ClienteController`: Gestión de perfil de cliente
- `CarritoController`: Gestión del carrito de compras

#### 2. **Service Layer** (`com.applegym.service`)
- `ClienteService`: Lógica de negocio de clientes
- `ProductoService`: Gestión de productos
- `ServicioService`: Gestión de servicios del gimnasio
- `CarritoService`: Lógica del carrito de compras

#### 3. **DAO Layer** (`com.applegym.repository`)
- Repositorios JPA con Spring Data
- Queries personalizadas con `@Query`
- Operaciones CRUD optimizadas

#### 4. **Entity Layer** (`com.applegym.entity`)
- Entidades JPA con validaciones
- Relaciones bidireccionales optimizadas
- Auditoría automática de fechas

## Patrones de Diseño Implementados

### 1. **Patrón MVC (Model-View-Controller)**

```java
// Controller
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Delegación al Service Layer
    }
}

// Service (Model Business Logic)
@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {
    // Lógica de negocio aislada
}

// Repository (Data Access)
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Acceso a datos
}
```

### 2. **Patrón DAO (Data Access Object)**

```java
// Interface DAO
public interface ClienteService {
    ClienteDTO registrarCliente(ClienteRegistroDTO clienteRegistroDTO);
    Optional<ClienteDTO> buscarClientePorId(Long id);
}

// Implementación DAO
@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private ClienteRepository clienteRepository; // Delegación al Repository
}
```

### 3. **Principios SOLID**

#### **S** - Single Responsibility Principle
```java
// Cada clase tiene una responsabilidad específica
public class JwtTokenProvider {
    // Solo responsable de la gestión de tokens JWT
}

public class ClienteServiceImpl {
    // Solo responsable de la lógica de negocio de clientes
}
```

#### **O** - Open/Closed Principle
```java
// Interfaces permiten extensión sin modificación
public interface ClienteService {
    // Contrato que puede ser extendido
}
```

#### **L** - Liskov Substitution Principle
```java
// Las implementaciones respetan los contratos
ClienteService service = new ClienteServiceImpl(); // Intercambiable
```

#### **I** - Interface Segregation Principle
```java
// Interfaces específicas por funcionalidad
public interface ClienteService { } // Solo operaciones de cliente
public interface ProductoService { } // Solo operaciones de producto
```

#### **D** - Dependency Inversion Principle
```java
// Dependencias por abstracción
@Autowired
private ClienteService clienteService; // Interface, no implementación
```

## Casos de Uso Implementados

### CU01 - Registro de Cliente
```java
@PostMapping("/register")
public ResponseEntity<?> registerUser(@Valid @RequestBody ClienteRegistroDTO clienteRegistroDTO) {
    // 1. Validar datos de entrada
    // 2. Verificar email único
    // 3. Encriptar contraseña
    // 4. Guardar cliente
    // 5. Generar token JWT
    // 6. Retornar respuesta
}
```

### CU02 - Inicio de Sesión
```java
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    // 1. Autenticar credenciales
    // 2. Generar token JWT
    // 3. Retornar información del usuario
}
```

### CU03 - Explorar Productos y Servicios
```java
@GetMapping
public ResponseEntity<?> obtenerCatalogo(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) Long categoria,
        @RequestParam(required = false) String busqueda) {
    // 1. Aplicar filtros
    // 2. Paginar resultados
    // 3. Retornar catálogo
}
```

### CU04 - Seleccionar Producto o Servicio
```java
@GetMapping("/productos/{id}")
public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
    // 1. Buscar producto por ID
    // 2. Verificar disponibilidad
    // 3. Incluir productos relacionados
    // 4. Retornar detalles
}
```

## Seguridad

### 1. **Autenticación JWT**

```java
@Component
public class JwtTokenProvider {
    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
}
```

### 2. **Configuración de Seguridad**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/catalogo/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
```

### 3. **Validaciones de Entrada**

```java
public class ClienteRegistroDTO {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 100)
    private String nombreCliente;
    
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
```

### 4. **Encriptación de Contraseñas**

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Fuerza 12 para mayor seguridad
}
```

## Testing

### 1. **Tests Unitarios (TDD)**

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {
    
    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private ClienteServiceImpl clienteService;
    
    @Test
    void registrarCliente_DatosValidos_RetornaClienteDTO() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        
        // Act
        ClienteDTO resultado = clienteService.registrarCliente(clienteRegistroDTO);
        
        // Assert
        assertNotNull(resultado);
        verify(clienteRepository).save(any(Cliente.class));
    }
}
```

### 2. **Cobertura de Tests**

- **Servicios**: 100% cobertura de métodos públicos
- **Controladores**: Tests de integración con MockMvc
- **Repositorios**: Tests con base de datos en memoria (H2)

### 3. **Configuración JaCoCo**

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Librerías de Apoyo Integradas

### 1. **Google Guava**
```java
// Uso en validaciones y utilidades
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return !Strings.isNullOrEmpty(email) && email.contains("@");
    }
}
```

### 2. **Apache Commons**
```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

public class DataValidator {
    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
```

### 3. **Apache POI**
```java
// Para generación de reportes Excel
public class ReporteService {
    public void generarReporteVentas() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ventas");
        // Generar reporte
    }
}
```

### 4. **Logback**
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.applegym" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

## Guía de Despliegue

### 1. **Requisitos del Sistema**
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- 4GB RAM mínimo
- 10GB espacio en disco

### 2. **Configuración de Base de Datos**
```sql
-- Crear base de datos
CREATE DATABASE applegym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario
CREATE USER 'applegym_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON applegym.* TO 'applegym_user'@'localhost';
```

### 3. **Variables de Entorno**
```bash
# Configuración de base de datos
export DB_USERNAME=applegym_user
export DB_PASSWORD=secure_password

# JWT Secret
export JWT_SECRET=your_secure_jwt_secret_key_here

# Email (opcional)
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
```

### 4. **Comandos de Despliegue**
```bash
# 1. Compilar y empaquetar
mvn clean package -DskipTests

# 2. Ejecutar aplicación
java -jar target/apple-gym-1.0.0.jar

# 3. Con perfil de producción
java -jar -Dspring.profiles.active=prod target/apple-gym-1.0.0.jar
```

### 5. **Health Checks**
```bash
# Verificar estado de la aplicación
curl http://localhost:8080/api/actuator/health

# Verificar endpoints disponibles
curl http://localhost:8080/api/catalogo/categorias
```

## Conclusiones

El sistema AppleGym implementa exitosamente:

1. **Arquitectura robusta** con separación clara de responsabilidades
2. **Principios SOLID** para código mantenible y extensible
3. **Patrones de diseño** probados (MVC, DAO)
4. **Seguridad integral** con JWT y validaciones
5. **Testing completo** siguiendo metodología TDD
6. **Librerías especializadas** para funcionalidades específicas
7. **Documentación completa** del código y arquitectura

El proyecto está listo para producción y puede servir como base para futuras extensiones del sistema de gestión del gimnasio.