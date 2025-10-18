package com.applegym.controller;

import com.applegym.entity.Producto;
import com.applegym.entity.Servicio;
import com.applegym.repository.ProductoRepository;
import com.applegym.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class SimpleProductosController {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @GetMapping("/api/productos")
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }
    
    @GetMapping("/api/servicios")
    public List<Servicio> getServicios() {
        return servicioRepository.findAll();
    }
    
    @GetMapping("/api/simple/metodos-pago")
    public Map<String, Object> getMetodosPago() {
        Map<String, Object> response = new HashMap<>();
        response.put("metodosDisponibles", List.of(
            Map.of("id", "TARJETA_CREDITO", "nombre", "Tarjeta de Crédito", "icon", "fas fa-credit-card"),
            Map.of("id", "TARJETA_DEBITO", "nombre", "Tarjeta de Débito", "icon", "fas fa-credit-card"), 
            Map.of("id", "EFECTIVO", "nombre", "Efectivo", "icon", "fas fa-money-bill"),
            Map.of("id", "TRANSFERENCIA", "nombre", "Transferencia Bancaria", "icon", "fas fa-university")
        ));
        return response;
    }
}