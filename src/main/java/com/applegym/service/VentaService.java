package com.applegym.service;

import com.applegym.dto.VentaDTO;
import java.util.Map;

/**
 * Interfaz de servicio para la gestión de Ventas.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public interface VentaService {
    
    VentaDTO procesarVenta(String emailCliente, String metodoPago);
    
    Map<String, Object> generarComprobante(Long ventaId, String emailCliente);
    
    VentaDTO buscarVentaPorId(Long ventaId);
}