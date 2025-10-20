# 🔍 DIAGNÓSTICO Y SOLUCIÓN - Error de Autenticación al Confirmar Pago

## ❌ Problema Reportado

Al hacer clic en "Confirmar Pago" aparece:
```
Error de autenticación. Por favor inicia sesión nuevamente.
```

## 🔎 Diagnóstico

El problema puede ser causado por:

1. **Token no se guarda correctamente** en localStorage
2. **Token expira** antes de confirmar el pago
3. **currentUser se pierde** al recargar la página
4. **Formato incorrecto** del objeto usuario

## ✅ Soluciones Implementadas

### Cambio 1: Debug mejorado en `utils.js`
- Ahora muestra en consola si el token está disponible
- Muestra los primeros caracteres del token
- Registra errores si los hay

### Cambio 2: Sincronización del carrito en `cart.js`
- Antes de procesar el pago, sincroniza el carrito local con el backend
- Agrega cada item del carrito al servidor
- Muestra logs detallados del proceso

### Cambio 3: Solo 2 métodos de pago
- Tarjeta de Crédito
- Tarjeta de Débito
- Backend actualizado para validar solo estos dos

## 🧪 PASOS PARA PROBAR Y DIAGNOSTICAR

### Paso 1: Abrir Consola del Navegador

1. Abre tu navegador (Chrome, Firefox, Edge)
2. Presiona `F12` para abrir DevTools
3. Ve a la pestaña **Console**
4. Deja la consola abierta durante todo el proceso

### Paso 2: Hacer Login

1. Refresca la página completamente (`Ctrl + F5`)
2. Haz clic en "Iniciar Sesión"
3. Ingresa credenciales:
   ```
   Email: juan.perez@email.com
   Password: password123
   ```
4. **Observa la consola**, debe mostrar:
   ```
   Usuario cargado desde localStorage: {email: "...", token: "..."}
   Token disponible: Sí - eyJhbGciOiJIUzI1NiI...
   ```

### Paso 3: Verificar localStorage

En la consola del navegador, ejecuta:
```javascript
// Ver el usuario guardado
console.log('currentUser:', currentUser);

// Ver localStorage
console.log('localStorage:', localStorage.getItem('userData'));

// Verificar token
const userData = JSON.parse(localStorage.getItem('userData'));
console.log('Token:', userData?.token);
```

**Resultado esperado:**
- Debe mostrar un objeto con `token`
- El token debe ser una cadena larga (tipo JWT)
- No debe ser `undefined` o `null`

### Paso 4: Agregar Productos al Carrito

1. Navega al catálogo
2. Agrega 2-3 productos
3. Verifica que el contador del carrito se actualice

### Paso 5: Proceder al Pago

1. Abre el carrito
2. Clic en "Proceder al Pago"
3. **Observa la consola**:
   ```
   Procesando pago con usuario: {email: "...", token: "..."}
   Token disponible: Sí
   Paso 1: Sincronizando carrito...
   Sincronizando 3 items del carrito...
   Item agregado al carrito backend: Producto X
   ...
   Paso 2: Procesando venta...
   ```

### Paso 6: Confirmar Pago

1. Selecciona "Tarjeta de Crédito" o "Tarjeta de Débito"
2. Clic en "Confirmar Pago"
3. **Observa la consola** para ver si hay errores

## 🐛 Posibles Problemas y Soluciones

### Problema A: "currentUser is undefined"

**Causa:** El usuario no se cargó correctamente

**Solución:**
```javascript
// En la consola del navegador:
const userData = {
    email: "juan.perez@email.com",
    token: "TU_TOKEN_AQUI", // Obtén el token del login
    nombre: "Juan Pérez"
};
localStorage.setItem('userData', JSON.stringify(userData));
currentUser = userData;
console.log('Usuario configurado manualmente');
```

### Problema B: "Token is null o undefined"

**Causa:** El backend no está devolviendo el token en el login

**Verificar en el backend:**

1. Revisa `AuthController.java` línea del login
2. Debe retornar algo como:
```java
return ResponseEntity.ok(Map.of(
    "success", true,
    "cliente", clienteDTO,  // Debe incluir el token
    "token", token
));
```

**Solución temporal en frontend:**
Modifica `auth.js` para asegurarse de que guarda el token:

```javascript
if (response.ok && result.success) {
    // Asegurarse de que el token está en el objeto cliente
    if (result.token && !result.cliente.token) {
        result.cliente.token = result.token;
    }
    
    currentUser = result.cliente;
    localStorage.setItem('userData', JSON.stringify(result.cliente));
    console.log('Usuario guardado con token:', currentUser.token ? 'Sí' : 'No');
    
    // ... resto del código
}
```

### Problema C: Token expira muy rápido

**Causa:** JWT configurado con expiración corta

**Solución:** Aumentar tiempo de expiración en `JwtTokenProvider.java`

```java
private static final long JWT_EXPIRATION = 86400000L; // 24 horas
```

### Problema D: CORS o problemas de red

**Síntoma:** Error 401 o 403

**Solución:** Verificar que el servidor está corriendo y accesible

```bash
# Verificar servidor
curl http://localhost:8080/api/ventas/metodos-pago

# Debería retornar los métodos de pago
```

## 🔧 Script de Verificación Rápida

Copia y pega esto en la consola del navegador:

```javascript
// SCRIPT DE DIAGNÓSTICO
console.log('=== DIAGNÓSTICO DE AUTENTICACIÓN ===');

// 1. Verificar currentUser
console.log('1. currentUser existe:', typeof currentUser !== 'undefined');
console.log('   Valor:', currentUser);

// 2. Verificar localStorage
const stored = localStorage.getItem('userData');
console.log('2. localStorage tiene datos:', !!stored);
if (stored) {
    const parsed = JSON.parse(stored);
    console.log('   Email:', parsed.email);
    console.log('   Token existe:', !!parsed.token);
    console.log('   Token (primeros 30 chars):', parsed.token?.substring(0, 30));
}

// 3. Verificar carrito
console.log('3. Carrito tiene items:', cart?.length || 0);

// 4. Test de autenticación
if (currentUser?.token) {
    fetch('http://localhost:8080/api/ventas/metodos-pago', {
        headers: {
            'Authorization': `Bearer ${currentUser.token}`
        }
    })
    .then(r => r.json())
    .then(data => console.log('4. Test API exitoso:', data))
    .catch(err => console.error('4. Test API falló:', err));
} else {
    console.log('4. No se puede hacer test API - no hay token');
}

console.log('=== FIN DIAGNÓSTICO ===');
```

## 📞 Si Sigue Fallando

Si después de seguir todos estos pasos sigue fallando:

1. **Copia el output completo de la consola** cuando haces el proceso
2. **Toma screenshot** del error
3. **Revisa los logs del servidor** en `logs/applegym.log`

## ✅ Verificación Final

Cuando todo funciona correctamente, deberías ver en la consola:

```
Usuario cargado desde localStorage: {email: "...", token: "eyJ..."}
Token disponible: Sí - eyJhbGciOiJIUzI1NiI...
Procesando pago con usuario: {email: "..."}
Token disponible: Sí
Paso 1: Sincronizando carrito...
Sincronizando 2 items del carrito...
Item agregado al carrito backend: Producto 1
Item agregado al carrito backend: Producto 2
Carrito sincronizado con backend
Paso 2: Procesando venta...
Respuesta del servidor: {message: "...", venta: {...}}
¡Compra realizada exitosamente!
```

Y el PDF se descargará automáticamente.
