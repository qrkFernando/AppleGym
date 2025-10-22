-- Script SQL para Agregar Imágenes a Productos y Servicios
-- AppleGym - Configuración de Imágenes

-- ============================================
-- PRODUCTOS - Actualizar con Imágenes
-- ============================================

-- Ejemplo 1: Actualizar producto existente con imagen
-- Asegúrate de que la imagen exista en: src/main/resources/static/images/
UPDATE producto 
SET imagen_url = 'Whey-Pro.jpg'
WHERE nombre LIKE '%Proteína%' OR nombre LIKE '%Whey%';

-- Ejemplo 2: Agregar múltiples productos con imágenes
-- Primero, coloca las imágenes en la carpeta images/

-- Suplementos
UPDATE producto SET imagen_url = 'creatina-monohidrato.jpg' WHERE nombre LIKE '%Creatina%';
UPDATE producto SET imagen_url = 'bcaa-recovery.jpg' WHERE nombre LIKE '%BCAA%';
UPDATE producto SET imagen_url = 'pre-workout.jpg' WHERE nombre LIKE '%Pre-Workout%';

-- Accesorios
UPDATE producto SET imagen_url = 'guantes-gym.jpg' WHERE nombre LIKE '%Guantes%';
UPDATE producto SET imagen_url = 'cinturon-pesas.jpg' WHERE nombre LIKE '%Cinturón%';
UPDATE producto SET imagen_url = 'shaker-bottle.jpg' WHERE nombre LIKE '%Shaker%';
UPDATE producto SET imagen_url = 'toalla-microfibra.jpg' WHERE nombre LIKE '%Toalla%';

-- Ropa Deportiva
UPDATE producto SET imagen_url = 'playera-gym.jpg' WHERE nombre LIKE '%Playera%' OR nombre LIKE '%Camiseta%';
UPDATE producto SET imagen_url = 'shorts-entrenamiento.jpg' WHERE nombre LIKE '%Shorts%';
UPDATE producto SET imagen_url = 'leggings-deportivos.jpg' WHERE nombre LIKE '%Leggings%';
UPDATE producto SET imagen_url = 'tenis-training.jpg' WHERE nombre LIKE '%Tenis%' OR nombre LIKE '%Zapatos%';

-- Equipamiento
UPDATE producto SET imagen_url = 'mancuernas-ajustables.jpg' WHERE nombre LIKE '%Mancuernas%';
UPDATE producto SET imagen_url = 'banda-resistencia.jpg' WHERE nombre LIKE '%Banda%';
UPDATE producto SET imagen_url = 'tapete-yoga.jpg' WHERE nombre LIKE '%Tapete%' OR nombre LIKE '%Yoga%';

-- ============================================
-- SERVICIOS - Actualizar con Imágenes
-- ============================================

-- Membresías
UPDATE servicio SET imagen_url = 'membresia-basica.jpg' WHERE nombre LIKE '%Membresía Básica%';
UPDATE servicio SET imagen_url = 'membresia-premium.jpg' WHERE nombre LIKE '%Membresía Premium%';
UPDATE servicio SET imagen_url = 'membresia-vip.jpg' WHERE nombre LIKE '%Membresía VIP%';

-- Clases Grupales
UPDATE servicio SET imagen_url = 'clase-yoga.jpg' WHERE nombre LIKE '%Yoga%';
UPDATE servicio SET imagen_url = 'clase-spinning.jpg' WHERE nombre LIKE '%Spinning%' OR nombre LIKE '%Ciclismo%';
UPDATE servicio SET imagen_url = 'clase-zumba.jpg' WHERE nombre LIKE '%Zumba%';
UPDATE servicio SET imagen_url = 'clase-pilates.jpg' WHERE nombre LIKE '%Pilates%';
UPDATE servicio SET imagen_url = 'clase-crossfit.jpg' WHERE nombre LIKE '%CrossFit%';
UPDATE servicio SET imagen_url = 'clase-boxeo.jpg' WHERE nombre LIKE '%Box%';

-- Entrenamientos Especializados
UPDATE servicio SET imagen_url = 'entrenamiento-personal.jpg' WHERE nombre LIKE '%Personal%';
UPDATE servicio SET imagen_url = 'entrenamiento-funcional.jpg' WHERE nombre LIKE '%Funcional%';
UPDATE servicio SET imagen_url = 'entrenamiento-fuerza.jpg' WHERE nombre LIKE '%Fuerza%' OR nombre LIKE '%Pesas%';

-- Servicios Adicionales
UPDATE servicio SET imagen_url = 'nutricion-deportiva.jpg' WHERE nombre LIKE '%Nutrición%';
UPDATE servicio SET imagen_url = 'evaluacion-fisica.jpg' WHERE nombre LIKE '%Evaluación%';
UPDATE servicio SET imagen_url = 'masaje-deportivo.jpg' WHERE nombre LIKE '%Masaje%';

-- ============================================
-- INSERTAR NUEVOS PRODUCTOS CON IMÁGENES
-- ============================================

-- Ejemplo de inserción con imagen desde el inicio
INSERT INTO producto (nombre, descripcion, precio, stock, imagen_url, id_categoria, activo, fecha_creacion)
VALUES 
('Proteína Whey Gold', 'Proteína premium con aminoácidos', 99.99, 30, 'whey-gold.jpg', 1, true, NOW()),
('Creatina Ultra', 'Creatina monohidrato pura', 45.50, 50, 'creatina-ultra.jpg', 1, true, NOW()),
('Shaker Premium', 'Mezclador de 700ml con compartimentos', 15.99, 100, 'shaker-premium.jpg', 2, true, NOW());

-- ============================================
-- INSERTAR NUEVOS SERVICIOS CON IMÁGENES
-- ============================================

INSERT INTO servicio (nombre, descripcion, precio, duracion, imagen_url, id_categoria, activo, fecha_creacion)
VALUES
('Clase de Yoga Matutina', 'Sesión relajante de yoga al amanecer', 25.00, 60, 'yoga-matutina.jpg', 3, true, NOW()),
('Spinning Extremo', 'Clase de alto rendimiento en bicicleta', 30.00, 45, 'spinning-extremo.jpg', 3, true, NOW());

-- ============================================
-- CONSULTAS DE VERIFICACIÓN
-- ============================================

-- Ver todos los productos con sus imágenes
SELECT id_producto, nombre, imagen_url, stock, precio
FROM producto
WHERE activo = true
ORDER BY id_producto;

-- Ver todos los servicios con sus imágenes
SELECT id_servicio, nombre, imagen_url, duracion, precio
FROM servicio
WHERE activo = true
ORDER BY id_servicio;

-- Contar productos sin imagen
SELECT COUNT(*) as productos_sin_imagen
FROM producto
WHERE imagen_url IS NULL AND activo = true;

-- Contar servicios sin imagen
SELECT COUNT(*) as servicios_sin_imagen
FROM servicio
WHERE imagen_url IS NULL AND activo = true;

-- Listar productos sin imagen
SELECT id_producto, nombre
FROM producto
WHERE imagen_url IS NULL AND activo = true;

-- Listar servicios sin imagen
SELECT id_servicio, nombre
FROM servicio
WHERE imagen_url IS NULL AND activo = true;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================

/*
1. Antes de ejecutar estos scripts, asegúrate de que las imágenes existen en:
   src/main/resources/static/images/

2. Los nombres de archivo deben coincidir EXACTAMENTE con lo que pones en imagen_url

3. Formatos soportados: .jpg, .jpeg, .png, .webp

4. Tamaño recomendado: 800x800 píxeles

5. Si una imagen no existe, se mostrará un icono predeterminado automáticamente

6. Puedes usar URLs completas si las imágenes están en un servidor externo:
   UPDATE producto SET imagen_url = 'https://ejemplo.com/imagen.jpg' WHERE id_producto = 1;

7. Para quitar una imagen:
   UPDATE producto SET imagen_url = NULL WHERE id_producto = 1;
*/
