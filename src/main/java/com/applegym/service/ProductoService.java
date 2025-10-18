package com.applegym.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.applegym.dto.ProductoDTO;

/**
 * Interfaz de servicio para la gestión de Productos.
 * 
 * Define los contratos de negocio para operaciones relacionadas con productos.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */


public interface ProductoService {
    
    Optional<ProductoDTO> buscarProductoPorId(Long id);
    
    Page<ProductoDTO> buscarProductosDisponibles(String nombre, Long idCategoria, 
                                               BigDecimal precioMin, BigDecimal precioMax, 
                                               Pageable pageable);
    
    Page<ProductoDTO> buscarProductosPorNombre(String nombre, Pageable pageable);
    
    Page<ProductoDTO> buscarProductosPorCategoria(Long idCategoria, Pageable pageable);
}