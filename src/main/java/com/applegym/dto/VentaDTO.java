package com.applegym.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para Venta.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class VentaDTO {
    
    private Long idVenta;
    
    @NotNull
    private Long idCliente;
    
    private String nombreCliente;
    
    private String emailCliente;
    
    private LocalDateTime fechaVenta;
    
    @NotNull
    private BigDecimal total;
    
    private String estado;
    
    private List<DetalleVentaDTO> detalles;
    
    private PagoDTO pago;
    
    private ComprobanteDTO comprobante;
    
    // Constructores
    public VentaDTO() {
        this.fechaVenta = LocalDateTime.now();
        this.estado = "PROCESANDO";
    }
    
    public VentaDTO(Long idCliente, BigDecimal total) {
        this();
        this.idCliente = idCliente;
        this.total = total;
    }
    
    // Getters y Setters
    public Long getIdVenta() {
        return idVenta;
    }
    
    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
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
    
    public String getEmailCliente() {
        return emailCliente;
    }
    
    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
    
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
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
    
    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
    
    public PagoDTO getPago() {
        return pago;
    }
    
    public void setPago(PagoDTO pago) {
        this.pago = pago;
    }
    
    public ComprobanteDTO getComprobante() {
        return comprobante;
    }
    
    public void setComprobante(ComprobanteDTO comprobante) {
        this.comprobante = comprobante;
    }
}

/**
 * DTO para Detalle de Venta.
 */
class DetalleVentaDTO {
    private Long idDetalle;
    private Long idVenta;
    private Long idProductoServicio;
    private String tipo;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    // Getters y Setters
    public Long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Long idDetalle) { this.idDetalle = idDetalle; }
    
    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }
    
    public Long getIdProductoServicio() { return idProductoServicio; }
    public void setIdProductoServicio(Long idProductoServicio) { this.idProductoServicio = idProductoServicio; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}

/**
 * DTO para Pago.
 */
class PagoDTO {
    private Long idPago;
    private Long idVenta;
    private String tipoPago;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String estadoPago;
    
    // Getters y Setters
    public Long getIdPago() { return idPago; }
    public void setIdPago(Long idPago) { this.idPago = idPago; }
    
    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }
    
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}

/**
 * DTO para Comprobante.
 */
class ComprobanteDTO {
    private Long idComprobante;
    private Long idVenta;
    private LocalDateTime fechaEmision;
    private String archivoComprobante;
    private String tipoComprobante;
    
    // Getters y Setters
    public Long getIdComprobante() { return idComprobante; }
    public void setIdComprobante(Long idComprobante) { this.idComprobante = idComprobante; }
    
    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }
    
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    
    public String getArchivoComprobante() { return archivoComprobante; }
    public void setArchivoComprobante(String archivoComprobante) { this.archivoComprobante = archivoComprobante; }
    
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
}