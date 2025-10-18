package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa el detalle de un carrito de compras en AppleGym.
 * 
 * Contiene la información específica de cada producto o servicio agregado
 * al carrito, incluyendo cantidad, precios y promociones aplicadas.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "detalle_carrito", indexes = {
    @Index(name = "idx_detalle_carrito_carrito", columnList = "id_carrito"),
    @Index(name = "idx_detalle_carrito_tipo", columnList = "tipo")
})
public class DetalleCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_carrito")
    private Long idDetalleCarrito;
    
    @NotNull(message = "El carrito es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;
    
    @NotNull(message = "El tipo es obligatorio")
    @Column(name = "tipo", nullable = false)
    private String tipo; // "PRODUCTO" o "SERVICIO"
    
    // ID genérico del item (producto o servicio)
    @NotNull(message = "El ID del item es obligatorio")
    @Column(name = "id_item", nullable = false)
    private Long idItem;
    
    // Información del item para evitar joins innecesarios
    @Column(name = "nombre_item", length = 255)
    private String nombreItem;
    
    @Column(name = "descripcion_item", length = 500)
    private String descripcionItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_promocion")
    private Promocion promocion;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    
    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado;
    
    // Constructores
    public DetalleCarrito() {
        this.fechaAgregado = LocalDateTime.now();
        this.descuentoAplicado = BigDecimal.ZERO;
    }
    
    public DetalleCarrito(Carrito carrito, Producto producto, Integer cantidad) {
        this();
        this.carrito = carrito;
        this.producto = producto;
        this.tipo = "PRODUCTO";
        this.idItem = producto.getIdProducto();
        this.nombreItem = producto.getNombre();
        this.descripcionItem = producto.getDescripcion();
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        calcularSubtotal();
    }
    
    public DetalleCarrito(Carrito carrito, Servicio servicio, Integer cantidad) {
        this();
        this.carrito = carrito;
        this.servicio = servicio;
        this.tipo = "SERVICIO";
        this.idItem = servicio.getIdServicio();
        this.nombreItem = servicio.getNombre();
        this.descripcionItem = servicio.getDescripcion();
        this.cantidad = cantidad;
        this.precioUnitario = servicio.getPrecio();
        calcularSubtotal();
    }
    
    // Métodos de cálculo
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                                        .subtract(descuentoAplicado != null ? descuentoAplicado : BigDecimal.ZERO);
        }
    }
    
    @PrePersist
    @PreUpdate
    protected void onSave() {
        calcularSubtotal();
    }
    
    // Getters y Setters
    public Long getIdDetalleCarrito() {
        return idDetalleCarrito;
    }
    
    public void setIdDetalleCarrito(Long idDetalleCarrito) {
        this.idDetalleCarrito = idDetalleCarrito;
    }
    
    public Carrito getCarrito() {
        return carrito;
    }
    
    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
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
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            this.tipo = "PRODUCTO";
            this.idItem = producto.getIdProducto();
            this.nombreItem = producto.getNombre();
            this.descripcionItem = producto.getDescripcion();
        }
    }
    
    public Servicio getServicio() {
        return servicio;
    }
    
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        if (servicio != null) {
            this.tipo = "SERVICIO";
            this.idItem = servicio.getIdServicio();
            this.nombreItem = servicio.getNombre();
            this.descripcionItem = servicio.getDescripcion();
        }
    }
    
    public Promocion getPromocion() {
        return promocion;
    }
    
    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
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
    
    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }
    
    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleCarrito that = (DetalleCarrito) o;
        return Objects.equals(idDetalleCarrito, that.idDetalleCarrito);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idDetalleCarrito);
    }
    
    @Override
    public String toString() {
        return "DetalleCarrito{" +
                "idDetalleCarrito=" + idDetalleCarrito +
                ", tipo='" + tipo + '\'' +
                ", idItem=" + idItem +
                ", nombreItem='" + nombreItem + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}