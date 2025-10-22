package com.applegym.repository;

import com.applegym.entity.Venta;
import com.applegym.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Venta.
 * 
 * Implementa el patrón DAO para la gestión de ventas
 * realizadas en AppleGym.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    /**
     * Busca venta por número de venta.
     * 
     * @param numeroVenta Número único de la venta
     * @return Optional con la venta si existe
     */
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    
    /**
     * Busca ventas por cliente.
     * 
     * @param cliente Cliente propietario
     * @return Lista de ventas del cliente
     */
    List<Venta> findByCliente(Cliente cliente);
    
    /**
     * Busca ventas por estado.
     * 
     * @param estado Estado de la venta
     * @return Lista de ventas con el estado especificado
     */
    List<Venta> findByEstado(Venta.EstadoVenta estado);
    
    /**
     * Busca ventas en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de ventas en el rango
     */
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechaVentaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Busca ventas por cliente y estado.
     * 
     * @param cliente Cliente
     * @param estado Estado de la venta
     * @return Lista de ventas
     */
    List<Venta> findByClienteAndEstado(Cliente cliente, Venta.EstadoVenta estado);
    
    /**
     * Calcula el total de ventas en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Total de ventas en el período
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE " +
           "v.fechaVenta BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'PAGADO'")
    BigDecimal calcularTotalVentasPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Cuenta ventas por estado.
     * 
     * @param estado Estado de la venta
     * @return Número de ventas con el estado
     */
    Long countByEstado(Venta.EstadoVenta estado);
    
    /**
     * Busca las ventas más recientes.
     * 
     * @param pageable Configuración de paginación
     * @return Página de ventas ordenadas por fecha descendente
     */
    @Query("SELECT v FROM Venta v ORDER BY v.fechaVenta DESC")
    Page<Venta> findVentasRecientes(Pageable pageable);
    
    /**
     * Busca ventas pendientes de pago.
     * 
     * @return Lista de ventas pendientes
     */
    @Query("SELECT v FROM Venta v WHERE v.estado = 'PENDIENTE' " +
           "AND v.fechaVenta < :fechaLimite")
    List<Venta> findVentasPendientes(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    /**
     * Busca ventas completamente pagadas.
     * 
     * @return Lista de ventas pagadas
     */
    @Query("SELECT v FROM Venta v WHERE v.estado IN ('PAGADO', 'COMPLETADO')")
    List<Venta> findVentasPagadas();
    
    /**
     * Calcula estadísticas de ventas mensuales.
     * 
     * @param año Año para el cálculo
     * @param mes Mes para el cálculo
     * @return Total de ventas del mes
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE " +
           "YEAR(v.fechaVenta) = :año AND MONTH(v.fechaVenta) = :mes " +
           "AND v.estado IN ('PAGADO', 'COMPLETADO')")
    BigDecimal calcularVentasMensuales(@Param("año") int año, @Param("mes") int mes);
    
    /**
     * Busca mejores clientes por volumen de compras.
     * 
     * @param pageable Configuración de paginación
     * @return Página de clientes ordenados por total de compras
     */
    @Query("SELECT v.cliente FROM Venta v WHERE v.estado IN ('PAGADO', 'COMPLETADO') " +
           "GROUP BY v.cliente ORDER BY SUM(v.total) DESC")
    Page<Cliente> findMejoresClientes(Pageable pageable);
    
    /**
     * Busca ventas por ID de cliente.
     * 
     * @param idCliente ID del cliente
     * @return Lista de ventas del cliente
     */
    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :idCliente ORDER BY v.fechaVenta DESC")
    List<Venta> findByClienteId(@Param("idCliente") Long idCliente);
    
    /**
     * Calcula el promedio de venta por cliente.
     * 
     * @return Promedio de venta
     */
    @Query("SELECT AVG(v.total) FROM Venta v WHERE v.estado IN ('PAGADO', 'COMPLETADO')")
    BigDecimal calcularPromedioVenta();
    
    /**
     * Busca ventas con total mayor al especificado.
     * 
     * @param montoMinimo Monto mínimo
     * @return Lista de ventas con total mayor
     */
    @Query("SELECT v FROM Venta v WHERE v.total >= :montoMinimo AND v.estado IN ('PAGADO', 'COMPLETADO')")
    List<Venta> findVentasMayoresA(@Param("montoMinimo") BigDecimal montoMinimo);
    
    /**
     * Cuenta ventas realizadas hoy.
     * 
     * @param fechaInicio Inicio del día actual
     * @param fechaFin Fin del día actual
     * @return Número de ventas del día
     */
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Long countVentasDelDia(@Param("fechaInicio") LocalDateTime fechaInicio, 
                          @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Busca ventas que requieren comprobante.
     * 
     * @return Lista de ventas sin comprobante generado
     */
    @Query("SELECT v FROM Venta v WHERE v.estado = 'PAGADO' AND v.comprobante IS NULL")
    List<Venta> findVentasQueRequierenComprobante();
    
    // ===========================================================================
    // MÉTODOS PARA REPORTES Y ESTADÍSTICAS
    // ===========================================================================
    
    /**
     * Calcula el total de todas las ventas completadas.
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado IN ('COMPLETADO', 'PAGADO')")
    BigDecimal calcularVentasTotales();
    
    /**
     * Calcula ventas por rango de fecha.
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE " +
           "v.fechaVenta BETWEEN :inicio AND :fin AND v.estado IN ('COMPLETADO', 'PAGADO')")
    BigDecimal calcularVentasPorRangoFecha(@Param("inicio") LocalDateTime inicio, 
                                          @Param("fin") LocalDateTime fin);
    
    /**
     * Encuentra la fecha de la última venta.
     */
    @Query("SELECT MAX(v.fechaVenta) FROM Venta v")
    LocalDateTime findFechaUltimaVenta();
    
    /**
     * Obtiene los productos más vendidos.
     */
    @Query("SELECT dv.idProductoServicio, dv.nombre, SUM(dv.cantidad), SUM(dv.subtotal) " +
           "FROM DetalleVenta dv WHERE dv.tipo = 'PRODUCTO' " +
           "GROUP BY dv.idProductoServicio, dv.nombre " +
           "ORDER BY SUM(dv.cantidad) DESC")
    List<Object[]> findTopProductosMasVendidos(int limit);
    
    /**
     * Obtiene los servicios más solicitados.
     */
    @Query("SELECT dv.idProductoServicio, dv.nombre, SUM(dv.cantidad), SUM(dv.subtotal) " +
           "FROM DetalleVenta dv WHERE dv.tipo = 'SERVICIO' " +
           "GROUP BY dv.idProductoServicio, dv.nombre " +
           "ORDER BY SUM(dv.cantidad) DESC")
    List<Object[]> findTopServiciosMasSolicitados(int limit);
    
    /**
     * Obtiene ventas agrupadas por fecha.
     */
    @Query("SELECT CAST(v.fechaVenta AS date), COUNT(v), SUM(v.total) " +
           "FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin " +
           "AND v.estado IN ('COMPLETADO', 'PAGADO') " +
           "GROUP BY CAST(v.fechaVenta AS date) " +
           "ORDER BY CAST(v.fechaVenta AS date)")
    List<Object[]> findVentasPorFecha(@Param("inicio") LocalDateTime inicio, 
                                     @Param("fin") LocalDateTime fin);
}