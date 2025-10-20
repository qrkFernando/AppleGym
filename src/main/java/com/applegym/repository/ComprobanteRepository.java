package com.applegym.repository;

import com.applegym.entity.Comprobante;
import com.applegym.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Comprobante.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    
    Optional<Comprobante> findByVenta(Venta venta);
    
    Optional<Comprobante> findByNumeroComprobante(String numeroComprobante);
}
