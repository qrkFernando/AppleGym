package com.applegym.service.impl;

import com.applegym.dto.ProductoDTO;
import com.applegym.entity.Producto;
import com.applegym.repository.ProductoRepository;
import com.applegym.service.ProductoService;

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
 * Implementación del servicio de Producto.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> buscarProductoPorId(Long id) {
        logger.debug("Buscando producto por ID: {}", id);
        
        return productoRepository.findById(id)
                .filter(producto -> producto.getActivo())
                .map(this::convertirADTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductosDisponibles(String nombre, Long idCategoria, 
                                                       BigDecimal precioMin, BigDecimal precioMax, 
                                                       Pageable pageable) {
        logger.debug("Buscando productos disponibles con filtros");
        
        try {
            return productoRepository.findProductosPorCriterios(
                    nombre, idCategoria, precioMin, precioMax, true, pageable)
                    .map(this::convertirADTO);
        } catch (Exception e) {
            // Fallback a búsqueda básica
            List<Producto> productos = productoRepository.findProductosDisponibles();
            List<ProductoDTO> productosDTO = productos.stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return new PageImpl<>(productosDTO, pageable, productosDTO.size());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductosPorNombre(String nombre, Pageable pageable) {
        logger.debug("Buscando productos por nombre: {}", nombre);
        
        List<ProductoDTO> productos = productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(productos, pageable, productos.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductosPorCategoria(Long idCategoria, Pageable pageable) {
        logger.debug("Buscando productos por categoría: {}", idCategoria);
        
        List<ProductoDTO> productos = productoRepository.findByCategoriaIdAndActivo(idCategoria, true)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(productos, pageable, productos.size());
    }
    
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
        if (producto.getCategoria() != null) {
            dto.setIdCategoria(producto.getCategoria().getIdCategoria());
            dto.setNombreCategoria(producto.getCategoria().getNombreCategoria());
        }
        dto.setDisponible(producto.isDisponible());
        return dto;
    }
}
