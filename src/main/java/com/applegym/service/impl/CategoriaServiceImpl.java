package com.applegym.service.impl;

import com.applegym.dto.CategoriaDTO;
import com.applegym.entity.Categoria;
import com.applegym.repository.CategoriaRepository;
import com.applegym.service.CategoriaService;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Categoria.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoriaServiceImpl.class);
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerCategoriasActivas() {
        logger.debug("Obteniendo categorías activas");
        
        return categoriaRepository.findByActivo(true)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> buscarCategoriasActivas() {
        return obtenerCategoriasActivas();
    }
    
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = modelMapper.map(categoria, CategoriaDTO.class);
        dto.setTotalProductos(categoria.getTotalProductos());
        dto.setTotalServicios(categoria.getTotalServicios());
        return dto;
    }
}