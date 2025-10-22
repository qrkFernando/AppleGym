package com.applegym.dto;

import java.math.BigDecimal;

/**
 * DTO para productos/servicios más vendidos.
 */
public class TopItemDTO {
    
    private Long idItem;
    private String nombre;
    private String tipo;
    private Long cantidadVendida;
    private BigDecimal totalVentas;
    private String categoria;
    
    public TopItemDTO() {}
    
    public TopItemDTO(Long idItem, String nombre, String tipo, Long cantidadVendida, BigDecimal totalVentas) {
        this.idItem = idItem;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidadVendida = cantidadVendida;
        this.totalVentas = totalVentas;
    }
    
    // Getters y Setters
    public Long getIdItem() {
        return idItem;
    }
    
    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Long getCantidadVendida() {
        return cantidadVendida;
    }
    
    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
    
    public BigDecimal getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
