package com.applegym.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Proveedor de tokens JWT para autenticación.
 * 
 * Maneja la generación, validación y extracción de información
 * de tokens JWT de forma segura.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;
    
    /**
     * Obtiene la clave secreta para firmar los tokens.
     * 
     * @return SecretKey generada a partir del secret configurado
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Genera un token JWT a partir de la autenticación.
     * 
     * @param authentication Información de autenticación del usuario
     * @return Token JWT generado
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Genera un token JWT para un usuario específico.
     * 
     * @param username Nombre de usuario (email)
     * @return Token JWT generado
     */
    public String generateTokenForUser(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Extrae el nombre de usuario del token JWT.
     * 
     * @param token Token JWT
     * @return Nombre de usuario extraído
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    /**
     * Obtiene la fecha de expiración del token.
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getExpiration();
    }
    
    /**
     * Valida si el token JWT es válido.
     * 
     * @param authToken Token a validar
     * @return true si el token es válido, false caso contrario
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT mal formado: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string está vacío: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error validando token JWT: {}", ex.getMessage());
        }
        return false;
    }
    
    /**
     * Verifica si el token está expirado.
     * 
     * @param token Token JWT
     * @return true si está expirado, false caso contrario
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Error verificando expiración del token: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * Obtiene el tiempo restante de vida del token en milisegundos.
     * 
     * @param token Token JWT
     * @return Tiempo restante en milisegundos, 0 si está expirado
     */
    public long getTimeToExpiration(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long timeLeft = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, timeLeft);
        } catch (Exception e) {
            logger.error("Error calculando tiempo de expiración: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Extrae todas las claims del token.
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}