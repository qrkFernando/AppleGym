package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa un carrito de compras en AppleGym.
 * 
 * Permite a los clientes agregar productos y servicios antes de realizar
 * la compra final. Implementa principios SOLID y gestión de estado.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "carrito", indexes = {
    @Index(name = "idx_carrito_cliente", columnList = "id_cliente"),
    @Index(name = "idx_carrito_fecha", columnList = "fecha")
})
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;
    
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @Column(name = "estado", nullable = false, length = 50)
    private String estado = "ACTIVO";
    
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relaciones
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleCarrito> detalles = new ArrayList<>();
    
    // Constructores
    public Carrito() {
        this.fecha = LocalDateTime.now();
        this.estado = "ACTIVO";
        this.total = BigDecimal.ZERO;
    }
    
    public Carrito(Cliente cliente) {
        this();
        this.cliente = cliente;
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
        calcularTotal();
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
        calcularTotal();
    }
    
    // Métodos de negocio
    public void agregarDetalle(DetalleCarrito detalle) {
        if (detalle != null) {
            detalle.setCarrito(this);
            this.detalles.add(detalle);
            calcularTotal();
        }
    }
    
    public void removerDetalle(DetalleCarrito detalle) {
        if (detalle != null) {
            this.detalles.remove(detalle);
            detalle.setCarrito(null);
            calcularTotal();
        }
    }
    
    public void limpiarCarrito() {
        this.detalles.clear();
        this.total = BigDecimal.ZERO;
    }
    
    public void calcularTotal() {
        this.total = detalles.stream()
                .map(DetalleCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean estaVacio() {
        return detalles == null || detalles.isEmpty();
    }
    
    public boolean estaActivo() {
        return "ACTIVO".equals(this.estado);
    }
    
    public int getTotalItems() {
        return detalles.stream()
                .mapToInt(DetalleCarrito::getCantidad)
                .sum();
    }
    
    // Getters y Setters
    public Long getIdCarrito() {
        return idCarrito;
    }
    
    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public List<DetalleCarrito> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleCarrito> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<>();
        calcularTotal();
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrito carrito = (Carrito) o;
        return Objects.equals(idCarrito, carrito.idCarrito);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idCarrito);
    }
    
    @Override
    public String toString() {
        return "Carrito{" +
                "idCarrito=" + idCarrito +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", total=" + total +
                ", totalItems=" + getTotalItems() +
                '}';
    }
}