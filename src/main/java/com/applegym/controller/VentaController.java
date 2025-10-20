package com.applegym.controller;

import com.applegym.dto.VentaDTO;
import com.applegym.service.VentaService;
import com.applegym.service.PdfService;
import com.applegym.security.JwtTokenProvider;
import com.applegym.entity.Venta;
import com.applegym.repository.VentaRepository;
import com.applegym.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Controlador para la gestión de ventas y pagos.
 * 
 * Implementa los casos de uso CU06-CU09 (Confirmar carrito, pago y comprobante).
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    
    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    /**
     * CU08 - Confirmar pago
     */
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarVenta(@Valid @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            String metodoPago = request.get("metodoPago").toString();
            
            // Validar método de pago
            if (!isValidPaymentMethod(metodoPago)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Método de pago inválido",
                    "metodosPermitidos", new String[]{"TARJETA_CREDITO", "TARJETA_DEBITO", "EFECTIVO", "TRANSFERENCIA"}
                ));
            }
            
            VentaDTO venta = ventaService.procesarVenta(email, metodoPago);
            
            logger.info("Venta procesada exitosamente - ID: {}, Cliente: {}", venta.getIdVenta(), email);
            
            return ResponseEntity.ok(Map.of(
                "message", "Venta procesada exitosamente",
                "venta", venta,
                "redirectTo", "/comprobante/" + venta.getIdVenta()
            ));
            
        } catch (Exception e) {
            logger.error("Error procesando venta: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * CU09 - Recibir comprobante digital
     */
    @GetMapping("/comprobante/{ventaId}")
    public ResponseEntity<?> obtenerComprobante(@PathVariable Long ventaId, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            
            Map<String, Object> comprobante = ventaService.generarComprobante(ventaId, email);
            
            return ResponseEntity.ok(comprobante);
            
        } catch (Exception e) {
            logger.error("Error obteniendo comprobante: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Descargar comprobante en PDF
     */
    @GetMapping("/comprobante/{ventaId}/pdf")
    @Transactional(readOnly = true)
    public ResponseEntity<?> descargarComprobantePDF(@PathVariable Long ventaId, HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token requerido para descargar el comprobante"));
            }
            
            String email = tokenProvider.getUsernameFromToken(token);
            
            // Buscar la venta y validar que pertenece al cliente
            Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
            
            if (!venta.getCliente().getEmail().equals(email)) {
                return ResponseEntity.status(403).body(Map.of("error", "No tiene permisos para acceder a este comprobante"));
            }
            
            // Generar PDF
            ByteArrayOutputStream pdfStream = pdfService.generarComprobantePDF(venta);
            
            // Configurar headers para descarga
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = String.format("Comprobante_%s.pdf", 
                venta.getNumeroVenta() != null ? venta.getNumeroVenta() : "VT" + venta.getIdVenta());
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            logger.info("PDF generado para venta ID: {}, Cliente: {}", ventaId, email);
            
            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (ResourceNotFoundException e) {
            logger.error("Venta no encontrada: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error generando PDF para venta ID {}: {}", ventaId, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error al generar el PDF: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener métodos de pago disponibles
     */
    @GetMapping("/metodos-pago")
    public ResponseEntity<?> obtenerMetodosPago() {
        return ResponseEntity.ok(Map.of(
            "metodosDisponibles", new Object[]{
                Map.of("id", "TARJETA_CREDITO", "nombre", "Tarjeta de Crédito", "icon", "fas fa-credit-card"),
                Map.of("id", "TARJETA_DEBITO", "nombre", "Tarjeta de Débito", "icon", "fas fa-credit-card")
            }
        ));
    }
    
    private boolean isValidPaymentMethod(String metodo) {
        return metodo != null && (
            metodo.equals("TARJETA_CREDITO") ||
            metodo.equals("TARJETA_DEBITO")
        );
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}