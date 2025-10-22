package com.applegym.repository;

import com.applegym.entity.Servicio;
import com.applegym.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Servicio.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    List<Servicio> findByActivo(Boolean activo);
    
    List<Servicio> findByCategoria(Categoria categoria);
    
    List<Servicio> findByCategoriaAndActivo(Categoria categoria, Boolean activo);
    
    @Query("SELECT s FROM Servicio s WHERE LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND s.activo = true")
    List<Servicio> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    /**
     * Cuenta servicios por estado activo.
     */
    Long countByActivo(Boolean activo);
}