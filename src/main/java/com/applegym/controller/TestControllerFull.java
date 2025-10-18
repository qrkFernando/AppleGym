package com.applegym.controller;

import com.applegym.entity.Cliente;
import com.applegym.entity.Producto;
import com.applegym.entity.Servicio;
import com.applegym.repository.ClienteRepository;
import com.applegym.repository.ProductoRepository;
import com.applegym.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-full")
public class TestControllerFull {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
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
            List<Producto> productos = productoRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", productos.size());
            response.put("productos", productos);
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al obtener productos: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/servicios")
    public Map<String, Object> getServicios() {
        try {
            List<Servicio> servicios = servicioRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", servicios.size());
            response.put("servicios", servicios);
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al obtener servicios: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/catalogo")
    public Map<String, Object> getCatalogo() {
        try {
            List<Producto> productos = productoRepository.findAll();
            List<Servicio> servicios = servicioRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productos", productos);
            response.put("servicios", servicios);
            response.put("totalProductos", productos.size());
            response.put("totalServicios", servicios.size());
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al obtener catálogo: " + e.getMessage());
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
}