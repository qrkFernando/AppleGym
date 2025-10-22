package com.applegym.controller;

import com.applegym.dto.ResumenReporteDTO;
import com.applegym.dto.TopItemDTO;
import com.applegym.dto.VentasPorFechaDTO;
import com.applegym.service.ReporteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para reportes y estadísticas del dashboard de administrador.
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReporteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);
    
    @Autowired
    private ReporteService reporteService;
    
    /**
     * Obtiene el resumen general para el dashboard.
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen() {
        try {
            logger.debug("Obteniendo resumen general del dashboard");
            
            ResumenReporteDTO resumen = reporteService.obtenerResumenGeneral();
            
            return ResponseEntity.ok(resumen);
            
        } catch (Exception e) {
            logger.error("Error obteniendo resumen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtiene los productos más vendidos.
     */
    @GetMapping("/productos-top")
    public ResponseEntity<?> obtenerTopProductos(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.debug("Obteniendo top {} productos", limit);
            
            List<TopItemDTO> topProductos = reporteService.obtenerProductosMasVendidos(limit);
            
            return ResponseEntity.ok(topProductos);
            
        } catch (Exception e) {
            logger.error("Error obteniendo top productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtiene los servicios más solicitados.
     */
    @GetMapping("/servicios-top")
    public ResponseEntity<?> obtenerTopServicios(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.debug("Obteniendo top {} servicios", limit);
            
            List<TopItemDTO> topServicios = reporteService.obtenerServiciosMasSolicitados(limit);
            
            return ResponseEntity.ok(topServicios);
            
        } catch (Exception e) {
            logger.error("Error obteniendo top servicios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtiene ventas por fecha en un rango.
     */
    @GetMapping("/ventas-por-fecha")
    public ResponseEntity<?> obtenerVentasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            logger.debug("Obteniendo ventas por fecha - Inicio: {}, Fin: {}", fechaInicio, fechaFin);
            
            List<VentasPorFechaDTO> ventas = reporteService.obtenerVentasPorFecha(fechaInicio, fechaFin);
            
            return ResponseEntity.ok(ventas);
            
        } catch (Exception e) {
            logger.error("Error obteniendo ventas por fecha: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Exporta reporte a Excel.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<?> exportarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            logger.debug("Exportando reporte a Excel");
            
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now().minusDays(30);
            }
            if (fechaFin == null) {
                fechaFin = LocalDate.now();
            }
            
            byte[] excelBytes = reporteService.exportarReporteExcel(fechaInicio, fechaFin);
            
            String filename = String.format("Reporte_AppleGym_%s_a_%s.xlsx", 
                                          fechaInicio.toString(), 
                                          fechaFin.toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelBytes.length);
            
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exportando a Excel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Exporta reporte a PDF.
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<?> exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            logger.debug("Exportando reporte a PDF");
            
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now().minusDays(30);
            }
            if (fechaFin == null) {
                fechaFin = LocalDate.now();
            }
            
            byte[] pdfBytes = reporteService.exportarReportePDF(fechaInicio, fechaFin);
            
            String filename = String.format("Reporte_AppleGym_%s_a_%s.pdf", 
                                          fechaInicio.toString(), 
                                          fechaFin.toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exportando a PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
