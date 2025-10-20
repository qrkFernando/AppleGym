package com.applegym.repository;

import com.applegym.entity.Pago;
import com.applegym.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Pago.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByVenta(Venta venta);
    
    Optional<Pago> findByNumeroTransaccion(String numeroTransaccion);
    
    Optional<Pago> findByReferenciaExterna(String referenciaExterna);
}
