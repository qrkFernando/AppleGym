package com.applegym.service;

import com.applegym.dto.CarritoDTO;

/**
 * Interfaz de servicio para la gestión de Carritos de compra.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public interface CarritoService {
    
    CarritoDTO obtenerCarritoActual(String emailCliente);
    
    CarritoDTO agregarItem(String emailCliente, Long itemId, String tipo, Integer cantidad);
    
    void eliminarItem(String emailCliente, Long itemId);
    
    CarritoDTO actualizarCantidad(String emailCliente, Long itemId, Integer nuevaCantidad);
    
    void limpiarCarrito(String emailCliente);
}