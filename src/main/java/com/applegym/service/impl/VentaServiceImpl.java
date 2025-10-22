package com.applegym.service.impl;

import com.applegym.dto.VentaDTO;
import com.applegym.entity.*;
import com.applegym.repository.*;
import com.applegym.service.VentaService;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de Ventas.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
@Transactional
public class VentaServiceImpl implements VentaService {
    
    private static final Logger logger = LoggerFactory.getLogger(VentaServiceImpl.class);
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public VentaDTO procesarVenta(String emailCliente, String metodoPago) {
        logger.debug("Procesando venta - Cliente: {}, Método: {}", emailCliente, metodoPago);
        
        // 1. Validar cliente
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        // 2. Obtener carrito activo
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ACTIVO")
            .orElseThrow(() -> new ResourceNotFoundException("No hay carrito activo para el cliente"));
        
        if (carrito.getDetalles().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }
        
        // 3. Crear la venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setTotal(carrito.getTotal());
        venta.setEstado("PROCESANDO");
        
        // 4. Crear detalles de venta desde el carrito
        for (DetalleCarrito detalleCarrito : carrito.getDetalles()) {
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(venta);
            detalleVenta.setIdProductoServicio(detalleCarrito.getIdItem());
            detalleVenta.setTipo(detalleCarrito.getTipo());
            detalleVenta.setNombre(detalleCarrito.getNombreItem());
            detalleVenta.setCantidad(detalleCarrito.getCantidad());
            detalleVenta.setPrecioUnitario(detalleCarrito.getPrecioUnitario());
            detalleVenta.setSubtotal(detalleCarrito.getSubtotal());
            
            // Asociar producto o servicio según corresponda
            if ("PRODUCTO".equals(detalleCarrito.getTipo())) {
                productoRepository.findById(detalleCarrito.getIdItem())
                    .ifPresent(detalleVenta::setProducto);
            } else if ("SERVICIO".equals(detalleCarrito.getTipo())) {
                servicioRepository.findById(detalleCarrito.getIdItem())
                    .ifPresent(detalleVenta::setServicio);
            }
            
            venta.getDetalles().add(detalleVenta);
        }
        
        // 5. Crear el pago
        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setTipoPago(metodoPago);
        pago.setMonto(venta.getTotal());
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstadoPago("COMPLETADO");
        
        venta.setPago(pago);
        
        // 6. Crear comprobante
        Comprobante comprobante = new Comprobante();
        comprobante.setVenta(venta);
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setTipoComprobante("BOLETA");
        comprobante.setSerieComprobante("001");
        comprobante.setEstadoComprobante("GENERADO");
        
        venta.setComprobante(comprobante);
        
        // 7. Actualizar stock de productos
        actualizarStock(carrito.getDetalles());
        
        // 8. Guardar la venta
        venta.setEstado("COMPLETADO");
        venta = ventaRepository.save(venta);
        
        // 9. Marcar carrito como procesado
        carrito.setEstado("PROCESADO");
        carritoRepository.save(carrito);
        
        logger.info("Venta procesada exitosamente - ID: {}, Total: {}", venta.getIdVenta(), venta.getTotal());
        
        return convertirADTO(venta);
    }
    
    @Override
    public Map<String, Object> generarComprobante(Long ventaId, String emailCliente) {
        logger.debug("Generando comprobante - Venta: {}, Cliente: {}", ventaId, emailCliente);
        
        Cliente cliente = clienteRepository.findByEmailAndActivo(emailCliente, true)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + emailCliente));
        
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + ventaId));
        
        // Validar que la venta pertenece al cliente
        if (!venta.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            throw new IllegalArgumentException("La venta no pertenece al cliente");
        }
        
        Map<String, Object> comprobante = new HashMap<>();
        comprobante.put("venta", convertirADTO(venta));
        comprobante.put("cliente", cliente);
        comprobante.put("fechaGeneracion", LocalDateTime.now());
        comprobante.put("numeroComprobante", "COMP-" + ventaId);
        
        return comprobante;
    }
    
    @Override
    @Transactional(readOnly = true)
    public VentaDTO buscarVentaPorId(Long ventaId) {
        logger.debug("Buscando venta por ID: {}", ventaId);
        
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + ventaId));
        
        return convertirADTO(venta);
    }
    
    private void actualizarStock(java.util.List<DetalleCarrito> detalles) {
        for (DetalleCarrito detalle : detalles) {
            if ("PRODUCTO".equalsIgnoreCase(detalle.getTipo())) {
                // Buscar el producto
                Producto producto = productoRepository.findById(detalle.getIdItem())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado: " + detalle.getIdItem()));
                
                // Verificar si hay suficiente stock
                if (producto.getStock() < detalle.getCantidad()) {
                    throw new IllegalStateException(
                        "Stock insuficiente para el producto: " + producto.getNombre() + 
                        ". Disponible: " + producto.getStock() + 
                        ", Solicitado: " + detalle.getCantidad());
                }
                
                // Actualizar el stock
                int nuevoStock = producto.getStock() - detalle.getCantidad();
                producto.setStock(nuevoStock);
                
                // NO desactivar el producto, solo actualizar stock
                // El producto seguirá visible pero mostrará "Sin stock"
                
                productoRepository.save(producto);
                
                logger.info("Stock actualizado - Producto: {} (ID: {}), Stock anterior: {}, Vendido: {}, Nuevo stock: {}", 
                           producto.getNombre(), 
                           producto.getIdProducto(),
                           producto.getStock() + detalle.getCantidad(),
                           detalle.getCantidad(), 
                           nuevoStock);
                
                if (nuevoStock <= 0) {
                    logger.warn("Producto '{}' (ID: {}) sin stock disponible", 
                               producto.getNombre(), producto.getIdProducto());
                }
            }
        }
    }
    
    private VentaDTO convertirADTO(Venta venta) {
        VentaDTO dto = modelMapper.map(venta, VentaDTO.class);
        
        if (venta.getCliente() != null) {
            dto.setNombreCliente(venta.getCliente().getNombreCliente());
            dto.setEmailCliente(venta.getCliente().getEmail());
        }
        
        return dto;
    }
}