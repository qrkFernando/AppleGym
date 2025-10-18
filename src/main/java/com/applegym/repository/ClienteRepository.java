package com.applegym.repository;

import com.applegym.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente.
 * 
 * Implementa el patrón DAO utilizando Spring Data JPA para el acceso a datos
 * de los clientes del gimnasio AppleGym.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su email (username).
     * 
     * @param email Email del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByEmail(String email);
    
    /**
     * Verifica si existe un cliente con el email especificado.
     * 
     * @param email Email a verificar
     * @return true si existe, false caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca clientes activos.
     * 
     * @param activo Estado activo
     * @return Lista de clientes activos
     */
    List<Cliente> findByActivo(Boolean activo);
    
    /**
     * Busca clientes por nombre (búsqueda parcial case-insensitive).
     * 
     * @param nombre Nombre a buscar
     * @return Lista de clientes que coincidan
     */
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cliente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    /**
     * Busca clientes registrados en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de clientes registrados en el rango
     */
    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    List<Cliente> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Cuenta el número de clientes activos.
     * 
     * @return Número de clientes activos
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.activo = true")
    Long countClientesActivos();
    
    /**
     * Busca clientes con carritos activos.
     * 
     * @return Lista de clientes con carritos activos
     */
    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.carritos car WHERE car.estado = 'ACTIVO'")
    List<Cliente> findClientesConCarritosActivos();
    
    /**
     * Busca clientes que han realizado compras.
     * 
     * @return Lista de clientes con ventas
     */
    @Query("SELECT DISTINCT c FROM Cliente c WHERE EXISTS (SELECT v FROM Venta v WHERE v.cliente = c)")
    List<Cliente> findClientesConVentas();
    
    /**
     * Busca clientes por email y que estén activos.
     * 
     * @param email Email del cliente
     * @param activo Estado activo
     * @return Optional con el cliente si existe y está activo
     */
    Optional<Cliente> findByEmailAndActivo(String email, Boolean activo);
    
    /**
     * Busca los últimos clientes registrados.
     * 
     * @param limit Número máximo de resultados
     * @return Lista de los últimos clientes registrados
     */
    @Query("SELECT c FROM Cliente c ORDER BY c.fechaRegistro DESC LIMIT :limit")
    List<Cliente> findUltimosClientesRegistrados(@Param("limit") int limit);
}