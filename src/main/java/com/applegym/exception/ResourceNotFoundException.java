package com.applegym.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra.
 * 
 * Esta excepción se utiliza cuando se busca una entidad por ID u otro
 * criterio y no existe en la base de datos.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s no encontrado con %s: '%s'", resource, field, value));
    }
}