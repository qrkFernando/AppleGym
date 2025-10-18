package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un pago realizado para una venta en AppleGym.
 * 
 * Gestiona la información de los pagos, incluyendo métodos de pago,
 * estados, referencias y validaciones de seguridad.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "pago", indexes = {
    @Index(name = "idx_pago_venta", columnList = "id_venta"),
    @Index(name = "idx_pago_fecha", columnList = "fecha_pago"),
    @Index(name = "idx_pago_estado", columnList = "estado_pago"),
    @Index(name = "idx_pago_referencia", columnList = "referencia_externa")
})
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;
    
    @NotNull(message = "La venta es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    @NotNull(message = "El tipo de pago es obligatorio")
    @Column(name = "tipo_pago", nullable = false, length = 50)
    private String tipoPago;
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
    
    @NotNull(message = "El estado del pago es obligatorio")
    @Column(name = "estado_pago", nullable = false, length = 50)
    private String estadoPago = "PENDIENTE";
    
    @Column(name = "referencia_externa", unique = true)
    private String referenciaExterna;
    
    @Column(name = "numero_transaccion")
    private String numeroTransaccion;
    
    @Column(name = "detalles_pago", length = 500)
    private String detallesPago;
    
    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;
    
    @Column(name = "codigo_autorizacion")
    private String codigoAutorizacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Enums
    public enum TipoPago {
        EFECTIVO("Efectivo"),
        TARJETA_CREDITO("Tarjeta de Crédito"),
        TARJETA_DEBITO("Tarjeta de Débito"),
        TRANSFERENCIA("Transferencia Bancaria"),
        PAYPAL("PayPal"),
        MERCADO_PAGO("MercadoPago"),
        YAPE("Yape"),
        PLIN("Plin");
        
        private final String descripcion;
        
        TipoPago(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum EstadoPago {
        PENDIENTE("Pendiente"),
        PROCESANDO("Procesando"),
        APROBADO("Aprobado"),
        RECHAZADO("Rechazado"),
        CANCELADO("Cancelado"),
        REEMBOLSADO("Reembolsado"),
        ERROR("Error");
        
        private final String descripcion;
        
        EstadoPago(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Constructores
    public Pago() {
        this.fechaPago = LocalDateTime.now();
        this.estadoPago = EstadoPago.PENDIENTE.name();
        generarNumeroTransaccion();
    }
    
    public Pago(Venta venta, TipoPago tipoPago, BigDecimal monto) {
        this();
        this.venta = venta;
        this.tipoPago = tipoPago.name();
        this.monto = monto;
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.fechaPago == null) {
            this.fechaPago = LocalDateTime.now();
        }
        if (this.numeroTransaccion == null) {
            generarNumeroTransaccion();
        }
    }
    
    // Métodos de negocio
    private void generarNumeroTransaccion() {
        // Generar número de transacción único
        this.numeroTransaccion = "TXN" + System.currentTimeMillis() + "-" + 
                                (int)(Math.random() * 1000);
    }
    
    public void aprobar() {
        if ("PENDIENTE".equals(this.estadoPago) || "PROCESANDO".equals(this.estadoPago)) {
            this.estadoPago = "APROBADO";
            this.fechaProcesamiento = LocalDateTime.now();
        }
    }
    
    public void rechazar(String motivo) {
        if ("PENDIENTE".equals(this.estadoPago) || "PROCESANDO".equals(this.estadoPago)) {
            this.estadoPago = "RECHAZADO";
            this.detallesPago = motivo;
            this.fechaProcesamiento = LocalDateTime.now();
        }
    }
    
    public void cancelar() {
        if ("PENDIENTE".equals(this.estadoPago)) {
            this.estadoPago = "CANCELADO";
            this.fechaProcesamiento = LocalDateTime.now();
        }
    }
    
    public void marcarComoProcesando() {
        if ("PENDIENTE".equals(this.estadoPago)) {
            this.estadoPago = "PROCESANDO";
        }
    }
    
    public boolean esAprobado() {
        return "APROBADO".equals(this.estadoPago);
    }
    
    public boolean puedeSerReembolsado() {
        return "APROBADO".equals(this.estadoPago);
    }
    
    public boolean esElectronico() {
        return !"EFECTIVO".equals(tipoPago);
    }
    
    // Getters y Setters
    public Long getIdPago() {
        return idPago;
    }
    
    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }
    
    public Venta getVenta() {
        return venta;
    }
    
    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    
    public String getTipoPago() {
        return tipoPago;
    }
    
    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public String getEstadoPago() {
        return estadoPago;
    }
    
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
    
    public String getReferenciaExterna() {
        return referenciaExterna;
    }
    
    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }
    
    public String getNumeroTransaccion() {
        return numeroTransaccion;
    }
    
    public void setNumeroTransaccion(String numeroTransaccion) {
        this.numeroTransaccion = numeroTransaccion;
    }
    
    public String getDetallesPago() {
        return detallesPago;
    }
    
    public void setDetallesPago(String detallesPago) {
        this.detallesPago = detallesPago;
    }
    
    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }
    
    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
    
    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }
    
    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pago pago = (Pago) o;
        return Objects.equals(idPago, pago.idPago) &&
               Objects.equals(numeroTransaccion, pago.numeroTransaccion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idPago, numeroTransaccion);
    }
    
    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", numeroTransaccion='" + numeroTransaccion + '\'' +
                ", tipoPago=" + tipoPago +
                ", monto=" + monto +
                ", estadoPago=" + estadoPago +
                ", fechaPago=" + fechaPago +
                '}';
    }
}