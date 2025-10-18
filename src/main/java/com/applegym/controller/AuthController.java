package com.applegym.controller;

import com.applegym.dto.ClienteDTO;
import com.applegym.dto.ClienteRegistroDTO;
import com.applegym.dto.LoginRequest;
import com.applegym.dto.LoginResponse;
import com.applegym.security.JwtTokenProvider;
import com.applegym.service.ClienteService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para autenticación y registro de usuarios.
 * 
 * Implementa los casos de uso CU01 (Registro de cliente) y CU02 (Inicio de sesión).
 * Maneja el registro de nuevos clientes y la autenticación con JWT.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    /**
     * CU02 - Inicio de sesión de cliente.
     * 
     * Autentica las credenciales del cliente y retorna un token JWT
     * si la autenticación es exitosa.
     * 
     * @param loginRequest Credenciales del usuario
     * @return ResponseEntity con token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Intento de inicio de sesión para usuario: {}", loginRequest.getEmail());
        
        try {
            // Autenticar credenciales
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail().toLowerCase().trim(),
                    loginRequest.getPassword()
                )
            );
            
            // Generar token JWT
            String jwt = tokenProvider.generateToken(authentication);
            
            // Obtener información del cliente
            ClienteDTO clienteDTO = clienteService.buscarClientePorEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
            // Crear respuesta de login exitoso
            LoginResponse loginResponse = new LoginResponse(jwt, "Bearer", clienteDTO);
            
            logger.info("Inicio de sesión exitoso para usuario: {}", loginRequest.getEmail());
            
            return ResponseEntity.ok(loginResponse);
            
        } catch (AuthenticationException e) {
            logger.warn("Fallo en autenticación para usuario: {}", loginRequest.getEmail());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciales inválidas");
            errorResponse.put("message", "Email o contraseña incorrectos");
            errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    /**
     * CU01 - Registro de cliente.
     * 
     * Registra un nuevo cliente en el sistema validando que el email
     * no esté previamente registrado.
     * 
     * @param clienteRegistroDTO Datos del nuevo cliente
     * @return ResponseEntity con el cliente registrado
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody ClienteRegistroDTO clienteRegistroDTO) {
        logger.info("Intento de registro para cliente con email: {}", clienteRegistroDTO.getEmail());
        
        try {
            // Verificar si el email ya existe
            if (clienteService.emailYaRegistrado(clienteRegistroDTO.getEmail())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Email ya registrado");
                errorResponse.put("message", "El email ya está registrado en el sistema");
                errorResponse.put("field", "email");
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Validar que las contraseñas coincidan
            if (!clienteRegistroDTO.isPasswordMatch()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Contraseñas no coinciden");
                errorResponse.put("message", "La contraseña y su confirmación deben ser iguales");
                errorResponse.put("field", "confirmPassword");
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Registrar cliente
            ClienteDTO clienteRegistrado = clienteService.registrarCliente(clienteRegistroDTO);
            
            // Crear respuesta de registro exitoso (sin token automático)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cliente registrado exitosamente");
            response.put("cliente", clienteRegistrado);
            response.put("status", "success");
            
            logger.info("Cliente registrado exitosamente con ID: {}", clienteRegistrado.getIdCliente());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error durante el registro del cliente: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en registro");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Validar token JWT.
     * 
     * Endpoint para que el frontend verifique si un token sigue siendo válido.
     * 
     * @param authHeader Header de autorización con el token
     * @return ResponseEntity indicando si el token es válido
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", username);
                    response.put("timeToExpiration", tokenProvider.getTimeToExpiration(token));
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", "Token inválido o expirado");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Error validando token: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", "Error validando token");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}