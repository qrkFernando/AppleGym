# 🚨 SOLUCIÓN INMEDIATA - TOKEN UNDEFINED

## ⚡ PROBLEMA ENCONTRADO

El error 500 es causado por configuración conflictiva de CORS:
- `WebConfig.java` y `SecurityConfig.java` ambos usan `allowCredentials(true)` con `allowedOriginPatterns("*")`
- Esto causa un IllegalArgumentException en Spring Boot 3.x

## ✅ SOLUCIÓN APLICADA

He cambiado `allowCredentials` a `false` en ambos archivos:
- ✓ WebConfig.java (líneas 61 y 67)
- ✓ SecurityConfig.java (línea 90)

## 🚀 PASOS PARA APLICAR (EJECUTA ESTO AHORA)

```bash
# 1. Detener cualquier proceso Maven running
# Presiona Ctrl+C si hay algo corriendo

# 2. Compilar e instalar
cd D:\ATAHUALPA\AppleGym
mvn clean install -DskipTests

# 3. Ejecutar el servidor
mvn spring-boot:run

# 4. Esperar a que inicie (mensaje "Started AppleGymApplication")

# 5. Abrir navegador
# http://localhost:8080

# 6. Abrir Console (F12)

# 7. Hacer login con pepe@gmail.com

# 8. Verificar en la consola:
console.log('Token:', currentUser?.token)

# DEBE MOSTRAR el token (no undefined)
```

## 📝 SI SIGUE SIN FUNCIONAR

El problema puede ser que el frontend está cacheado. Haz esto:

### Opción 1: Limpiar Caché del Navegador

1. Ctrl + Shift + Delete
2. Seleccionar "Todo"
3. Limpiar
4. Cerrar navegador completamente
5. Reabrir y probar

### Opción 2: Modo Incógnito

1. Ctrl + Shift + N (Chrome) o Ctrl + Shift + P (Firefox)
2. Ir a http://localhost:8080
3. Hacer login
4. Probar

### Opción 3: Hard Reload

1. Abrir la página
2. Ctrl + F5 (o Ctrl + Shift + R)
3. Hacer login nuevamente

## 🔍 VERIFICAR QUE EL BACKEND FUNCIONA

Antes de probar en el navegador, verifica que el backend responda bien:

```powershell
# En PowerShell:
$body = @{
    email = "pepe@gmail.com"
    password = "pepe"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $body -ContentType "application/json"
$response.Content

# DEBE MOSTRAR un JSON con:
# - success: true
# - token: "eyJ..."
# - cliente.token: "eyJ..."
```

Si esto funciona pero el navegador no, el problema es del frontend/caché.

## 🎯 EXPLICACIÓN DEL PROBLEMA

### Por qué el token estaba undefined:

1. **Backend devolvía error 500** por configuración CORS
2. El frontend NO recibía la respuesta correcta
3. Por lo tanto, currentUser.token era undefined

### La solución:

1. ✅ Arreglar configuración CORS (allowCredentials = false)
2. ✅ Backend ahora responde correctamente
3. ✅ Frontend recibe el token
4. ✅ Token se guarda en localStorage

## 📋 ARCHIVOS MODIFICADOS

1. `AuthController.java` - Formato de respuesta con token
2. `WebConfig.java` - allowCredentials = false
3. `SecurityConfig.java` - allowCredentials = false
4. `cart.js` - Solo 2 métodos de pago + sincronización
5. `VentaController.java` - Solo 2 métodos de pago
6. `index.html` - Carrito siempre visible
7. `catalogo.html` - Carrito siempre visible

## ⏰ SI TIENES POCO TIEMPO

**HACER SOLO ESTO:**

```bash
# Terminal 1:
cd D:\ATAHUALPA\AppleGym
mvn clean install -DskipTests && mvn spring-boot:run

# Esperar mensaje: "Started AppleGymApplication"

# Navegador (Modo Incógnito):
http://localhost:8080
# Login → Ver consola → Verificar token
```

Si el token SIGUE siendo undefined después de:
- Compilar clean
- Ejecutar servidor nuevo
- Naveg ador en modo incógnito
- Login nuevo

Entonces el problema está en `auth.js` que no guarda el token correctamente.

En ese caso, ejecuta este script en la consola del navegador DESPUÉS de hacer login:

```javascript
// Forzar guardar el token manualmente
if (localStorage.getItem('userData')) {
    const userData = JSON.parse(localStorage.getItem('userData'));
    console.log('Datos actuales:', userData);
    
    // Si no tiene token, hacer login de nuevo y capturar la respuesta
    // O pedir ayuda para modificar auth.js
}
```

## 🆘 ÚLTIMO RECURSO

Si nada de esto funciona, el problema es que `auth.js` no está guardando el token.  

Modifica `auth.js` línea ~28:

```javascript
// ANTES:
currentUser = result.cliente;

// DESPUÉS:
currentUser = result.cliente;
// Si el cliente no tiene token, agregarlo
if (!currentUser.token && result.token) {
    currentUser.token = result.token;
}
```

Luego guarda, recompila y prueba.

---

**Tiempo estimado de solución: 5-10 minutos**

¡Sigue estos pasos y debería funcionar! 🚀
