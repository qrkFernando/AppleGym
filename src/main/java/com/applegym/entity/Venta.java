package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una venta realizada en AppleGym.
 * 
 * Contiene toda la información de la transacción comercial,
 * incluyendo productos, servicios, pagos y comprobantes.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "venta", indexes = {
    @Index(name = "idx_venta_cliente", columnList = "id_cliente"),
    @Index(name = "idx_venta_fecha", columnList = "fecha_venta"),
    @Index(name = "idx_venta_estado", columnList = "estado")
})
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;
    
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @NotNull(message = "La fecha de venta es obligatoria")
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;
    
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El total debe ser mayor a 0")
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @NotNull(message = "El estado es obligatorio")
    @Column(name = "estado", nullable = false, length = 50)
    private String estado = "PENDIENTE";
    
    @Column(name = "numero_venta", unique = true)
    private String numeroVenta;
    
    @Column(name = "observaciones")
    private String observaciones;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relaciones
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pago pago;
    
    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Comprobante comprobante;
    
    // Enum para estado de la venta
    public enum EstadoVenta {
        PENDIENTE,
        PAGADO,
        CANCELADO,
        REEMBOLSADO,
        COMPLETADO
    }
    
    // Constructores
    public Venta() {
        this.fechaVenta = LocalDateTime.now();
        this.estado = "PENDIENTE";
        generarNumeroVenta();
    }
    
    public Venta(Cliente cliente, BigDecimal total) {
        this();
        this.cliente = cliente;
        this.total = total;
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.fechaVenta == null) {
            this.fechaVenta = LocalDateTime.now();
        }
        if (this.numeroVenta == null) {
            generarNumeroVenta();
        }
    }
    
    // Métodos de negocio
    private void generarNumeroVenta() {
        // Generar número de venta único basado en timestamp
        this.numeroVenta = "VT" + System.currentTimeMillis();
    }
    
    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            detalle.setVenta(this);
            this.detalles.add(detalle);
        }
    }
    
    public void agregarPago(Pago pago) {
        if (pago != null) {
            pago.setVenta(this);
            this.pago = pago;
        }
    }
    
    public BigDecimal getTotalPagado() {
        return pago != null && "COMPLETADO".equals(pago.getEstadoPago()) 
            ? pago.getMonto() 
            : BigDecimal.ZERO;
    }
    
    public BigDecimal getSaldoPendiente() {
        return total.subtract(getTotalPagado());
    }
    
    public boolean estaPagadaCompleta() {
        return getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0;
    }
    
    public boolean puedeSerCancelada() {
        return "PENDIENTE".equals(estado);
    }
    
    public void marcarComoPagada() {
        if (estaPagadaCompleta()) {
            this.estado = "PAGADO";
        }
    }
    
    public void cancelar() {
        if (puedeSerCancelada()) {
            this.estado = "CANCELADO";
        }
    }
    
    public int getTotalItems() {
        return detalles.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }
    
    // Getters y Setters
    public Long getIdVenta() {
        return idVenta;
    }
    
    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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
    
    public String getNumeroVenta() {
        return numeroVenta;
    }
    
    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<>();
    }
    
    public Pago getPago() {
        return pago;
    }
    
    public void setPago(Pago pago) {
        this.pago = pago;
    }
    
    public Comprobante getComprobante() {
        return comprobante;
    }
    
    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return Objects.equals(idVenta, venta.idVenta) &&
               Objects.equals(numeroVenta, venta.numeroVenta);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idVenta, numeroVenta);
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", numeroVenta='" + numeroVenta + '\'' +
                ", fechaVenta=" + fechaVenta +
                ", total=" + total +
                ", estado=" + estado +
                ", totalItems=" + getTotalItems() +
                '}';
    }
}