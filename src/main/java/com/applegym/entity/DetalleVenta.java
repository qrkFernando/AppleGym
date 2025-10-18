package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa el detalle de una venta en AppleGym.
 * 
 * Contiene información específica de cada producto o servicio vendido,
 * incluyendo cantidades, precios y subtotales.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "detalle_venta", indexes = {
    @Index(name = "idx_detalle_venta_venta", columnList = "id_venta"),
    @Index(name = "idx_detalle_venta_producto", columnList = "id_producto"),
    @Index(name = "idx_detalle_venta_servicio", columnList = "id_servicio")
})
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;
    
    @NotNull(message = "La venta es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    // ID genérico del producto o servicio
    @NotNull(message = "El ID del producto/servicio es obligatorio")
    @Column(name = "id_producto_servicio", nullable = false)
    private Long idProductoServicio;
    
    // Tipo de item: PRODUCTO o SERVICIO
    @Column(name = "tipo", length = 50)
    private String tipo;
    
    // Nombre del item para referencia
    @Column(name = "nombre", length = 255)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @NotNull(message = "El subtotal es obligatorio")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public DetalleVenta() {
        this.fechaCreacion = LocalDateTime.now();
        this.descuentoAplicado = BigDecimal.ZERO;
    }
    
    public DetalleVenta(Venta venta, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public DetalleVenta(Venta venta, Servicio servicio, Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.venta = venta;
        this.servicio = servicio;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    // Métodos de ciclo de vida JPA
    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad != null) {
            BigDecimal subtotalBase = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
            BigDecimal descuento = this.descuentoAplicado != null ? this.descuentoAplicado : BigDecimal.ZERO;
            this.subtotal = subtotalBase.subtract(descuento);
            
            // Asegurar que el subtotal no sea negativo
            if (this.subtotal.compareTo(BigDecimal.ZERO) < 0) {
                this.subtotal = BigDecimal.ZERO;
            }
        }
    }
    
    // Métodos de negocio
    public String getNombreItem() {
        if (producto != null) {
            return producto.getNombre();
        } else if (servicio != null) {
            return servicio.getNombre();
        }
        return "Item desconocido";
    }
    
    public String getTipoItem() {
        if (producto != null) {
            return "PRODUCTO";
        } else if (servicio != null) {
            return "SERVICIO";
        }
        return "DESCONOCIDO";
    }
    
    public boolean esProducto() {
        return producto != null;
    }
    
    public boolean esServicio() {
        return servicio != null;
    }
    
    public void aplicarDescuento(BigDecimal descuento) {
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) >= 0) {
            this.descuentoAplicado = descuento;
            calcularSubtotal();
        }
    }
    
    public BigDecimal getPorcentajeDescuento() {
        if (descuentoAplicado.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal subtotalBase = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            return descuentoAplicado.divide(subtotalBase, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    // Getters y Setters
    public Long getIdDetalle() {
        return idDetalle;
    }
    
    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }
    
    public Venta getVenta() {
        return venta;
    }
    
    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    
    public Long getIdProductoServicio() {
        return idProductoServicio;
    }
    
    public void setIdProductoServicio(Long idProductoServicio) {
        this.idProductoServicio = idProductoServicio;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Servicio getServicio() {
        return servicio;
    }
    
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }
    
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
        calcularSubtotal();
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta that = (DetalleVenta) o;
        return Objects.equals(idDetalle, that.idDetalle);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idDetalle);
    }
    
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "idDetalle=" + idDetalle +
                ", nombreItem='" + getNombreItem() + '\'' +
                ", tipoItem='" + getTipoItem() + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", descuentoAplicado=" + descuentoAplicado +
                '}';
    }
}