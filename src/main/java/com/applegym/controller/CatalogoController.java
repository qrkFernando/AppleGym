package com.applegym.controller;

import com.applegym.dto.ProductoDTO;
import com.applegym.dto.ServicioDTO;
import com.applegym.dto.CategoriaDTO;
import com.applegym.service.ProductoService;
import com.applegym.service.ServicioService;
import com.applegym.service.CategoriaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para el catálogo de productos y servicios.
 * 
 * Implementa los casos de uso CU03 (Explorar productos y servicios) y 
 * CU04 (Seleccionar producto o servicio). Permite la navegación del catálogo
 * tanto para usuarios invitados como registrados.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CatalogoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CatalogoController.class);
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ServicioService servicioService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    /**
     * CU03 - Obtener catálogo completo con productos y servicios.
     * 
     * Endpoint público que retorna el catálogo general con paginación
     * y filtros opcionales.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 20)
     * @param categoria ID de categoría (opcional)
     * @param precioMin Precio mínimo (opcional)
     * @param precioMax Precio máximo (opcional)
     * @param busqueda Término de búsqueda (opcional)
     * @return ResponseEntity con catálogo paginado
     */
    @GetMapping
    public ResponseEntity<?> obtenerCatalogo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.debug("Obteniendo catálogo - page: {}, size: {}, categoria: {}", page, size, categoria);
        
        try {
            // Configurar paginación y ordenamiento
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Obtener productos
            Page<ProductoDTO> productos = productoService.buscarProductosDisponibles(
                busqueda, categoria, precioMin, precioMax, pageable);
            
            // Obtener servicios
            Page<ServicioDTO> servicios = servicioService.buscarServiciosDisponibles(
                busqueda, categoria, precioMin, precioMax, pageable);
            
            // Obtener categorías
            List<CategoriaDTO> categorias = categoriaService.obtenerCategoriasActivas();
            
            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("productos", productos);
            response.put("servicios", servicios);
            response.put("categorias", categorias);
            response.put("filtros", Map.of(
                "categoria", categoria,
                "precioMin", precioMin,
                "precioMax", precioMax,
                "busqueda", busqueda
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo catálogo: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al cargar el catálogo");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * CU04 - Obtener detalles de un producto específico.
     * 
     * @param id ID del producto
     * @return ResponseEntity con detalles del producto
     */
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        logger.debug("Obteniendo detalles del producto ID: {}", id);
        
        try {
            Optional<ProductoDTO> producto = productoService.buscarProductoPorId(id);
            
            if (producto.isPresent()) {
                ProductoDTO productoDTO = producto.get();
                
                // Verificar disponibilidad
                Map<String, Object> response = new HashMap<>();
                response.put("producto", productoDTO);
                response.put("disponible", productoDTO.isDisponible());
                response.put("estadoStock", productoDTO.getEstadoStock());
                
                // Agregar productos relacionados (misma categoría)
                if (productoDTO.getIdCategoria() != null) {
                    List<ProductoDTO> relacionados = productoService.buscarProductosPorCategoria(
                        productoDTO.getIdCategoria(), PageRequest.of(0, 4)).getContent();
                    response.put("productosRelacionados", relacionados);
                }
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Producto no encontrado");
                errorResponse.put("message", "No existe un producto con el ID especificado");
                
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error obteniendo producto {}: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener el producto");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * CU04 - Obtener detalles de un servicio específico.
     * 
     * @param id ID del servicio
     * @return ResponseEntity con detalles del servicio
     */
    @GetMapping("/servicios/{id}")
    public ResponseEntity<?> obtenerServicio(@PathVariable Long id) {
        logger.debug("Obteniendo detalles del servicio ID: {}", id);
        
        try {
            Optional<ServicioDTO> servicio = servicioService.buscarServicioPorId(id);
            
            if (servicio.isPresent()) {
                ServicioDTO servicioDTO = servicio.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("servicio", servicioDTO);
                response.put("disponible", servicioDTO.isDisponible());
                
                // Agregar servicios relacionados (misma categoría)
                if (servicioDTO.getIdCategoria() != null) {
                    List<ServicioDTO> relacionados = servicioService.buscarServiciosPorCategoria(
                        servicioDTO.getIdCategoria(), PageRequest.of(0, 4)).getContent();
                    response.put("serviciosRelacionados", relacionados);
                }
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error obteniendo servicio {}: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener el servicio");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * CU03 - Obtener todas las categorías disponibles.
     * 
     * @return ResponseEntity con lista de categorías
     */
    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        logger.debug("Obteniendo categorías disponibles");
        
        try {
            List<CategoriaDTO> categorias = categoriaService.obtenerCategoriasActivas();
            return ResponseEntity.ok(categorias);
            
        } catch (Exception e) {
            logger.error("Error obteniendo categorías: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener las categorías");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * CU03 - Buscar productos y servicios por término.
     * 
     * @param q Término de búsqueda
     * @param page Página
     * @param size Tamaño de página
     * @return ResponseEntity con resultados de búsqueda
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Buscando en catálogo: '{}'", q);
        
        try {
            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Término de búsqueda requerido"));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            
            Page<ProductoDTO> productos = productoService.buscarProductosPorNombre(q, pageable);
            Page<ServicioDTO> servicios = servicioService.buscarServiciosPorNombre(q, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("productos", productos);
            response.put("servicios", servicios);
            response.put("terminoBusqueda", q);
            response.put("totalResultados", productos.getTotalElements() + servicios.getTotalElements());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en búsqueda: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en la búsqueda");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Endpoint simple para probar conectividad
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "API funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene todos los productos y servicios para el frontend.
     * Formato compatible con el JavaScript.
     */
    @GetMapping("/completo")
    public ResponseEntity<?> obtenerCatalogoCompleto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        
        try {
            logger.info("Obteniendo catálogo completo para frontend - página: {}, tamaño: {}", page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
            
            // Cargar productos reales de la base de datos
            Page<ProductoDTO> productos = productoService.buscarProductosDisponibles(
                busqueda, categoria, precioMin, precioMax, pageable);
            
            // Cargar servicios reales de la base de datos
            Page<ServicioDTO> servicios = servicioService.buscarServiciosDisponibles(
                busqueda, categoria, precioMin, precioMax, pageable);
            
            List<Map<String, Object>> items = new java.util.ArrayList<>();
            
            // Agregar productos
            for (ProductoDTO producto : productos.getContent()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", producto.getIdProducto());
                item.put("nombre", producto.getNombre());
                item.put("descripcion", producto.getDescripcion());
                item.put("precio", producto.getPrecio());
                item.put("stock", producto.getStock());
                item.put("tipo", "producto");
                item.put("categoria", producto.getNombreCategoria() != null ? 
                        producto.getNombreCategoria() : "Sin categoría");
                item.put("icon", determinarIconoProducto(producto));
                item.put("disponible", producto.isDisponible());
                items.add(item);
            }
            
            // Agregar servicios
            for (ServicioDTO servicio : servicios.getContent()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", servicio.getIdServicio());
                item.put("nombre", servicio.getNombre());
                item.put("descripcion", servicio.getDescripcion());
                item.put("precio", servicio.getPrecio());
                item.put("duracion", servicio.getDuracion());
                item.put("tipo", "servicio");
                item.put("categoria", servicio.getNombreCategoria() != null ? 
                        servicio.getNombreCategoria() : "Sin categoría");
                item.put("icon", determinarIconoServicio(servicio));
                item.put("disponible", servicio.isDisponible());
                items.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", items);
            response.put("totalProductos", productos.getTotalElements());
            response.put("totalServicios", servicios.getTotalElements());
            response.put("totalElements", productos.getTotalElements() + servicios.getTotalElements());
            response.put("totalPages", Math.max(productos.getTotalPages(), servicios.getTotalPages()));
            response.put("currentPage", page);
            response.put("source", "MySQL Database");
            
            logger.info("Catálogo completo cargado: {} productos, {} servicios", 
                       productos.getTotalElements(), servicios.getTotalElements());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo catálogo completo: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("stackTrace", e.getStackTrace()[0].toString());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Obtiene solo productos en formato para JavaScript.
     */
    @GetMapping("/productos")
    public ResponseEntity<?> obtenerProductosParaFrontend(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        
        try {
            logger.info("Obteniendo productos para frontend - página: {}, tamaño: {}", page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
            
            Page<ProductoDTO> productos = productoService.buscarProductosDisponibles(
                busqueda, categoria, precioMin, precioMax, pageable);
            
            List<Map<String, Object>> items = new java.util.ArrayList<>();
            
            for (ProductoDTO producto : productos.getContent()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", producto.getIdProducto());
                item.put("nombre", producto.getNombre());
                item.put("descripcion", producto.getDescripcion());
                item.put("precio", producto.getPrecio());
                item.put("stock", producto.getStock());
                item.put("tipo", "producto");
                item.put("categoria", producto.getNombreCategoria() != null ? 
                        producto.getNombreCategoria() : "Sin categoría");
                item.put("icon", determinarIconoProducto(producto));
                item.put("disponible", producto.isDisponible());
                items.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productos", items);
            response.put("totalElements", productos.getTotalElements());
            response.put("totalPages", productos.getTotalPages());
            response.put("currentPage", page);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo productos: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Obtiene solo servicios en formato para JavaScript.
     */
    @GetMapping("/servicios")  
    public ResponseEntity<?> obtenerServiciosParaFrontend(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        
        try {
            logger.info("Obteniendo servicios para frontend - página: {}, tamaño: {}", page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
            
            Page<ServicioDTO> servicios = servicioService.buscarServiciosDisponibles(
                busqueda, categoria, precioMin, precioMax, pageable);
            
            List<Map<String, Object>> items = new java.util.ArrayList<>();
            
            for (ServicioDTO servicio : servicios.getContent()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", servicio.getIdServicio());
                item.put("nombre", servicio.getNombre());
                item.put("descripcion", servicio.getDescripcion());
                item.put("precio", servicio.getPrecio());
                item.put("duracion", servicio.getDuracion());
                item.put("tipo", "servicio");
                item.put("categoria", servicio.getNombreCategoria() != null ? 
                        servicio.getNombreCategoria() : "Sin categoría");
                item.put("icon", determinarIconoServicio(servicio));
                item.put("disponible", servicio.isDisponible());
                items.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("servicios", items);
            response.put("totalElements", servicios.getTotalElements());
            response.put("totalPages", servicios.getTotalPages());
            response.put("currentPage", page);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo servicios: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Determina el icono apropiado para un producto basado en su categoría.
     */
    private String determinarIconoProducto(ProductoDTO producto) {
        String categoria = producto.getNombreCategoria();
        if (categoria == null) return "fas fa-box";
        
        categoria = categoria.toLowerCase();
        if (categoria.contains("suplemento") || categoria.contains("proteína") || categoria.contains("creatina")) {
            return "fas fa-flask";
        } else if (categoria.contains("equipamiento") || categoria.contains("equipo") || categoria.contains("mancuerna")) {
            return "fas fa-dumbbell";
        } else if (categoria.contains("banca") || categoria.contains("mobiliario")) {
            return "fas fa-couch";
        } else if (categoria.contains("pill") || categoria.contains("medicament") || categoria.contains("vitamina")) {
            return "fas fa-pills";
        }
        return "fas fa-box";
    }
    
    /**
     * Determina el icono apropiado para un servicio basado en su categoría.
     */
    private String determinarIconoServicio(ServicioDTO servicio) {
        String categoria = servicio.getNombreCategoria();
        if (categoria == null) return "fas fa-cogs";
        
        categoria = categoria.toLowerCase();
        if (categoria.contains("entrenamiento") || categoria.contains("personal")) {
            return "fas fa-user-tie";
        } else if (categoria.contains("yoga") || categoria.contains("relajación")) {
            return "fas fa-leaf";
        } else if (categoria.contains("nutrición") || categoria.contains("nutricional")) {
            return "fas fa-apple-alt";
        } else if (categoria.contains("crossfit") || categoria.contains("funcional") || categoria.contains("intenso")) {
            return "fas fa-fire";
        } else if (categoria.contains("clases") || categoria.contains("grupal")) {
            return "fas fa-users";
        }
        return "fas fa-cogs";
    }
}