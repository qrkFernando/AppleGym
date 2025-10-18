package com.applegym.service.impl;

import com.applegym.dto.ServicioDTO;
import com.applegym.entity.Servicio;
import com.applegym.repository.ServicioRepository;
import com.applegym.service.ServicioService;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Servicio.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {
    
    private static final Logger logger = LoggerFactory.getLogger(ServicioServiceImpl.class);
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ServicioDTO> buscarServicioPorId(Long id) {
        logger.debug("Buscando servicio por ID: {}", id);
        
        return servicioRepository.findById(id)
                .filter(servicio -> servicio.getActivo())
                .map(this::convertirADTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServicioDTO> buscarServiciosDisponibles(String nombre, Long idCategoria, 
                                                       BigDecimal precioMin, BigDecimal precioMax, 
                                                       Pageable pageable) {
        logger.debug("Buscando servicios disponibles con filtros");
        
        List<Servicio> servicios = servicioRepository.findByActivo(true);
        
        // Aplicar filtros
        List<ServicioDTO> serviciosFiltrados = servicios.stream()
                .filter(servicio -> nombre == null || 
                        servicio.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(servicio -> idCategoria == null || 
                        (servicio.getCategoria() != null && servicio.getCategoria().getIdCategoria().equals(idCategoria)))
                .filter(servicio -> precioMin == null || 
                        servicio.getPrecio().compareTo(precioMin) >= 0)
                .filter(servicio -> precioMax == null || 
                        servicio.getPrecio().compareTo(precioMax) <= 0)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(serviciosFiltrados, pageable, serviciosFiltrados.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServicioDTO> buscarServiciosPorNombre(String nombre, Pageable pageable) {
        logger.debug("Buscando servicios por nombre: {}", nombre);
        
        List<ServicioDTO> servicios = servicioRepository.findByActivo(true)
                .stream()
                .filter(servicio -> servicio.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(servicios, pageable, servicios.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServicioDTO> buscarServiciosPorCategoria(Long idCategoria, Pageable pageable) {
        logger.debug("Buscando servicios por categoría: {}", idCategoria);
        
        List<ServicioDTO> servicios = servicioRepository.findByActivo(true)
                .stream()
                .filter(servicio -> servicio.getCategoria() != null && 
                                   servicio.getCategoria().getIdCategoria().equals(idCategoria))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(servicios, pageable, servicios.size());
    }
    
    private ServicioDTO convertirADTO(Servicio servicio) {
        ServicioDTO dto = modelMapper.map(servicio, ServicioDTO.class);
        if (servicio.getCategoria() != null) {
            dto.setIdCategoria(servicio.getCategoria().getIdCategoria());
            dto.setNombreCategoria(servicio.getCategoria().getNombreCategoria());
        }
        dto.setDisponible(servicio.isDisponible());
        return dto;
    }
}