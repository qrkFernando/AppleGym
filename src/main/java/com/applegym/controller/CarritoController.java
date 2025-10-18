package com.applegym.controller;

import com.applegym.dto.CarritoDTO;
import com.applegym.service.CarritoService;
import com.applegym.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la gestión del carrito de compras.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CarritoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);
    
    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@Valid @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            Long productoId = Long.valueOf(request.get("productoId").toString());
            String tipo = request.get("tipo").toString(); // "producto" o "servicio"
            Integer cantidad = Integer.valueOf(request.getOrDefault("cantidad", 1).toString());
            
            CarritoDTO resultado = carritoService.agregarItem(email, productoId, tipo, cantidad);
            
            return ResponseEntity.ok(Map.of(
                "message", "Producto agregado al carrito exitosamente",
                "carrito", resultado
            ));
            
        } catch (Exception e) {
            logger.error("Error agregando al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> obtenerCarrito(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            CarritoDTO carrito = carritoService.obtenerCarritoActual(email);
            
            return ResponseEntity.ok(carrito);
            
        } catch (Exception e) {
            logger.error("Error obteniendo carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long itemId, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            carritoService.eliminarItem(email, itemId);
            
            return ResponseEntity.ok(Map.of("message", "Item eliminado del carrito"));
            
        } catch (Exception e) {
            logger.error("Error eliminando del carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Long itemId, @RequestBody Map<String, Integer> request, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            Integer nuevaCantidad = request.get("cantidad");
            
            CarritoDTO resultado = carritoService.actualizarCantidad(email, itemId, nuevaCantidad);
            
            return ResponseEntity.ok(Map.of(
                "message", "Cantidad actualizada exitosamente",
                "carrito", resultado
            ));
            
        } catch (Exception e) {
            logger.error("Error actualizando cantidad: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}