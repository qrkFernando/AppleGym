package com.applegym.service;

import com.applegym.dto.ServicioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de Servicios.
 * 
 * Define los contratos de negocio para operaciones relacionadas con servicios.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public interface ServicioService {
    
    Optional<ServicioDTO> buscarServicioPorId(Long id);
    
    Page<ServicioDTO> buscarServiciosDisponibles(String nombre, Long idCategoria, 
                                               BigDecimal precioMin, BigDecimal precioMax, 
                                               Pageable pageable);
    
    Page<ServicioDTO> buscarServiciosPorNombre(String nombre, Pageable pageable);
    
    Page<ServicioDTO> buscarServiciosPorCategoria(Long idCategoria, Pageable pageable);
}