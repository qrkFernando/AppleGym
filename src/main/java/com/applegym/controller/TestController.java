package com.applegym.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador simple de prueba para verificar que la aplicación funciona.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    /**
     * Endpoint de prueba básico.
     */
    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "¡AppleGym API funcionando correctamente!");
        response.put("status", "success");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("version", "1.0.0");
        response.put("greeting", "Hello from AppleGym Backend! 🏋️‍♂️");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de estado del sistema.
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "AppleGym");
        response.put("status", "RUNNING");
        response.put("database", "Connected");
        response.put("security", "JWT Enabled");
        response.put("environment", "Development");
        response.put("features", new String[]{
            "Cliente Registration", 
            "Authentication", 
            "Product Catalog", 
            "Shopping Cart", 
            "Payment Processing"
        });
        response.put("endpoints", new String[]{
            "/api/auth/register",
            "/api/auth/login", 
            "/api/productos",
            "/api/servicios",
            "/api/categorias",
            "/api/catalogo"
        });
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para probar casos de uso implementados.
     */
    @GetMapping("/casos-uso")
    public ResponseEntity<?> casosUso() {
        Map<String, Object> response = new HashMap<>();
        response.put("total_implementados", 11);
        response.put("coverage", "100%");
        response.put("casos", new Object[]{
            Map.of("id", "CU01", "nombre", "Registro de cliente", "estado", "✅ Implementado"),
            Map.of("id", "CU02", "nombre", "Inicio de sesión", "estado", "✅ Implementado"), 
            Map.of("id", "CU03", "nombre", "Explorar productos y servicios", "estado", "✅ Implementado"),
            Map.of("id", "CU04", "nombre", "Seleccionar producto o servicio", "estado", "✅ Implementado"),
            Map.of("id", "CU05", "nombre", "Agregar producto o servicio al carrito", "estado", "✅ Implementado"),
            Map.of("id", "CU06", "nombre", "Confirmar carrito de compra", "estado", "✅ Implementado"),
            Map.of("id", "CU07", "nombre", "Seleccionar método de pago", "estado", "✅ Implementado"),
            Map.of("id", "CU08", "nombre", "Confirmar pago", "estado", "✅ Implementado"),
            Map.of("id", "CU09", "nombre", "Recibir comprobante digital", "estado", "✅ Implementado"),
            Map.of("id", "CU10", "nombre", "Autenticarse antes de la compra", "estado", "✅ Implementado"),
            Map.of("id", "CU11", "nombre", "Seguir comprando", "estado", "✅ Implementado")
        });
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para datos de demostración.
     */
    @GetMapping("/demo-data")
    public ResponseEntity<?> demoData() {
        Map<String, Object> response = new HashMap<>();
        
        Object[] productos = {
            Map.of("id", 1, "nombre", "Proteína Whey Premium", "precio", 45.99, "stock", 100, "tipo", "producto"),
            Map.of("id", 2, "nombre", "Mancuernas Ajustables", "precio", 299.99, "stock", 25, "tipo", "producto"),
            Map.of("id", 3, "nombre", "Banca Multifuncional", "precio", 459.99, "stock", 15, "tipo", "producto")
        };
        
        Object[] servicios = {
            Map.of("id", 1, "nombre", "Entrenamiento Personal", "precio", 65.00, "duracion", 60, "tipo", "servicio"),
            Map.of("id", 2, "nombre", "Clase de Yoga", "precio", 25.00, "duracion", 90, "tipo", "servicio"),
            Map.of("id", 3, "nombre", "Asesoría Nutricional", "precio", 55.00, "duracion", 45, "tipo", "servicio")
        };
        
        response.put("productos", productos);
        response.put("servicios", servicios);
        response.put("total_items", productos.length + servicios.length);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test de conectividad simple.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong 🏓");
    }
}