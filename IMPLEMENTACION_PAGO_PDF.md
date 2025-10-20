# Sistema de Pago y Comprobantes - AppleGym

## Nuevas Funcionalidades Implementadas

### 1. Carrito Visible Sin Login ✅
- El icono del carrito ahora es visible siempre, estés o no logueado
- Puedes agregar productos al carrito sin estar autenticado
- El carrito se mantiene en localStorage

### 2. Validación de Login Obligatorio para Pago ✅
- Al intentar proceder al pago, **se requiere obligatoriamente estar logueado**
- Si no estás autenticado, el sistema te redirige al login
- Mensaje claro indicando que debes iniciar sesión

### 3. Proceso de Pago Completo ✅
- Modal de selección de método de pago:
  - Tarjeta de Crédito
  - Tarjeta de Débito
  - Efectivo
  - Transferencia Bancaria
- Procesamiento de la venta en el backend
- Actualización de stock automática
- Generación de comprobante

### 4. Generación Automática de PDF ✅
- Al confirmar la compra, se genera automáticamente un PDF
- El PDF se descarga automáticamente al navegador
- Incluye:
  - Datos del cliente
  - Detalle de productos/servicios comprados
  - Información de pago
  - Número de comprobante único
  - Diseño profesional con colores de la marca

### 5. Modal de Confirmación ✅
- Muestra información de la compra realizada
- Permite descargar nuevamente el comprobante si es necesario
- Opción para continuar navegando

## Instalación y Configuración

### Prerequisitos
- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

### Pasos de Instalación

#### 1. Actualizar Base de Datos

Si ya tienes la base de datos creada, ejecuta el script de actualización:

```bash
mysql -u root -p applegym < docs/update-database.sql
```

Si es una instalación nueva:

```bash
mysql -u root -p < docs/database-schema.sql
mysql -u root -p applegym < docs/sample-data.sql
```

#### 2. Instalar Dependencias Maven

```bash
mvn clean install
```

Esto descargará automáticamente la librería iText7 para generación de PDFs.

#### 3. Configurar application.properties

Asegúrate de que tu archivo `src/main/resources/application.properties` tenga:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/applegym
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
```

#### 4. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

O desde tu IDE, ejecuta `AppleGymApplication.java`

## Uso del Sistema

### Para Clientes

1. **Navegar sin Login**
   - Abre http://localhost:8080
   - Explora productos y servicios
   - Agrega items al carrito (visible siempre)

2. **Proceder al Pago**
   - Haz clic en el carrito
   - Clic en "Proceder al Pago"
   - Si no estás logueado, serás redirigido al login
   - Después de login, continúa con el pago

3. **Realizar el Pago**
   - Selecciona método de pago
   - Confirma la compra
   - El PDF se descarga automáticamente
   - Guarda tu comprobante

### Para Desarrolladores

#### Endpoints del API

**Procesar Venta**
```
POST /api/ventas/procesar
Headers: Authorization: Bearer {token}
Body: {
  "metodoPago": "TARJETA_CREDITO"
}
```

**Obtener Comprobante (JSON)**
```
GET /api/ventas/comprobante/{ventaId}
Headers: Authorization: Bearer {token}
```

**Descargar Comprobante (PDF)**
```
GET /api/ventas/comprobante/{ventaId}/pdf
Headers: Authorization: Bearer {token}
Response: application/pdf
```

## Problemas Solucionados

### 1. Carrito No Visible Sin Login ❌ → ✅
**Problema:** El carrito solo aparecía cuando el usuario estaba logueado.

**Solución:** 
- Movido el icono del carrito fuera del contenedor `nav-user`
- Ahora es visible independientemente del estado de autenticación
- Actualizado en `index.html` y `catalogo.html`

### 2. No Validación de Login en Checkout ❌ → ✅
**Problema:** El checkout no validaba adecuadamente si el usuario estaba autenticado.

**Solución:**
- Validación estricta en `proceedToCheckout()` en `cart.js`
- Si no hay `currentUser`, muestra error y redirige al login
- No permite continuar sin autenticación

### 3. Falta Generación de PDF ❌ → ✅
**Problema:** No existía funcionalidad para generar comprobantes en PDF.

**Solución:**
- Agregada dependencia `iText7` en `pom.xml`
- Creado `PdfService` y `PdfServiceImpl`
- Implementado endpoint `/api/ventas/comprobante/{id}/pdf`
- PDF con diseño profesional y datos completos

### 4. Descarga Automática de PDF ❌ → ✅
**Problema:** No se descargaba automáticamente el comprobante.

**Solución:**
- Función `descargarComprobantePDF()` en `cart.js`
- Se ejecuta automáticamente después del pago exitoso
- Usa blob download para guardar el archivo
- Modal de confirmación con opción de re-descarga

### 5. Posibles Problemas en Tablas MySQL ❌ → ✅
**Problema:** Tablas podrían tener campos faltantes o estados incorrectos.

**Solución:**
- Actualizado `database-schema.sql` con:
  - Estado `PROCESANDO` en tabla `venta`
  - Estado `PROCESADO` en tabla `carrito`
  - Estado `COMPLETADO` en tabla `pago`
  - Campos adicionales en `detalle_venta` (`id_producto_servicio`, `tipo`, `nombre`)
  - Claves foráneas con `ON DELETE CASCADE` para integridad referencial
- Creado script `update-database.sql` para actualizar BDs existentes

## Estructura de Archivos Modificados/Creados

```
AppleGym/
├── pom.xml                                    [MODIFICADO] - Agregada dependencia iText7
├── docs/
│   ├── database-schema.sql                   [MODIFICADO] - Actualizados ENUMs y constraints
│   └── update-database.sql                   [NUEVO] - Script de actualización
├── src/main/java/com/applegym/
│   ├── controller/
│   │   └── VentaController.java              [MODIFICADO] - Agregado endpoint PDF
│   ├── entity/
│   │   ├── Venta.java                        [EXISTENTE] - Sin cambios
│   │   ├── Pago.java                         [EXISTENTE] - Sin cambios
│   │   ├── Comprobante.java                  [EXISTENTE] - Sin cambios
│   │   └── DetalleVenta.java                 [EXISTENTE] - Sin cambios
│   ├── repository/
│   │   ├── ComprobanteRepository.java        [NUEVO] - Repositorio de comprobantes
│   │   └── PagoRepository.java               [NUEVO] - Repositorio de pagos
│   ├── service/
│   │   ├── PdfService.java                   [NUEVO] - Interfaz servicio PDF
│   │   └── impl/
│   │       ├── PdfServiceImpl.java           [NUEVO] - Implementación generación PDF
│   │       └── VentaServiceImpl.java         [MODIFICADO] - Mejorada lógica de venta
│   └── ...
└── src/main/resources/static/
    ├── index.html                             [MODIFICADO] - Carrito siempre visible
    ├── catalogo.html                          [MODIFICADO] - Carrito siempre visible
    └── js/
        └── cart.js                            [MODIFICADO] - Checkout y descarga PDF
```

## Testing

### Prueba Manual

1. **Sin Login:**
   ```
   - Abrir navegador en http://localhost:8080
   - Agregar productos al carrito
   - Verificar que el icono del carrito es visible
   - Intentar proceder al pago
   - Verificar redirección al login
   ```

2. **Con Login:**
   ```
   - Iniciar sesión
   - Agregar productos
   - Proceder al pago
   - Seleccionar método de pago
   - Confirmar compra
   - Verificar descarga automática del PDF
   - Verificar contenido del PDF
   ```

### Datos de Prueba

Usuario de prueba (si ejecutaste `sample-data.sql`):
```
Email: juan.perez@email.com
Password: password123
```

## Notas Importantes

1. **Seguridad:** El sistema valida que el cliente solo pueda descargar sus propios comprobantes
2. **Transaccionalidad:** Todo el proceso de venta es transaccional, si falla algo, se hace rollback
3. **Stock:** El sistema actualiza el stock automáticamente (implementar según necesidad)
4. **PDFs:** Los PDFs se generan en memoria y se envían al navegador, no se guardan en disco

## Soporte

Para problemas o preguntas:
- Revisar logs en `logs/application.log`
- Verificar consola del navegador (F12)
- Comprobar estado de la base de datos

## Autores

Grupo 10 - AppleGym Team
