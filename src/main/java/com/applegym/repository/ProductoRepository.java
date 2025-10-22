package com.applegym.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.applegym.entity.Categoria;
import com.applegym.entity.Producto;

/**
 * Repositorio para la entidad Producto.
 * 
 * Implementa el patrón DAO para el acceso a datos de productos
 * disponibles en AppleGym.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    /**
     * Busca productos activos.
     * 
     * @param activo Estado activo
     * @return Lista de productos activos
     */
    List<Producto> findByActivo(Boolean activo);
    
    /**
     * Busca productos por nombre (búsqueda parcial case-insensitive).
     * 
     * @param nombre Nombre a buscar
     * @return Lista de productos que coincidan
     */
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    /**
     * Busca productos por categoría.
     * 
     * @param categoria Categoría del producto
     * @return Lista de productos de la categoría
     */
    List<Producto> findByCategoria(Categoria categoria);
    
    /**
     * Busca productos activos por categoría.
     * 
     * @param categoria Categoría del producto
     * @param activo Estado activo
     * @return Lista de productos activos de la categoría
     */
    List<Producto> findByCategoriaAndActivo(Categoria categoria, Boolean activo);
    
    /**
     * Busca productos en un rango de precios.
     * 
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de productos en el rango de precios
     */
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax AND p.activo = true")
    List<Producto> findByPrecioBetween(@Param("precioMin") BigDecimal precioMin, 
                                      @Param("precioMax") BigDecimal precioMax);
    
    /**
     * Busca productos con stock disponible.
     * 
     * @return Lista de productos con stock > 0
     */
    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.activo = true")
    List<Producto> findProductosDisponibles();
    
    /**
     * Busca productos con stock bajo (menor al mínimo especificado).
     * 
     * @param stockMinimo Stock mínimo
     * @return Lista de productos con stock bajo
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo(@Param("stockMinimo") Integer stockMinimo);
    
    /**
     * Busca productos sin stock.
     * 
     * @return Lista de productos sin stock
     */
    @Query("SELECT p FROM Producto p WHERE p.stock = 0 AND p.activo = true")
    List<Producto> findProductosSinStock();
    
    /**
     * Busca productos más vendidos.
     * 
     * @param pageable Configuración de paginación
     * @return Página con los productos más vendidos
     */
    @Query("SELECT p FROM Producto p JOIN DetalleVenta dv ON p.idProducto = dv.producto.idProducto " +
           "WHERE p.activo = true GROUP BY p ORDER BY SUM(dv.cantidad) DESC")
    Page<Producto> findProductosMasVendidos(Pageable pageable);
    
    /**
     * Cuenta productos por categoría.
     * 
     * @param categoria Categoría
     * @return Número de productos en la categoría
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria = :categoria AND p.activo = true")
    Long countByCategoria(@Param("categoria") Categoria categoria);
    
    /**
     * Busca productos por ID de categoría y activos.
     * 
     * @param idCategoria ID de la categoría
     * @param activo Estado activo
     * @return Lista de productos
     */
    @Query("SELECT p FROM Producto p WHERE p.categoria.idCategoria = :idCategoria AND p.activo = :activo")
    List<Producto> findByCategoriaIdAndActivo(@Param("idCategoria") Long idCategoria, 
                                             @Param("activo") Boolean activo);
    
    /**
     * Busca productos disponibles paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de productos disponibles
     */
    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.activo = true")
    Page<Producto> findProductosDisponiblesPaginados(Pageable pageable);
    
    /**
     * Busca productos por criterios múltiples.
     * 
     * @param nombre Nombre del producto (opcional)
     * @param idCategoria ID de categoría (opcional)
     * @param precioMin Precio mínimo (opcional)
     * @param precioMax Precio máximo (opcional)
     * @param conStock Si debe tener stock disponible
     * @param pageable Configuración de paginación
     * @return Página de productos que coincidan con los criterios
     */
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
           "(:conStock = false OR p.stock > 0) AND " +
           "p.activo = true")
    Page<Producto> findProductosPorCriterios(@Param("nombre") String nombre,
                                           @Param("idCategoria") Long idCategoria,
                                           @Param("precioMin") BigDecimal precioMin,
                                           @Param("precioMax") BigDecimal precioMax,
                                           @Param("conStock") Boolean conStock,
                                           Pageable pageable);
    
    /**
     * Obtiene el valor total del inventario.
     *
     * @return Valor total del inventario (precio * stock)
     */
    @Query("SELECT SUM(p.precio * p.stock) FROM Producto p WHERE p.activo = true")
    BigDecimal calcularValorTotalInventario();
    
    /**
     * Cuenta productos por estado activo.
     */
    Long countByActivo(Boolean activo);
}