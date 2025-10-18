package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa un servicio disponible en el gimnasio AppleGym.
 * 
 * Incluye servicios como membresías, entrenamiento personal, clases grupales, etc.
 * Implementa principios SOLID y validaciones de seguridad.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "servicio", indexes = {
    @Index(name = "idx_servicio_nombre", columnList = "nombre"),
    @Index(name = "idx_servicio_categoria", columnList = "id_categoria")
})
public class Servicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;
    
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Column(name = "duracion", nullable = false)
    private Integer duracion; // Duración en días
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleCarrito> detallesCarrito;
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detallesVenta;
    
    // Constructores
    public Servicio() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Servicio(String nombre, String descripcion, BigDecimal precio, Integer duracion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracion = duracion;
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de negocio
    public boolean isDisponible() {
        return activo;
    }
    
    public boolean tieneCapacidadDisponible(Integer cantidad) {
        return capacidadMaxima == null || cantidad <= capacidadMaxima;
    }
    
    // Getters y Setters
    public Long getIdServicio() {
        return idServicio;
    }
    
    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
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
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public Integer getDuracion() {
        return duracion;
    }
    
    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }
    
    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
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
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public List<DetalleCarrito> getDetallesCarrito() {
        return detallesCarrito;
    }
    
    public void setDetallesCarrito(List<DetalleCarrito> detallesCarrito) {
        this.detallesCarrito = detallesCarrito;
    }
    
    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }
    
    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servicio servicio = (Servicio) o;
        return Objects.equals(idServicio, servicio.idServicio) &&
               Objects.equals(nombre, servicio.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idServicio, nombre);
    }
    
    @Override
    public String toString() {
        return "Servicio{" +
                "idServicio=" + idServicio +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", duracion=" + duracion +
                ", activo=" + activo +
                '}';
    }
}