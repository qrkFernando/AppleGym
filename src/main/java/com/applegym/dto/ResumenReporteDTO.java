package com.applegym.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resumen de reportes del dashboard.
 */
public class ResumenReporteDTO {
    
    private BigDecimal ventasTotales;
    private Long totalVentas;
    private Long totalClientes;
    private Long totalProductos;
    private Long totalServicios;
    private BigDecimal ventasHoy;
    private BigDecimal ventasSemana;
    private BigDecimal ventasMes;
    private LocalDateTime fechaUltimaVenta;
    
    public ResumenReporteDTO() {}
    
    // Getters y Setters
    public BigDecimal getVentasTotales() {
        return ventasTotales;
    }
    
    public void setVentasTotales(BigDecimal ventasTotales) {
        this.ventasTotales = ventasTotales;
    }
    
    public Long getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(Long totalVentas) {
        this.totalVentas = totalVentas;
    }
    
    public Long getTotalClientes() {
        return totalClientes;
    }
    
    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }
    
    public Long getTotalProductos() {
        return totalProductos;
    }
    
    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }
    
    public Long getTotalServicios() {
        return totalServicios;
    }
    
    public void setTotalServicios(Long totalServicios) {
        this.totalServicios = totalServicios;
    }
    
    public BigDecimal getVentasHoy() {
        return ventasHoy;
    }
    
    public void setVentasHoy(BigDecimal ventasHoy) {
        this.ventasHoy = ventasHoy;
    }
    
    public BigDecimal getVentasSemana() {
        return ventasSemana;
    }
    
    public void setVentasSemana(BigDecimal ventasSemana) {
        this.ventasSemana = ventasSemana;
    }
    
    public BigDecimal getVentasMes() {
        return ventasMes;
    }
    
    public void setVentasMes(BigDecimal ventasMes) {
        this.ventasMes = ventasMes;
    }
    
    public LocalDateTime getFechaUltimaVenta() {
        return fechaUltimaVenta;
    }
    
    public void setFechaUltimaVenta(LocalDateTime fechaUltimaVenta) {
        this.fechaUltimaVenta = fechaUltimaVenta;
    }
}
