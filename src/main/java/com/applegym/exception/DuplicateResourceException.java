package com.applegym.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 * 
 * Esta excepción se utiliza para manejar casos de duplicación de datos
 * únicos como emails, códigos, etc.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s ya existe con %s: '%s'", resource, field, value));
    }
}