package com.applegym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object para Categoria.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class CategoriaDTO {
    
    private Long idCategoria;
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreCategoria;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    private Boolean activo;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    // Campos calculados
    private int totalProductos;
    private int totalServicios;
    
    // Constructores
    public CategoriaDTO() {}
    
    public CategoriaDTO(Long idCategoria, String nombreCategoria, String descripcion, Boolean activo) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.activo = activo;
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
    
    public int getTotalProductos() {
        return totalProductos;
    }
    
    public void setTotalProductos(int totalProductos) {
        this.totalProductos = totalProductos;
    }
    
    public int getTotalServicios() {
        return totalServicios;
    }
    
    public void setTotalServicios(int totalServicios) {
        this.totalServicios = totalServicios;
    }
    
    @Override
    public String toString() {
        return "CategoriaDTO{" +
                "idCategoria=" + idCategoria +
                ", nombreCategoria='" + nombreCategoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", totalProductos=" + totalProductos +
                ", totalServicios=" + totalServicios +
                '}';
    }
}