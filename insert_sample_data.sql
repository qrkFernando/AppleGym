-- Script para insertar datos de muestra en AppleGym
-- Ejecutar este script en tu base de datos MySQL 'applegym'

USE applegym;

-- Insertar categorías
INSERT INTO categoria (nombre_categoria, descripcion, activo, fecha_creacion) VALUES
('Suplementos', 'Productos nutricionales y suplementos deportivos', 1, NOW()),
('Equipamiento', 'Equipos y accesorios para entrenamiento', 1, NOW()),
('Entrenamiento', 'Servicios de entrenamiento personal y grupal', 1, NOW()),
('Clases Grupales', 'Clases y actividades grupales', 1, NOW()),
('Nutricion', 'Servicios de asesoramiento nutricional', 1, NOW());

-- Insertar productos
INSERT INTO producto (nombre, descripcion, precio, stock, activo, id_categoria, fecha_creacion, imagen_url) VALUES
('Proteina Whey Premium', 'Proteina de alta calidad para desarrollo muscular optimo. Contiene aminoácidos esenciales.', 45.99, 100, 1, 1, NOW(), NULL),
('Mancuernas Ajustables', 'Set completo de mancuernas profesionales 5-50kg. Ideales para entrenamiento en casa.', 299.99, 25, 1, 2, NOW(), NULL),
('Banca Multifuncional', 'Banca profesional para entrenamiento completo del cuerpo. Ajustable a diferentes ángulos.', 459.99, 15, 1, 2, NOW(), NULL),
('Creatina Monohidrato', 'Creatina pura para mayor fuerza y resistencia en entrenamientos intensos.', 29.99, 200, 1, 1, NOW(), NULL),
('Pre-Entreno Energético', 'Fórmula avanzada para maximizar el rendimiento durante el entrenamiento.', 35.50, 80, 1, 1, NOW(), NULL),
('Barras Paralelas', 'Barras paralelas para ejercicios de calistenia y entrenamiento funcional.', 189.99, 12, 1, 2, NOW(), NULL);

-- Insertar servicios
INSERT INTO servicio (nombre, descripcion, precio, duracion, activo, id_categoria, fecha_creacion, capacidad_maxima) VALUES
('Entrenamiento Personal', 'Sesion personalizada de 60 minutos con entrenador certificado. Plan adaptado a tus objetivos.', 65.00, 1, 1, 3, NOW(), 1),
('Clase de Yoga', 'Clase grupal de yoga relajante para todos los niveles. Mejora flexibilidad y bienestar.', 25.00, 1, 1, 4, NOW(), 15),
('Asesoria Nutricional', 'Consulta personalizada con nutricionista especializado. Incluye plan alimentario.', 55.00, 1, 1, 5, NOW(), 1),
('Clase de CrossFit', 'Entrenamiento funcional de alta intensidad y resistencia. Ideal para quemar grasa.', 35.00, 1, 1, 4, NOW(), 12),
('Entrenamiento Grupal', 'Sesiones de entrenamiento en grupos pequeños con instructor personalizado.', 40.00, 1, 1, 4, NOW(), 8),
('Consulta Deportiva', 'Evaluación completa de condición física y plan de entrenamiento personalizado.', 80.00, 1, 1, 3, NOW(), 1);

-- Verificar datos insertados
SELECT 'Categorías insertadas:' as tabla, COUNT(*) as cantidad FROM categoria WHERE activo = 1
UNION ALL
SELECT 'Productos insertados:', COUNT(*) FROM producto WHERE activo = 1  
UNION ALL
SELECT 'Servicios insertados:', COUNT(*) FROM servicio WHERE activo = 1;

-- Consulta para verificar los datos con categorías
SELECT 
    p.nombre as producto_nombre,
    p.precio,
    p.stock,
    c.nombre_categoria as categoria
FROM producto p 
LEFT JOIN categoria c ON p.id_categoria = c.id_categoria
WHERE p.activo = 1;

SELECT 
    s.nombre as servicio_nombre,
    s.precio,
    s.duracion as duracion_dias,
    c.nombre_categoria as categoria
FROM servicio s 
LEFT JOIN categoria c ON s.id_categoria = c.id_categoria  
WHERE s.activo = 1;