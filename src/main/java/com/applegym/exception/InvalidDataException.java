package com.applegym.exception;

/**
 * Excepción lanzada cuando los datos proporcionados son inválidos.
 * 
 * Esta excepción se utiliza para validaciones de negocio y formato
 * de datos de entrada.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class InvalidDataException extends RuntimeException {
    
    public InvalidDataException(String message) {
        super(message);
    }
    
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}