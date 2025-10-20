package com.applegym.service;

import com.applegym.entity.Venta;

import java.io.ByteArrayOutputStream;

/**
 * Servicio para generación de documentos PDF.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public interface PdfService {
    
    /**
     * Genera un comprobante de venta en formato PDF.
     * 
     * @param venta La venta para generar el comprobante
     * @return ByteArrayOutputStream con el PDF generado
     */
    ByteArrayOutputStream generarComprobantePDF(Venta venta);
}
