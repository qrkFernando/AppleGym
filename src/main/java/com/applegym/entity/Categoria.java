package com.applegym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una categoría de productos y servicios en AppleGym.
 * 
 * Permite organizar los productos y servicios en categorías para una mejor
 * navegación y gestión del catálogo.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Entity
@Table(name = "categoria", indexes = {
    @Index(name = "idx_categoria_nombre", columnList = "nombre_categoria", unique = true)
})
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre_categoria", nullable = false, unique = true, length = 100)
    private String nombreCategoria;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relaciones
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Servicio> servicios;
    
    // Constructores
    public Categoria() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Categoria(String nombreCategoria, String descripcion) {
        this();
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }
    
    // Métodos de ciclo de vida JPA
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de negocio
    public boolean isActivo() {
        return activo != null && activo;
    }
    
    public int getTotalProductos() {
        return productos != null ? productos.size() : 0;
    }
    
    public int getTotalServicios() {
        return servicios != null ? servicios.size() : 0;
    }
    
    // Getters y Setters
    public Long getIdCategoria() {
        return idCategoria;
    }
    
    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }
    
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
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
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    public List<Servicio> getServicios() {
        return servicios;
    }
    
    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(idCategoria, categoria.idCategoria) &&
               Objects.equals(nombreCategoria, categoria.nombreCategoria);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idCategoria, nombreCategoria);
    }
    
    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombreCategoria='" + nombreCategoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}