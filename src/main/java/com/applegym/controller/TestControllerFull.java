package com.applegym.controller;

import com.applegym.entity.Cliente;
import com.applegym.entity.Producto;
import com.applegym.entity.Servicio;
import com.applegym.repository.ClienteRepository;
import com.applegym.repository.ProductoRepository;
import com.applegym.repository.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-full")
public class TestControllerFull {
    
    private static final Logger logger = LoggerFactory.getLogger(TestControllerFull.class);
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "AppleGym API is running!");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @PostMapping("/register-simple")
    public Map<String, Object> registerSimple(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String nombre = request.get("nombreCliente");
            String password = request.get("password");
            String telefono = request.get("telefono");
            String direccion = request.get("direccion");
            
            boolean exists = clienteRepository.existsByEmail(email);
            if (exists) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Email ya registrado");
                return response;
            }
            
            Cliente cliente = new Cliente();
            cliente.setNombreCliente(nombre);
            cliente.setEmail(email);
            cliente.setPassword(passwordEncoder.encode(password));
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setActivo(true);
            cliente.setFechaRegistro(LocalDateTime.now());
            
            Cliente clienteGuardado = clienteRepository.save(cliente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente registrado exitosamente");
            response.put("clienteId", clienteGuardado.getIdCliente());
            response.put("email", clienteGuardado.getEmail());
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al registrar cliente: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/clientes")
    public Map<String, Object> getClientes() {
        try {
            long count = clienteRepository.count();
            var clientes = clienteRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", count);
            response.put("clientes", clientes);
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al obtener clientes: " + e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/login-simple")
    public Map<String, Object> loginSimple(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            var clienteOpt = clienteRepository.findByEmail(email);
            if (clienteOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Usuario no encontrado");
                return response;
            }
            
            Cliente cliente = clienteOpt.get();
            boolean passwordMatch = passwordEncoder.matches(password, cliente.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            if (passwordMatch) {
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("cliente", Map.of(
                    "id", cliente.getIdCliente(),
                    "nombre", cliente.getNombreCliente(),
                    "email", cliente.getEmail()
                ));
            } else {
                response.put("error", "Contraseña incorrecta");
            }
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error en login: " + e.getMessage());
            return response;
        }
    }
    
    // NUEVOS ENDPOINTS PARA PRODUCTOS Y SERVICIOS
    @GetMapping("/productos")
    public Map<String, Object> getProductos() {
        try {
            logger.info("Obteniendo productos desde base de datos MySQL");
            
            List<Map<String, Object>> productosRaw = jdbcTemplate.queryForList(
                "SELECT id_producto as id, nombre, descripcion, precio, stock, " +
                "c.nombre_categoria as categoria FROM producto p " +
                "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                "WHERE p.activo = true"
            );
            
            List<Map<String, Object>> productos = new ArrayList<>();
            for (Map<String, Object> prod : productosRaw) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", prod.get("id"));
                item.put("nombre", prod.get("nombre"));
                item.put("descripcion", prod.get("descripcion"));
                item.put("precio", prod.get("precio"));
                item.put("stock", prod.get("stock"));
                item.put("tipo", "productos");
                item.put("categoria", prod.get("categoria") != null ? prod.get("categoria") : "Sin categoría");
                item.put("icon", determinarIconoProducto((String) prod.get("categoria")));
                item.put("disponible", ((Number) prod.get("stock")).intValue() > 0);
                productos.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productos", productos);
            response.put("total", productos.size());
            response.put("source", "MySQL Database");
            
            logger.info("Productos cargados: {}", productos.size());
            
            return response;
        } catch (Exception e) {
            logger.error("Error al obtener productos: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al obtener productos: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/servicios")
    public Map<String, Object> getServicios() {
        try {
            logger.info("Obteniendo servicios desde base de datos MySQL");
            
            List<Map<String, Object>> serviciosRaw = jdbcTemplate.queryForList(
                "SELECT id_servicio as id, nombre, descripcion, precio, duracion, " +
                "c.nombre_categoria as categoria FROM servicio s " +
                "LEFT JOIN categoria c ON s.id_categoria = c.id_categoria " +
                "WHERE s.activo = true"
            );
            
            List<Map<String, Object>> servicios = new ArrayList<>();
            for (Map<String, Object> serv : serviciosRaw) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", serv.get("id"));
                item.put("nombre", serv.get("nombre"));
                item.put("descripcion", serv.get("descripcion"));
                item.put("precio", serv.get("precio"));
                item.put("duracion", serv.get("duracion"));
                item.put("tipo", "servicios");
                item.put("categoria", serv.get("categoria") != null ? serv.get("categoria") : "Sin categoría");
                item.put("icon", determinarIconoServicio((String) serv.get("categoria")));
                item.put("disponible", true);
                servicios.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("servicios", servicios);
            response.put("total", servicios.size());
            response.put("source", "MySQL Database");
            
            logger.info("Servicios cargados: {}", servicios.size());
            
            return response;
        } catch (Exception e) {
            logger.error("Error al obtener servicios: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al obtener servicios: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/catalogo")
    public Map<String, Object> getCatalogo() {
        try {
            logger.info("Obteniendo catálogo desde base de datos MySQL");
            
            // Usar consultas nativas más simples
            List<Map<String, Object>> productosRaw = jdbcTemplate.queryForList(
                "SELECT p.id_producto as id, p.nombre, p.descripcion, p.precio, p.stock, " +
                "COALESCE(c.nombre_categoria, 'Sin categoría') as categoria " +
                "FROM producto p " +
                "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                "WHERE p.activo = 1"
            );
            
            List<Map<String, Object>> serviciosRaw = jdbcTemplate.queryForList(
                "SELECT s.id_servicio as id, s.nombre, s.descripcion, s.precio, s.duracion, " +
                "COALESCE(c.nombre_categoria, 'Sin categoría') as categoria " +
                "FROM servicio s " +
                "LEFT JOIN categoria c ON s.id_categoria = c.id_categoria " +
                "WHERE s.activo = 1"
            );
            
            // Convertir a formato esperado por el frontend
            List<Map<String, Object>> items = new ArrayList<>();
            
            // Agregar productos
            for (Map<String, Object> prod : productosRaw) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", prod.get("id"));
                item.put("nombre", prod.get("nombre"));
                item.put("descripcion", prod.get("descripcion"));
                item.put("precio", prod.get("precio"));
                item.put("stock", prod.get("stock"));
                item.put("tipo", "productos");
                item.put("categoria", prod.get("categoria"));
                item.put("icon", determinarIconoProducto((String) prod.get("categoria")));
                item.put("disponible", ((Number) prod.get("stock")).intValue() > 0);
                items.add(item);
            }
            
            // Agregar servicios
            for (Map<String, Object> serv : serviciosRaw) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", serv.get("id"));
                item.put("nombre", serv.get("nombre"));
                item.put("descripcion", serv.get("descripcion"));
                item.put("precio", serv.get("precio"));
                item.put("duracion", serv.get("duracion"));
                item.put("tipo", "servicios");
                item.put("categoria", serv.get("categoria"));
                item.put("icon", determinarIconoServicio((String) serv.get("categoria")));
                item.put("disponible", true);
                items.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", items);
            response.put("totalProductos", productosRaw.size());
            response.put("totalServicios", serviciosRaw.size());
            response.put("source", "MySQL Database applegym");
            
            logger.info("✅ Catálogo cargado exitosamente: {} productos, {} servicios", productosRaw.size(), serviciosRaw.size());
            
            return response;
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener catálogo desde base de datos: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al conectar con la base de datos: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/metodos-pago")
    public Map<String, Object> getMetodosPago() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("metodosDisponibles", List.of(
            Map.of("id", "TARJETA_CREDITO", "nombre", "Tarjeta de Crédito", "icon", "fas fa-credit-card"),
            Map.of("id", "TARJETA_DEBITO", "nombre", "Tarjeta de Débito", "icon", "fas fa-credit-card"),
            Map.of("id", "EFECTIVO", "nombre", "Efectivo", "icon", "fas fa-money-bill"),
            Map.of("id", "TRANSFERENCIA", "nombre", "Transferencia Bancaria", "icon", "fas fa-university")
        ));
        return response;
    }
    
    // Funciones helper para determinar iconos
    private String determinarIconoProducto(String categoria) {
        if (categoria == null) return "fas fa-box";
        
        String cat = categoria.toLowerCase();
        if (cat.contains("suplemento") || cat.contains("proteína") || cat.contains("creatina")) {
            return "fas fa-flask";
        } else if (cat.contains("equipamiento") || cat.contains("equipo") || cat.contains("mancuerna")) {
            return "fas fa-dumbbell";
        } else if (cat.contains("banca") || cat.contains("mobiliario")) {
            return "fas fa-couch";
        } else if (cat.contains("pill") || cat.contains("medicament") || cat.contains("vitamina")) {
            return "fas fa-pills";
        }
        return "fas fa-box";
    }
    
    private String determinarIconoServicio(String categoria) {
        if (categoria == null) return "fas fa-cogs";
        
        String cat = categoria.toLowerCase();
        if (cat.contains("entrenamiento") || cat.contains("personal")) {
            return "fas fa-user-tie";
        } else if (cat.contains("yoga") || cat.contains("relajación")) {
            return "fas fa-leaf";
        } else if (cat.contains("nutrición") || cat.contains("nutricional")) {
            return "fas fa-apple-alt";
        } else if (cat.contains("crossfit") || cat.contains("funcional") || cat.contains("intenso")) {
            return "fas fa-fire";
        } else if (cat.contains("clases") || cat.contains("grupal")) {
            return "fas fa-users";
        }
        return "fas fa-cogs";
    }
}