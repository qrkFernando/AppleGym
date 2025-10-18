package com.applegym.repository;

import com.applegym.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Categoria.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    List<Categoria> findByActivo(Boolean activo);
    
    Categoria findByNombreCategoria(String nombreCategoria);
}