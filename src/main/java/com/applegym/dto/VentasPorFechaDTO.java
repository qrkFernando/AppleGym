package com.applegym.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para ventas por fecha.
 */
public class VentasPorFechaDTO {
    
    private LocalDate fecha;
    private Long cantidadVentas;
    private BigDecimal totalVentas;
    
    public VentasPorFechaDTO() {}
    
    public VentasPorFechaDTO(LocalDate fecha, Long cantidadVentas, BigDecimal totalVentas) {
        this.fecha = fecha;
        this.cantidadVentas = cantidadVentas;
        this.totalVentas = totalVentas;
    }
    
    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public Long getCantidadVentas() {
        return cantidadVentas;
    }
    
    public void setCantidadVentas(Long cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }
    
    public BigDecimal getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }
}
