package com.applegym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object para Servicio.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class ServicioDTO {
    
    private Long idServicio;
    
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracion;
    
    private Boolean activo;
    
    private String imagenUrl;
    
    private Integer capacidadMaxima;
    
    private Long idCategoria;
    
    private String nombreCategoria;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    // Campos calculados
    private boolean disponible;
    
    // Constructores
    public ServicioDTO() {}
    
    // Métodos de negocio
    public boolean isDisponible() {
        return activo != null && activo;
    }
    
    public String getDuracionTexto() {
        if (duracion == null) return "";
        if (duracion == 1) return "1 día";
        if (duracion < 30) return duracion + " días";
        if (duracion == 30) return "1 mes";
        return (duracion / 30) + " meses";
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
        this.disponible = isDisponible();
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
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public boolean isDisponibleField() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    @Override
    public String toString() {
        return "ServicioDTO{" +
                "idServicio=" + idServicio +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", duracion=" + duracion +
                ", activo=" + activo +
                ", disponible=" + disponible +
                ", nombreCategoria='" + nombreCategoria + '\'' +
                '}';
    }
}