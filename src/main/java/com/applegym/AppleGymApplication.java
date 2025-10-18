package com.applegym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Clase principal de la aplicación AppleGym.
 * 
 * Esta aplicación implementa un sistema de gestión para gimnasios siguiendo:
 * - Arquitectura Cliente-Servidor
 * - Patrón MVC (Model-View-Controller)
 * - Principios SOLID
 * - Patrón DAO (Data Access Object)
 * - Desarrollo dirigido por pruebas (TDD)
 * 
 * @author AppleGym Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableTransactionManagement
public class AppleGymApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppleGymApplication.class, args);
    }
}