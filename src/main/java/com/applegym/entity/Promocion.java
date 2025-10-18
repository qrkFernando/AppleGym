package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una promoción o descuento en AppleGym.
 * 
 * Gestiona las ofertas especiales, descuentos por temporada,
 * promociones por volumen y otros incentivos comerciales.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "promocion", indexes = {
    @Index(name = "idx_promocion_nombre", columnList = "nombre"),
    @Index(name = "idx_promocion_tipo", columnList = "tipo"),
    @Index(name = "idx_promocion_fechas", columnList = "fecha_inicio,fecha_fin"),
    @Index(name = "idx_promocion_activa", columnList = "activo")
})
public class Promocion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Long idPromocion;
    
    @NotBlank(message = "El nombre de la promoción es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El descuento debe ser mayor a 0")
    @DecimalMax(value = "100.0", message = "El descuento no puede ser mayor a 100%")
    @Column(name = "descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuento;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;
    
    @NotNull(message = "El tipo de promoción es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoPromocion tipo;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "monto_minimo", precision = 10, scale = 2)
    private BigDecimal montoMinimo;
    
    @Column(name = "cantidad_maxima_uso")
    private Integer cantidadMaximaUso;
    
    @Column(name = "cantidad_usada", nullable = false)
    private Integer cantidadUsada = 0;
    
    @Column(name = "codigo_promocional", unique = true)
    private String codigoPromocional;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relaciones
    @OneToMany(mappedBy = "promocion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleCarrito> detallesCarrito;
    
    // Enum para tipo de promoción
    public enum TipoPromocion {
        PORCENTAJE("Descuento por Porcentaje"),
        MONTO_FIJO("Descuento Monto Fijo"),
        DOS_POR_UNO("2x1"),
        TRES_POR_DOS("3x2"),
        ENVIO_GRATIS("Envío Gratis"),
        PRIMERA_COMPRA("Primera Compra"),
        CLIENTE_FRECUENTE("Cliente Frecuente"),
        TEMPORADA("Promoción de Temporada");
        
        private final String descripcion;
        
        TipoPromocion(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Constructores
    public Promocion() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.cantidadUsada = 0;
    }
    
    public Promocion(String nombre, String descripcion, BigDecimal descuento, 
                    LocalDateTime fechaInicio, LocalDateTime fechaFin, TipoPromocion tipo) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de negocio
    public boolean esAplicable() {
        LocalDateTime ahora = LocalDateTime.now();
        return activo && 
               ahora.isAfter(fechaInicio) && 
               ahora.isBefore(fechaFin) &&
               (cantidadMaximaUso == null || cantidadUsada < cantidadMaximaUso);
    }
    
    public boolean esAplicableParaMonto(BigDecimal monto) {
        return esAplicable() && 
               (montoMinimo == null || monto.compareTo(montoMinimo) >= 0);
    }
    
    public BigDecimal calcularDescuento(BigDecimal montoBase) {
        if (!esAplicableParaMonto(montoBase)) {
            return BigDecimal.ZERO;
        }
        
        return switch (tipo) {
            case PORCENTAJE -> montoBase.multiply(descuento).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            case MONTO_FIJO -> descuento.min(montoBase);
            case DOS_POR_UNO, TRES_POR_DOS -> montoBase.multiply(BigDecimal.valueOf(0.5)); // Simplificado
            default -> BigDecimal.ZERO;
        };
    }
    
    public void incrementarUso() {
        if (cantidadUsada != null) {
            this.cantidadUsada++;
        }
    }
    
    public boolean esVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
    }
    
    public boolean tieneCodigoPromocional() {
        return codigoPromocional != null && !codigoPromocional.trim().isEmpty();
    }
    
    public int getUsosDisponibles() {
        if (cantidadMaximaUso == null) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, cantidadMaximaUso - cantidadUsada);
    }
    
    public void desactivar() {
        this.activo = false;
    }
    
    // Getters y Setters
    public Long getIdPromocion() {
        return idPromocion;
    }
    
    public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public TipoPromocion getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoPromocion tipo) {
        this.tipo = tipo;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public BigDecimal getMontoMinimo() {
        return montoMinimo;
    }
    
    public void setMontoMinimo(BigDecimal montoMinimo) {
        this.montoMinimo = montoMinimo;
    }
    
    public Integer getCantidadMaximaUso() {
        return cantidadMaximaUso;
    }
    
    public void setCantidadMaximaUso(Integer cantidadMaximaUso) {
        this.cantidadMaximaUso = cantidadMaximaUso;
    }
    
    public Integer getCantidadUsada() {
        return cantidadUsada;
    }
    
    public void setCantidadUsada(Integer cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }
    
    public String getCodigoPromocional() {
        return codigoPromocional;
    }
    
    public void setCodigoPromocional(String codigoPromocional) {
        this.codigoPromocional = codigoPromocional;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public List<DetalleCarrito> getDetallesCarrito() {
        return detallesCarrito;
    }
    
    public void setDetallesCarrito(List<DetalleCarrito> detallesCarrito) {
        this.detallesCarrito = detallesCarrito;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Promocion promocion = (Promocion) o;
        return Objects.equals(idPromocion, promocion.idPromocion) &&
               Objects.equals(nombre, promocion.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idPromocion, nombre);
    }
    
    @Override
    public String toString() {
        return "Promocion{" +
                "idPromocion=" + idPromocion +
                ", nombre='" + nombre + '\'' +
                ", descuento=" + descuento +
                ", tipo=" + tipo +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", activo=" + activo +
                ", esVigente=" + esVigente() +
                '}';
    }
}