package com.applegym.service.impl;

import com.applegym.dto.CarritoDTO;
import com.applegym.dto.DetalleCarritoDTO;
import com.applegym.entity.*;
import com.applegym.repository.*;
import com.applegym.service.CarritoService;
import com.applegym.exception.ResourceNotFoundException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Carrito.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoServiceImpl.class);
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarritoActual(String emailCliente) {
        logger.debug("Obteniendo carrito actual para cliente: {}", emailCliente);
        
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ACTIVO")
            .orElseGet(() -> crearNuevoCarrito(cliente));
        
        return convertirADTO(carrito);
    }
    
    @Override
    public CarritoDTO agregarItem(String emailCliente, Long itemId, String tipo, Integer cantidad) {
        logger.debug("Agregando item al carrito - Cliente: {}, Item: {}, Tipo: {}, Cantidad: {}", 
                    emailCliente, itemId, tipo, cantidad);
        
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ACTIVO")
            .orElseGet(() -> crearNuevoCarrito(cliente));
        
        // Validar que el item existe y está disponible
        BigDecimal precioUnitario;
        String nombreItem;
        String descripcionItem;
        
        if ("producto".equalsIgnoreCase(tipo)) {
            Producto producto = productoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + itemId));
            
            if (!producto.getActivo() || producto.getStock() <= 0) {
                throw new IllegalStateException("Producto no disponible: " + producto.getNombre());
            }
            
            precioUnitario = producto.getPrecio();
            nombreItem = producto.getNombre();
            descripcionItem = producto.getDescripcion();
            
        } else if ("servicio".equalsIgnoreCase(tipo)) {
            Servicio servicio = servicioRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + itemId));
            
            if (!servicio.getActivo() || !servicio.isDisponible()) {
                throw new IllegalStateException("Servicio no disponible: " + servicio.getNombre());
            }
            
            precioUnitario = servicio.getPrecio();
            nombreItem = servicio.getNombre();
            descripcionItem = servicio.getDescripcion();
            
        } else {
            throw new IllegalArgumentException("Tipo de item inválido: " + tipo);
        }
        
        // Buscar si ya existe el item en el carrito
        DetalleCarrito detalleExistente = carrito.getDetalles().stream()
            .filter(d -> d.getTipo().equalsIgnoreCase(tipo) && d.getIdItem().equals(itemId))
            .findFirst()
            .orElse(null);
        
        if (detalleExistente != null) {
            // Actualizar cantidad
            detalleExistente.setCantidad(detalleExistente.getCantidad() + cantidad);
            detalleExistente.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(detalleExistente.getCantidad())));
        } else {
            // Crear nuevo detalle
            DetalleCarrito nuevoDetalle = new DetalleCarrito();
            nuevoDetalle.setCarrito(carrito);
            nuevoDetalle.setTipo(tipo.toUpperCase());
            nuevoDetalle.setIdItem(itemId);
            nuevoDetalle.setNombreItem(nombreItem);
            nuevoDetalle.setDescripcionItem(descripcionItem);
            nuevoDetalle.setPrecioUnitario(precioUnitario);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
            
            carrito.getDetalles().add(nuevoDetalle);
        }
        
        // Actualizar total del carrito
        actualizarTotalCarrito(carrito);
        
        carritoRepository.save(carrito);
        
        logger.info("Item agregado al carrito exitosamente - Carrito ID: {}", carrito.getIdCarrito());
        
        return convertirADTO(carrito);
    }
    
    @Override
    public void eliminarItem(String emailCliente, Long itemId) {
        logger.debug("Eliminando item del carrito - Cliente: {}, Item: {}", emailCliente, itemId);
        
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ACTIVO")
            .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        boolean itemEliminado = carrito.getDetalles().removeIf(
            detalle -> detalle.getIdItem().equals(itemId)
        );
        
        if (!itemEliminado) {
            throw new ResourceNotFoundException("Item no encontrado en el carrito: " + itemId);
        }
        
        actualizarTotalCarrito(carrito);
        carritoRepository.save(carrito);
        
        logger.info("Item eliminado del carrito exitosamente - Item ID: {}", itemId);
    }
    
    @Override
    public CarritoDTO actualizarCantidad(String emailCliente, Long itemId, Integer nuevaCantidad) {
        logger.debug("Actualizando cantidad en carrito - Cliente: {}, Item: {}, Nueva cantidad: {}", 
                    emailCliente, itemId, nuevaCantidad);
        
        if (nuevaCantidad <= 0) {
            eliminarItem(emailCliente, itemId);
            return obtenerCarritoActual(emailCliente);
        }
        
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ACTIVO")
            .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        DetalleCarrito detalle = carrito.getDetalles().stream()
            .filter(d -> d.getIdItem().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado en el carrito: " + itemId));
        
        detalle.setCantidad(nuevaCantidad);
        detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
        
        actualizarTotalCarrito(carrito);
        carritoRepository.save(carrito);
        
        logger.info("Cantidad actualizada exitosamente - Item ID: {}, Nueva cantidad: {}", itemId, nuevaCantidad);
        
        return convertirADTO(carrito);
    }
    
    @Override
    public void limpiarCarrito(String emailCliente) {
        logger.debug("Limpiando carrito para cliente: {}", emailCliente);
        
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ACTIVO")
            .orElse(null);
        
        if (carrito != null) {
            carrito.setEstado("PROCESADO");
            carritoRepository.save(carrito);
            logger.info("Carrito limpiado exitosamente - Cliente: {}", emailCliente);
        }
    }
    
    private Carrito crearNuevoCarrito(Cliente cliente) {
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setCliente(cliente);
        nuevoCarrito.setFecha(LocalDateTime.now());
        nuevoCarrito.setEstado("ACTIVO");
        nuevoCarrito.setTotal(BigDecimal.ZERO);
        
        return carritoRepository.save(nuevoCarrito);
    }
    
    private void actualizarTotalCarrito(Carrito carrito) {
        BigDecimal total = carrito.getDetalles().stream()
            .map(DetalleCarrito::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        carrito.setTotal(total);
    }
    
    private CarritoDTO convertirADTO(Carrito carrito) {
        CarritoDTO dto = modelMapper.map(carrito, CarritoDTO.class);
        
        if (carrito.getCliente() != null) {
            dto.setNombreCliente(carrito.getCliente().getNombreCliente());
        }
        
        List<DetalleCarritoDTO> detallesDTO = carrito.getDetalles().stream()
            .map(this::convertirDetalleADTO)
            .collect(Collectors.toList());
        
        dto.setDetalles(detallesDTO);
        
        return dto;
    }
    
    private DetalleCarritoDTO convertirDetalleADTO(DetalleCarrito detalle) {
        DetalleCarritoDTO dto = modelMapper.map(detalle, DetalleCarritoDTO.class);
        dto.setNombreItem(detalle.getNombreItem());
        dto.setDescripcionItem(detalle.getDescripcionItem());
        return dto;
    }
}