# GUÍA RÁPIDA DE PRUEBA - Sistema de Pago y PDF

## 🚀 Inicio Rápido

### 1. Actualizar Base de Datos

```bash
# Opción A: Actualizar BD existente (RECOMENDADO)
mysql -u root -p applegym < docs/update-database.sql

# Opción B: Crear BD desde cero (si no existe)
mysql -u root -p < docs/database-schema.sql
mysql -u root -p applegym < docs/sample-data.sql
```

### 2. Compilar el Proyecto

```bash
mvn clean install
```

### 3. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

O desde tu IDE: Ejecuta `AppleGymApplication.java`

---

## 🧪 PRUEBAS PASO A PASO

### Prueba 1: Carrito Visible Sin Login ✅

1. Abre tu navegador en `http://localhost:8080`
2. **Verifica:** El icono del carrito es VISIBLE en la barra de navegación
3. Navega a "Catálogo"
4. Agrega algunos productos al carrito
5. **Verifica:** El contador del carrito se actualiza
6. Haz clic en el icono del carrito
7. **Verifica:** Se muestra el modal con los productos agregados

**Resultado Esperado:** ✅ Carrito funcional sin necesidad de login

---

### Prueba 2: Validación Obligatoria de Login ✅

1. Con el carrito lleno (sin estar logueado)
2. Haz clic en "Proceder al Pago"
3. **Verifica:** Aparece un mensaje de error
4. **Verifica:** Se muestra el formulario de login automáticamente
5. **Verifica:** El mensaje dice "Debes iniciar sesión para proceder con la compra"

**Resultado Esperado:** ✅ No permite checkout sin autenticación

---

### Prueba 3: Proceso de Pago Completo ✅

#### Paso 1: Login
```
Email: juan.perez@email.com
Password: password123
```

#### Paso 2: Agregar Productos
1. Navega al catálogo
2. Agrega 2-3 productos diferentes
3. Verifica el carrito

#### Paso 3: Proceder al Pago
1. Clic en el carrito
2. Clic en "Proceder al Pago"
3. **Verifica:** Aparece modal de selección de pago

#### Paso 4: Seleccionar Método de Pago
1. Selecciona "Tarjeta de Crédito"
2. Clic en "Confirmar Pago"
3. **Verifica:** Aparece indicador de carga

**Resultado Esperado:** ✅ Modal de pago se muestra correctamente

---

### Prueba 4: Descarga Automática de PDF ✅

Después de confirmar el pago:

1. **Verifica:** El navegador descarga automáticamente un archivo PDF
2. **Verifica:** El nombre del archivo es `Comprobante_VT[número].pdf`
3. Abre el PDF descargado
4. **Verifica en el PDF:**
   - Logo y nombre "APPLEGYM"
   - Tus datos de cliente
   - Detalle de productos comprados
   - Método de pago seleccionado
   - Número de comprobante único
   - Total de la compra

**Resultado Esperado:** ✅ PDF descargado con información completa

---

### Prueba 5: Modal de Confirmación ✅

Después de la descarga del PDF:

1. **Verifica:** Aparece modal de confirmación
2. **Verifica:** Muestra:
   - Icono de check verde ✓
   - "¡Compra Realizada con Éxito!"
   - Número de venta
   - Total pagado
   - Estado de la venta
3. Clic en "Descargar Comprobante" (botón secundario)
4. **Verifica:** Se descarga nuevamente el PDF
5. Clic en "Continuar"
6. **Verifica:** Redirige al catálogo

**Resultado Esperado:** ✅ Confirmación visual clara y funcional

---

### Prueba 6: Verificación en Base de Datos ✅

```sql
-- Conectar a MySQL
mysql -u root -p applegym

-- Verificar la venta
SELECT * FROM venta ORDER BY id_venta DESC LIMIT 1;

-- Verificar el detalle
SELECT * FROM detalle_venta WHERE id_venta = [último id];

-- Verificar el pago
SELECT * FROM pago WHERE id_venta = [último id];

-- Verificar el comprobante
SELECT * FROM comprobante WHERE id_venta = [último id];

-- Verificar el carrito
SELECT * FROM carrito WHERE id_cliente = 1 ORDER BY id_carrito DESC LIMIT 1;
```

**Resultado Esperado:**
- ✅ Registro en tabla `venta` con estado "COMPLETADO"
- ✅ Detalles en `detalle_venta` con productos correctos
- ✅ Pago en tabla `pago` con estado "COMPLETADO"
- ✅ Comprobante en tabla `comprobante` con estado "GENERADO"
- ✅ Carrito con estado "PROCESADO"

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: "Token requerido"
**Causa:** No estás autenticado
**Solución:** Inicia sesión primero

### Error: "Venta no encontrada"
**Causa:** ID de venta no existe
**Solución:** Verifica que la venta se creó correctamente

### Error: "No tiene permisos"
**Causa:** Intentas descargar comprobante de otro usuario
**Solución:** Solo puedes descargar tus propios comprobantes

### PDF no se descarga
**Causa:** Bloqueador de pop-ups o problema de permisos
**Solución:** 
1. Permite descargas automáticas en tu navegador
2. Verifica la consola del navegador (F12)
3. Usa el botón de re-descarga en el modal

### Error de compilación
**Causa:** Dependencias faltantes
**Solución:**
```bash
mvn clean install -U
```

---

## 📊 DATOS DE PRUEBA

### Usuarios Predefinidos (si ejecutaste sample-data.sql)

```
Usuario 1:
  Email: juan.perez@email.com
  Password: password123
  
Usuario 2:
  Email: maria.lopez@email.com
  Password: password123
```

### Productos de Prueba

- Membresía Mensual - $50.00
- Membresía Trimestral - $130.00
- Proteína Whey - $45.00
- Entrenamiento Personal - $35.00/sesión

---

## 🎨 CAPTURAS ESPERADAS

### Vista del Carrito (Sin Login)
```
┌─────────────────────────────────────┐
│  AppleGym  Inicio  Catálogo  [🛒 2] │ ← Carrito visible
│  [Iniciar Sesión] [Registrarse]     │
└─────────────────────────────────────┘
```

### Modal de Pago
```
┌──────────────────────────────┐
│  Proceder al Pago        [×] │
│                              │
│  Total a Pagar: $150.00      │
│                              │
│  Método de pago:             │
│  [Tarjeta de Crédito    ▼]   │
│                              │
│  [Cancelar] [Confirmar Pago] │
└──────────────────────────────┘
```

### Modal de Confirmación
```
┌────────────────────────────────┐
│           ✓                [×] │
│  ¡Compra Realizada con Éxito!  │
│                                │
│  Nº Venta: VT1729443111234     │
│  Total: $150.00                │
│  Estado: COMPLETADO            │
│                                │
│  El comprobante se descargó    │
│  automáticamente               │
│                                │
│  [📥 Descargar] [Continuar]    │
└────────────────────────────────┘
```

---

## ✅ CHECKLIST FINAL

Marca cada ítem después de probar:

- [ ] Carrito visible sin login
- [ ] Puedo agregar productos sin login
- [ ] No puedo hacer checkout sin login
- [ ] Mensaje de error me redirige al login
- [ ] Después de login, puedo proceder
- [ ] Modal de pago se muestra
- [ ] Puedo seleccionar método de pago
- [ ] Confirmación procesa la venta
- [ ] PDF se descarga automáticamente
- [ ] PDF contiene información correcta
- [ ] Modal de confirmación aparece
- [ ] Puedo re-descargar el PDF
- [ ] Datos se guardan en BD correctamente
- [ ] Carrito se vacía después del pago
- [ ] Puedo realizar múltiples compras

---

## 📞 CONTACTO Y SOPORTE

Si encuentras algún problema:

1. Revisa los logs en `logs/applegym.log`
2. Verifica la consola del navegador (F12)
3. Comprueba los logs de MySQL
4. Revisa `IMPLEMENTACION_PAGO_PDF.md` para más detalles

---

## 🎯 ¡LISTO!

Si todos los checks están marcados, el sistema está funcionando correctamente.

**¡Felicitaciones! Has implementado exitosamente el sistema de pago con PDF.** 🎉
