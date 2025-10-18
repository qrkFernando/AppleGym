package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un comprobante de venta en AppleGym.
 * 
 * Gestiona los documentos electrónicos generados para las ventas,
 * incluyendo boletas, facturas y comprobantes de pago.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "comprobante", indexes = {
    @Index(name = "idx_comprobante_venta", columnList = "id_venta"),
    @Index(name = "idx_comprobante_fecha", columnList = "fecha_emision"),
    @Index(name = "idx_comprobante_tipo", columnList = "tipo_comprobante"),
    @Index(name = "idx_comprobante_numero", columnList = "numero_comprobante", unique = true)
})
public class Comprobante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idComprobante;
    
    @NotNull(message = "La venta es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false, unique = true)
    private Venta venta;
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;
    
    @Column(name = "numero_comprobante", unique = true, nullable = false)
    private String numeroComprobante;
    
    @Column(name = "serie_comprobante")
    private String serieComprobante;
    
    @Column(name = "archivo_comprobante")
    private String archivoComprobante;
    
    @NotNull(message = "El tipo de comprobante es obligatorio")
    @Column(name = "tipo_comprobante", nullable = false, length = 50)
    private String tipoComprobante;
    
    @Column(name = "estado_comprobante", nullable = false, length = 50)
    private String estadoComprobante = "GENERADO";
    
    @Column(name = "hash_documento")
    private String hashDocumento;
    
    @Column(name = "url_descarga")
    private String urlDescarga;
    
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "observaciones")
    private String observaciones;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Enums
    public enum TipoComprobante {
        BOLETA("Boleta de Venta"),
        FACTURA("Factura"),
        NOTA_CREDITO("Nota de Crédito"),
        NOTA_DEBITO("Nota de Débito"),
        COMPROBANTE_PAGO("Comprobante de Pago"),
        RECIBO("Recibo");
        
        private final String descripcion;
        
        TipoComprobante(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum EstadoComprobante {
        GENERADO("Generado"),
        ENVIADO("Enviado"),
        DESCARGADO("Descargado"),
        ERROR("Error"),
        ANULADO("Anulado");
        
        private final String descripcion;
        
        EstadoComprobante(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Constructores
    public Comprobante() {
        this.fechaEmision = LocalDateTime.now();
        this.estadoComprobante = EstadoComprobante.GENERADO.name();
    }
    
    public Comprobante(Venta venta, TipoComprobante tipoComprobante) {
        this();
        this.venta = venta;
        this.tipoComprobante = tipoComprobante.name();
        generarNumeroComprobante();
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDateTime.now();
        }
        if (this.numeroComprobante == null) {
            generarNumeroComprobante();
        }
    }
    
    // Métodos de negocio
    private void generarNumeroComprobante() {
        // Generar número de comprobante basado en el tipo y timestamp
        String prefijo = obtenerPrefijoPorTipo();
        String serie = this.serieComprobante != null ? this.serieComprobante : "001";
        long numero = System.currentTimeMillis() % 100000000; // Últimos 8 dígitos del timestamp
        this.numeroComprobante = String.format("%s-%s-%08d", prefijo, serie, numero);
    }
    
    private String obtenerPrefijoPorTipo() {
        return switch (this.tipoComprobante) {
            case "BOLETA" -> "B";
            case "FACTURA" -> "F";
            case "NOTA_CREDITO" -> "NC";
            case "NOTA_DEBITO" -> "ND";
            case "COMPROBANTE_PAGO" -> "CP";
            case "RECIBO" -> "R";
            default -> "C";
        };
    }
    
    public void marcarComoEnviado() {
        this.estadoComprobante = EstadoComprobante.ENVIADO.name();
        this.fechaEnvio = LocalDateTime.now();
    }
    
    public void marcarComoDescargado() {
        if ("GENERADO".equals(this.estadoComprobante) || 
            "ENVIADO".equals(this.estadoComprobante)) {
            this.estadoComprobante = EstadoComprobante.DESCARGADO.name();
        }
    }
    
    public void marcarComoError(String observacion) {
        this.estadoComprobante = EstadoComprobante.ERROR.name();
        this.observaciones = observacion;
    }
    
    public void anular(String motivo) {
        this.estadoComprobante = EstadoComprobante.ANULADO.name();
        this.observaciones = motivo;
    }
    
    public boolean puedeSerDescargado() {
        return "GENERADO".equals(this.estadoComprobante) || 
               "ENVIADO".equals(this.estadoComprobante) ||
               "DESCARGADO".equals(this.estadoComprobante);
    }
    
    public boolean esValido() {
        return !"ERROR".equals(this.estadoComprobante) &&
               !"ANULADO".equals(this.estadoComprobante);
    }
    
    public String getNombreArchivo() {
        if (this.archivoComprobante != null) {
            return this.archivoComprobante;
        }
        return String.format("%s_%s.pdf", 
                this.tipoComprobante, 
                this.numeroComprobante.replace("-", "_"));
    }
    
    // Getters y Setters
    public Long getIdComprobante() {
        return idComprobante;
    }
    
    public void setIdComprobante(Long idComprobante) {
        this.idComprobante = idComprobante;
    }
    
    public Venta getVenta() {
        return venta;
    }
    
    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    
    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public String getNumeroComprobante() {
        return numeroComprobante;
    }
    
    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }
    
    public String getSerieComprobante() {
        return serieComprobante;
    }
    
    public void setSerieComprobante(String serieComprobante) {
        this.serieComprobante = serieComprobante;
    }
    
    public String getArchivoComprobante() {
        return archivoComprobante;
    }
    
    public void setArchivoComprobante(String archivoComprobante) {
        this.archivoComprobante = archivoComprobante;
    }
    
    public String getTipoComprobante() {
        return tipoComprobante;
    }
    
    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }
    
    public String getEstadoComprobante() {
        return estadoComprobante;
    }
    
    public void setEstadoComprobante(String estadoComprobante) {
        this.estadoComprobante = estadoComprobante;
    }
    
    public String getHashDocumento() {
        return hashDocumento;
    }
    
    public void setHashDocumento(String hashDocumento) {
        this.hashDocumento = hashDocumento;
    }
    
    public String getUrlDescarga() {
        return urlDescarga;
    }
    
    public void setUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
    }
    
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }
    
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
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
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comprobante that = (Comprobante) o;
        return Objects.equals(idComprobante, that.idComprobante) &&
               Objects.equals(numeroComprobante, that.numeroComprobante);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idComprobante, numeroComprobante);
    }
    
    @Override
    public String toString() {
        return "Comprobante{" +
                "idComprobante=" + idComprobante +
                ", numeroComprobante='" + numeroComprobante + '\'' +
                ", tipoComprobante=" + tipoComprobante +
                ", estadoComprobante=" + estadoComprobante +
                ", fechaEmision=" + fechaEmision +
                '}';
    }
}