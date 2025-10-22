package com.applegym.service;

import com.applegym.dto.ResumenReporteDTO;
import com.applegym.dto.TopItemDTO;
import com.applegym.dto.VentasPorFechaDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para generación de reportes y estadísticas.
 */
public interface ReporteService {
    
    /**
     * Obtiene el resumen general del dashboard.
     */
    ResumenReporteDTO obtenerResumenGeneral();
    
    /**
     * Obtiene los productos más vendidos.
     */
    List<TopItemDTO> obtenerProductosMasVendidos(int limit);
    
    /**
     * Obtiene los servicios más solicitados.
     */
    List<TopItemDTO> obtenerServiciosMasSolicitados(int limit);
    
    /**
     * Obtiene las ventas por fecha en un rango.
     */
    List<VentasPorFechaDTO> obtenerVentasPorFecha(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Exporta reportes a Excel.
     */
    byte[] exportarReporteExcel(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Exporta reportes a PDF.
     */
    byte[] exportarReportePDF(LocalDate fechaInicio, LocalDate fechaFin);
}
