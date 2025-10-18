package com.applegym.repository;

import com.applegym.entity.Carrito;
import com.applegym.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Carrito.
 * 
 * Implementa el patrón DAO para la gestión de carritos de compra
 * en el sistema AppleGym.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    /**
     * Busca el carrito activo de un cliente.
     * 
     * @param cliente Cliente propietario del carrito
     * @param estado Estado del carrito
     * @return Optional con el carrito activo si existe
     */
    Optional<Carrito> findByClienteAndEstado(Cliente cliente, String estado);
    
    /**
     * Busca todos los carritos de un cliente.
     * 
     * @param cliente Cliente propietario
     * @return Lista de carritos del cliente
     */
    List<Carrito> findByCliente(Cliente cliente);
    
    /**
     * Busca carritos por estado.
     * 
     * @param estado Estado del carrito
     * @return Lista de carritos con el estado especificado
     */
    List<Carrito> findByEstado(String estado);
    
    /**
     * Busca carritos abandonados (activos pero antiguos).
     * 
     * @param fechaLimite Fecha límite para considerar abandono
     * @return Lista de carritos abandonados
     */
    @Query("SELECT c FROM Carrito c WHERE c.estado = 'ACTIVO' AND c.fechaActualizacion < :fechaLimite")
    List<Carrito> findCarritosAbandonados(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    /**
     * Busca carritos creados en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de carritos en el rango
     */
    @Query("SELECT c FROM Carrito c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Carrito> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                    @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Cuenta carritos activos.
     * 
     * @return Número de carritos activos
     */
    @Query("SELECT COUNT(c) FROM Carrito c WHERE c.estado = 'ACTIVO'")
    Long countCarritosActivos();
    
    /**
     * Busca carritos con items (no vacíos).
     * 
     * @return Lista de carritos que tienen detalles
     */
    @Query("SELECT DISTINCT c FROM Carrito c JOIN c.detalles d WHERE c.estado = 'ACTIVO'")
    List<Carrito> findCarritosConItems();
    
    /**
     * Busca carritos vacíos.
     * 
     * @return Lista de carritos sin detalles
     */
    @Query("SELECT c FROM Carrito c WHERE c.estado = 'ACTIVO' AND c.detalles IS EMPTY")
    List<Carrito> findCarritosVacios();
    
    /**
     * Busca el carrito activo más reciente de un cliente por ID.
     * 
     * @param idCliente ID del cliente
     * @return Optional con el carrito activo más reciente
     */
    @Query("SELECT c FROM Carrito c WHERE c.cliente.idCliente = :idCliente " +
           "AND c.estado = 'ACTIVO' ORDER BY c.fecha DESC LIMIT 1")
    Optional<Carrito> findCarritoActivoMasReciente(@Param("idCliente") Long idCliente);
    
    /**
     * Cuenta items en carritos activos por cliente.
     * 
     * @param cliente Cliente
     * @return Número total de items en carritos activos
     */
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM Carrito c JOIN c.detalles d " +
           "WHERE c.cliente = :cliente AND c.estado = 'ACTIVO'")
    Long countItemsEnCarritosActivos(@Param("cliente") Cliente cliente);
    
    /**
     * Busca carritos por ID de cliente y estado.
     * 
     * @param idCliente ID del cliente
     * @param estado Estado del carrito
     * @return Lista de carritos
     */
    @Query("SELECT c FROM Carrito c WHERE c.cliente.idCliente = :idCliente AND c.estado = :estado")
    List<Carrito> findByClienteIdAndEstado(@Param("idCliente") Long idCliente, 
                                          @Param("estado") String estado);
    
    /**
     * Elimina carritos antiguos según criterios de limpieza.
     * 
     * @param fechaLimite Fecha límite para eliminar
     * @param estados Estados de carritos a eliminar
     */
    @Query("DELETE FROM Carrito c WHERE c.fecha < :fechaLimite AND c.estado IN :estados")
    void eliminarCarritosAntiguos(@Param("fechaLimite") LocalDateTime fechaLimite, 
                                 @Param("estados") List<String> estados);
}