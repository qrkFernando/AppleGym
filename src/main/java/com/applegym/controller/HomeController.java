package com.applegym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para servir el frontend y páginas estáticas.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Controller
public class HomeController {
    
    /**
     * Página principal - redirige al frontend.
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
    
    /**
     * Página principal con ruta explícita.
     */
    @GetMapping("/home")
    public String home() {
        return "forward:/index.html";
    }
    
    /**
     * Página principal con ruta de aplicación.
     */
    @GetMapping("/app")
    public String app() {
        return "forward:/index.html";
    }
}