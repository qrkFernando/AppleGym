package com.applegym.service;

import com.applegym.dto.CategoriaDTO;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de Categorías.
 * 
 * Define los contratos de negocio para operaciones relacionadas con categorías.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public interface CategoriaService {
    
    List<CategoriaDTO> obtenerCategoriasActivas();
}