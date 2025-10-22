package com.applegym.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar hashes BCrypt de contraseñas.
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generar hash para la contraseña del administrador
        String password = "applegymadmin";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("==============================================");
        System.out.println("Password Hash Generator - AppleGym");
        System.out.println("==============================================");
        System.out.println("Password original: " + password);
        System.out.println("Hash BCrypt: " + hashedPassword);
        System.out.println("==============================================");
        
        // Verificar que el hash funciona
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("Verificación: " + (matches ? "✓ OK" : "✗ ERROR"));
        System.out.println("==============================================");
    }
}
