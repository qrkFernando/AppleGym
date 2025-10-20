package com.applegym.service.impl;

import com.applegym.entity.*;
import com.applegym.service.PdfService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de generación de PDFs.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
public class PdfServiceImpl implements PdfService {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DeviceRgb COLOR_PRIMARY = new DeviceRgb(66, 148, 76); // #42944C
    
    @Override
    public ByteArrayOutputStream generarComprobantePDF(Venta venta) {
        logger.info("Generando PDF para venta ID: {}", venta.getIdVenta());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Encabezado
            agregarEncabezado(document, venta);
            
            // Información del cliente
            agregarInformacionCliente(document, venta.getCliente());
            
            // Información de la venta
            agregarInformacionVenta(document, venta);
            
            // Tabla de productos/servicios
            agregarTablaDetalles(document, venta);
            
            // Totales
            agregarTotales(document, venta);
            
            // Información del pago
            if (venta.getPago() != null) {
                agregarInformacionPago(document, venta.getPago());
            }
            
            // Pie de página
            agregarPiePagina(document, venta);
            
            document.close();
            logger.info("PDF generado exitosamente para venta ID: {}", venta.getIdVenta());
            
        } catch (Exception e) {
            logger.error("Error generando PDF para venta ID: {}", venta.getIdVenta(), e);
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
        
        return baos;
    }
    
    private void agregarEncabezado(Document document, Venta venta) {
        // Título principal
        Paragraph titulo = new Paragraph("APPLEGYM")
            .setFontSize(24)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);
        
        Paragraph subtitulo = new Paragraph("Comprobante de Venta")
            .setFontSize(16)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);
        document.add(subtitulo);
        
        // Número de comprobante
        String numeroComprobante = venta.getComprobante() != null ? 
            venta.getComprobante().getNumeroComprobante() : 
            "COMP-" + venta.getIdVenta();
            
        Paragraph numero = new Paragraph("Nº " + numeroComprobante)
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(numero);
    }
    
    private void agregarInformacionCliente(Document document, Cliente cliente) {
        document.add(new Paragraph("DATOS DEL CLIENTE")
            .setFontSize(12)
            .setBold()
            .setMarginTop(10)
            .setMarginBottom(5));
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaInfo(table, "Nombre:", cliente.getNombreCliente());
        agregarFilaInfo(table, "Email:", cliente.getEmail());
        
        if (cliente.getTelefono() != null) {
            agregarFilaInfo(table, "Teléfono:", cliente.getTelefono());
        }
        if (cliente.getDireccion() != null) {
            agregarFilaInfo(table, "Dirección:", cliente.getDireccion());
        }
        
        document.add(table);
    }
    
    private void agregarInformacionVenta(Document document, Venta venta) {
        document.add(new Paragraph("DATOS DE LA VENTA")
            .setFontSize(12)
            .setBold()
            .setMarginTop(15)
            .setMarginBottom(5));
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaInfo(table, "Número de Venta:", venta.getNumeroVenta());
        agregarFilaInfo(table, "Fecha:", venta.getFechaVenta().format(DATE_FORMATTER));
        agregarFilaInfo(table, "Estado:", venta.getEstado());
        
        document.add(table);
    }
    
    private void agregarTablaDetalles(Document document, Venta venta) {
        document.add(new Paragraph("DETALLE DE PRODUCTOS/SERVICIOS")
            .setFontSize(12)
            .setBold()
            .setMarginTop(15)
            .setMarginBottom(5));
        
        Table table = new Table(new float[]{3, 1, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        agregarCeldaEncabezado(table, "Descripción");
        agregarCeldaEncabezado(table, "Cant.");
        agregarCeldaEncabezado(table, "Precio Unit.");
        agregarCeldaEncabezado(table, "Subtotal");
        
        // Detalles
        for (DetalleVenta detalle : venta.getDetalles()) {
            table.addCell(new Cell().add(new Paragraph(detalle.getNombre())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad())))
                .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("$" + detalle.getPrecioUnitario().toString()))
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph("$" + detalle.getSubtotal().toString()))
                .setTextAlignment(TextAlignment.RIGHT));
        }
        
        document.add(table);
    }
    
    private void agregarTotales(Document document, Venta venta) {
        Table table = new Table(new float[]{3, 1});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginTop(10);
        
        // Subtotal
        BigDecimal subtotal = venta.getDetalles().stream()
            .map(DetalleVenta::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Cell labelCell = new Cell().add(new Paragraph("Subtotal:"))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(null);
        Cell valueCell = new Cell().add(new Paragraph("$" + subtotal.toString()))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(null);
        table.addCell(labelCell);
        table.addCell(valueCell);
        
        // Total
        labelCell = new Cell().add(new Paragraph("TOTAL:"))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBold()
            .setFontSize(14)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        valueCell = new Cell().add(new Paragraph("$" + venta.getTotal().toString()))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBold()
            .setFontSize(14)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addCell(labelCell);
        table.addCell(valueCell);
        
        document.add(table);
    }
    
    private void agregarInformacionPago(Document document, Pago pago) {
        document.add(new Paragraph("INFORMACIÓN DEL PAGO")
            .setFontSize(12)
            .setBold()
            .setMarginTop(15)
            .setMarginBottom(5));
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaInfo(table, "Método de Pago:", formatearTipoPago(pago.getTipoPago()));
        agregarFilaInfo(table, "Estado:", pago.getEstadoPago());
        agregarFilaInfo(table, "Nº Transacción:", pago.getNumeroTransaccion());
        agregarFilaInfo(table, "Fecha de Pago:", pago.getFechaPago().format(DATE_FORMATTER));
        
        if (pago.getCodigoAutorizacion() != null) {
            agregarFilaInfo(table, "Código de Autorización:", pago.getCodigoAutorizacion());
        }
        
        document.add(table);
    }
    
    private void agregarPiePagina(Document document, Venta venta) {
        document.add(new Paragraph("\n"));
        
        Paragraph agradecimiento = new Paragraph("¡Gracias por su compra!")
            .setFontSize(12)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20);
        document.add(agradecimiento);
        
        Paragraph info = new Paragraph("AppleGym - Tu gimnasio digital de confianza")
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY);
        document.add(info);
        
        Paragraph contacto = new Paragraph("info@applegym.com | www.applegym.com")
            .setFontSize(9)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY);
        document.add(contacto);
    }
    
    private void agregarFilaInfo(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold())
            .setBorder(null));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "N/A"))
            .setBorder(null));
    }
    
    private void agregarCeldaEncabezado(Table table, String texto) {
        Cell cell = new Cell().add(new Paragraph(texto).setBold())
            .setBackgroundColor(COLOR_PRIMARY)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }
    
    private String formatearTipoPago(String tipoPago) {
        return switch (tipoPago) {
            case "TARJETA_CREDITO" -> "Tarjeta de Crédito";
            case "TARJETA_DEBITO" -> "Tarjeta de Débito";
            case "EFECTIVO" -> "Efectivo";
            case "TRANSFERENCIA" -> "Transferencia Bancaria";
            case "PAYPAL" -> "PayPal";
            case "MERCADO_PAGO" -> "Mercado Pago";
            case "YAPE" -> "Yape";
            case "PLIN" -> "Plin";
            default -> tipoPago;
        };
    }
}
