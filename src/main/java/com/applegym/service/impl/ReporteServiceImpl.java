package com.applegym.service.impl;

import com.applegym.dto.ResumenReporteDTO;
import com.applegym.dto.TopItemDTO;
import com.applegym.dto.VentasPorFechaDTO;
import com.applegym.repository.*;
import com.applegym.service.ReporteService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de reportes.
 */
@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReporteServiceImpl.class);
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Override
    public ResumenReporteDTO obtenerResumenGeneral() {
        logger.debug("Generando resumen general de reportes");
        
        ResumenReporteDTO resumen = new ResumenReporteDTO();
        
        // Total de ventas (monto)
        BigDecimal ventasTotales = ventaRepository.calcularVentasTotales();
        resumen.setVentasTotales(ventasTotales != null ? ventasTotales : BigDecimal.ZERO);
        
        // Cantidad de ventas
        Long totalVentas = ventaRepository.count();
        resumen.setTotalVentas(totalVentas);
        
        // Total de clientes activos
        Long totalClientes = clienteRepository.countByActivo(true);
        resumen.setTotalClientes(totalClientes);
        
        // Total de productos activos
        Long totalProductos = productoRepository.countByActivo(true);
        resumen.setTotalProductos(totalProductos);
        
        // Total de servicios activos
        Long totalServicios = servicioRepository.countByActivo(true);
        resumen.setTotalServicios(totalServicios);
        
        // Ventas de hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().plusDays(1).atStartOfDay();
        BigDecimal ventasHoy = ventaRepository.calcularVentasPorRangoFecha(inicioHoy, finHoy);
        resumen.setVentasHoy(ventasHoy != null ? ventasHoy : BigDecimal.ZERO);
        
        // Ventas de la semana
        LocalDateTime inicioSemana = LocalDate.now().minusDays(7).atStartOfDay();
        BigDecimal ventasSemana = ventaRepository.calcularVentasPorRangoFecha(inicioSemana, LocalDateTime.now());
        resumen.setVentasSemana(ventasSemana != null ? ventasSemana : BigDecimal.ZERO);
        
        // Ventas del mes
        LocalDateTime inicioMes = LocalDate.now().minusDays(30).atStartOfDay();
        BigDecimal ventasMes = ventaRepository.calcularVentasPorRangoFecha(inicioMes, LocalDateTime.now());
        resumen.setVentasMes(ventasMes != null ? ventasMes : BigDecimal.ZERO);
        
        // Fecha de última venta
        resumen.setFechaUltimaVenta(ventaRepository.findFechaUltimaVenta());
        
        logger.info("Resumen generado - Ventas totales: {}, Total ventas: {}", 
                   resumen.getVentasTotales(), resumen.getTotalVentas());
        
        return resumen;
    }
    
    @Override
    public List<TopItemDTO> obtenerProductosMasVendidos(int limit) {
        logger.debug("Obteniendo top {} productos más vendidos", limit);
        
        List<Object[]> resultados = ventaRepository.findTopProductosMasVendidos(limit);
        List<TopItemDTO> topProductos = new ArrayList<>();
        
        for (Object[] row : resultados) {
            TopItemDTO dto = new TopItemDTO();
            dto.setIdItem(((Number) row[0]).longValue());
            dto.setNombre((String) row[1]);
            dto.setTipo("PRODUCTO");
            dto.setCantidadVendida(((Number) row[2]).longValue());
            dto.setTotalVentas((BigDecimal) row[3]);
            
            topProductos.add(dto);
        }
        
        return topProductos;
    }
    
    @Override
    public List<TopItemDTO> obtenerServiciosMasSolicitados(int limit) {
        logger.debug("Obteniendo top {} servicios más solicitados", limit);
        
        List<Object[]> resultados = ventaRepository.findTopServiciosMasSolicitados(limit);
        List<TopItemDTO> topServicios = new ArrayList<>();
        
        for (Object[]row : resultados) {
            TopItemDTO dto = new TopItemDTO();
            dto.setIdItem(((Number) row[0]).longValue());
            dto.setNombre((String) row[1]);
            dto.setTipo("SERVICIO");
            dto.setCantidadVendida(((Number) row[2]).longValue());
            dto.setTotalVentas((BigDecimal) row[3]);
            
            topServicios.add(dto);
        }
        
        return topServicios;
    }
    
    @Override
    public List<VentasPorFechaDTO> obtenerVentasPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        logger.debug("Obteniendo ventas por fecha - Inicio: {}, Fin: {}", fechaInicio, fechaFin);
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();
        
        List<Object[]> resultados = ventaRepository.findVentasPorFecha(inicio, fin);
        List<VentasPorFechaDTO> ventasPorFecha = new ArrayList<>();
        
        for (Object[] row : resultados) {
            VentasPorFechaDTO dto = new VentasPorFechaDTO();
            dto.setFecha(((java.sql.Date) row[0]).toLocalDate());
            dto.setCantidadVentas(((Number) row[1]).longValue());
            dto.setTotalVentas((BigDecimal) row[2]);
            
            ventasPorFecha.add(dto);
        }
        
        return ventasPorFecha;
    }
    
    @Override
    public byte[] exportarReporteExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        logger.debug("Exportando reporte a Excel - Inicio: {}, Fin: {}", fechaInicio, fechaFin);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Hoja 1: Resumen General
            Sheet resumenSheet = workbook.createSheet("Resumen General");
            crearHojaResumen(resumenSheet, workbook);
            
            // Hoja 2: Productos Más Vendidos
            Sheet productosSheet = workbook.createSheet("Top Productos");
            crearHojaTopProductos(productosSheet, workbook);
            
            // Hoja 3: Servicios Más Solicitados
            Sheet serviciosSheet = workbook.createSheet("Top Servicios");
            crearHojaTopServicios(serviciosSheet, workbook);
            
            // Hoja 4: Ventas por Fecha
            Sheet ventasSheet = workbook.createSheet("Ventas por Fecha");
            crearHojaVentasPorFecha(ventasSheet, workbook, fechaInicio, fechaFin);
            
            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            logger.info("Reporte Excel generado exitosamente");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generando reporte Excel: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }
    
    @Override
    public byte[] exportarReportePDF(LocalDate fechaInicio, LocalDate fechaFin) {
        logger.debug("Exportando reporte a PDF - Inicio: {}, Fin: {}", fechaInicio, fechaFin);
        
        // TODO: Implementar exportación a PDF con iText
        throw new UnsupportedOperationException("Exportación a PDF en desarrollo");
    }
    
    // Métodos privados para crear hojas de Excel
    
    private void crearHojaResumen(Sheet sheet, Workbook workbook) {
        ResumenReporteDTO resumen = obtenerResumenGeneral();
        
        // Estilo de encabezado
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESUMEN GENERAL DE VENTAS - APPLEGYM");
        titleCell.setCellStyle(headerStyle);
        
        rowNum++; // Línea en blanco
        
        // Datos
        crearFilaDato(sheet, rowNum++, "Ventas Totales", "$" + resumen.getVentasTotales(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Cantidad de Ventas", resumen.getTotalVentas().toString(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Total Clientes", resumen.getTotalClientes().toString(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Total Productos", resumen.getTotalProductos().toString(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Total Servicios", resumen.getTotalServicios().toString(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Ventas Hoy", "$" + resumen.getVentasHoy(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Ventas Última Semana", "$" + resumen.getVentasSemana(), dataStyle);
        crearFilaDato(sheet, rowNum++, "Ventas Último Mes", "$" + resumen.getVentasMes(), dataStyle);
        
        // Ajustar ancho de columnas
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 6000);
    }
    
    private void crearHojaTopProductos(Sheet sheet, Workbook workbook) {
        List<TopItemDTO> topProductos = obtenerProductosMasVendidos(10);
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Posición", "Producto", "Cantidad Vendida", "Total Ventas"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        int posicion = 1;
        for (TopItemDTO item : topProductos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(posicion++);
            row.createCell(1).setCellValue(item.getNombre());
            row.createCell(2).setCellValue(item.getCantidadVendida());
            row.createCell(3).setCellValue("$" + item.getTotalVentas());
            
            for (int i = 0; i < 4; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Ajustar anchos
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void crearHojaTopServicios(Sheet sheet, Workbook workbook) {
        List<TopItemDTO> topServicios = obtenerServiciosMasSolicitados(10);
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Posición", "Servicio", "Cantidad Solicitada", "Total Ventas"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        int posicion = 1;
        for (TopItemDTO item : topServicios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(posicion++);
            row.createCell(1).setCellValue(item.getNombre());
            row.createCell(2).setCellValue(item.getCantidadVendida());
            row.createCell(3).setCellValue("$" + item.getTotalVentas());
            
            for (int i = 0; i < 4; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Ajustar anchos
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void crearHojaVentasPorFecha(Sheet sheet, Workbook workbook, LocalDate fechaInicio, LocalDate fechaFin) {
        List<VentasPorFechaDTO> ventas = obtenerVentasPorFecha(fechaInicio, fechaFin);
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Fecha", "Cantidad de Ventas", "Total Ventas"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        for (VentasPorFechaDTO venta : ventas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(venta.getFecha().toString());
            row.createCell(1).setCellValue(venta.getCantidadVentas());
            row.createCell(2).setCellValue("$" + venta.getTotalVentas());
            
            for (int i = 0; i < 3; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Ajustar anchos
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void crearFilaDato(Sheet sheet, int rowNum, String label, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(style);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
