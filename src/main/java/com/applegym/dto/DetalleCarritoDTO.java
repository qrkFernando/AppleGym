package com.applegym.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO para Detalle del Carrito.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class DetalleCarritoDTO {
    
    private Long idDetalleCarrito;
    
    @NotNull
    private Long idCarrito;
    
    @NotNull
    private String tipo; // "PRODUCTO" o "SERVICIO"
    
    @NotNull
    private Long idItem; // ID del producto o servicio
    
    private String nombreItem;
    
    private String descripcionItem;
    
    private BigDecimal precioUnitario;
    
    @NotNull
    @Positive
    private Integer cantidad;
    
    private BigDecimal subtotal;
    
    private Long idPromocion;
    
    private String nombrePromocion;
    
    private BigDecimal descuento;
    
    // Constructores
    public DetalleCarritoDTO() {}
    
    public DetalleCarritoDTO(Long idCarrito, String tipo, Long idItem, Integer cantidad, BigDecimal precioUnitario) {
        this.idCarrito = idCarrito;
        this.tipo = tipo;
        this.idItem = idItem;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    // Getters y Setters
    public Long getIdDetalleCarrito() {
        return idDetalleCarrito;
    }
    
    public void setIdDetalleCarrito(Long idDetalleCarrito) {
        this.idDetalleCarrito = idDetalleCarrito;
    }
    
    public Long getIdCarrito() {
        return idCarrito;
    }
    
    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Long getIdItem() {
        return idItem;
    }
    
    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }
    
    public String getNombreItem() {
        return nombreItem;
    }
    
    public void setNombreItem(String nombreItem) {
        this.nombreItem = nombreItem;
    }
    
    public String getDescripcionItem() {
        return descripcionItem;
    }
    
    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        if (this.cantidad != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
        }
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        if (this.precioUnitario != null) {
            this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Long getIdPromocion() {
        return idPromocion;
    }
    
    public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }
    
    public String getNombrePromocion() {
        return nombrePromocion;
    }
    
    public void setNombrePromocion(String nombrePromocion) {
        this.nombrePromocion = nombrePromocion;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
}