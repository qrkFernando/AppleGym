package com.applegym.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para Carrito de compras.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class CarritoDTO {
    
    private Long idCarrito;
    
    @NotNull
    private Long idCliente;
    
    private String nombreCliente;
    
    private LocalDateTime fecha;
    
    private List<DetalleCarritoDTO> detalles;
    
    private BigDecimal total;
    
    private String estado;
    
    // Constructores
    public CarritoDTO() {
        this.fecha = LocalDateTime.now();
        this.estado = "ACTIVO";
    }
    
    public CarritoDTO(Long idCliente) {
        this();
        this.idCliente = idCliente;
    }
    
    // Getters y Setters
    public Long getIdCarrito() {
        return idCarrito;
    }
    
    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }
    
    public Long getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public List<DetalleCarritoDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleCarritoDTO> detalles) {
        this.detalles = detalles;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}