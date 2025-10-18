package com.applegym.service;

import com.applegym.dto.ClienteDTO;
import com.applegym.dto.ClienteRegistroDTO;
import com.applegym.entity.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de Clientes.
 * 
 * Define los contratos de negocio para operaciones relacionadas con clientes.
 * Implementa el principio de Inversión de Dependencias (SOLID).
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public interface ClienteService {
    
    /**
     * Registra un nuevo cliente en el sistema.
     * 
     * @param clienteRegistroDTO Datos del cliente a registrar
     * @return ClienteDTO con la información del cliente registrado
     * @throws IllegalArgumentException si los datos son inválidos
     */
    ClienteDTO registrarCliente(ClienteRegistroDTO clienteRegistroDTO);
    
    /**
     * Busca un cliente por su ID.
     * 
     * @param id ID del cliente
     * @return Optional con ClienteDTO si existe
     */
    Optional<ClienteDTO> buscarClientePorId(Long id);
    
    /**
     * Busca un cliente por su email.
     * 
     * @param email Email del cliente
     * @return Optional con ClienteDTO si existe
     */
    Optional<ClienteDTO> buscarClientePorEmail(String email);
    
    /**
     * Obtiene todos los clientes activos.
     * 
     * @return Lista de ClienteDTO activos
     */
    List<ClienteDTO> obtenerClientesActivos();
    
    /**
     * Actualiza la información de un cliente.
     * 
     * @param id ID del cliente a actualizar
     * @param clienteDTO Nuevos datos del cliente
     * @return ClienteDTO actualizado
     * @throws IllegalArgumentException si el ID no existe o los datos son inválidos
     */
    ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO);
    
    /**
     * Desactiva un cliente (eliminación lógica).
     * 
     * @param id ID del cliente a desactivar
     * @throws IllegalArgumentException si el ID no existe
     */
    void desactivarCliente(Long id);
    
    /**
     * Activa un cliente previamente desactivado.
     * 
     * @param id ID del cliente a activar
     * @throws IllegalArgumentException si el ID no existe
     */
    void activarCliente(Long id);
    
    /**
     * Busca clientes por nombre (búsqueda parcial).
     * 
     * @param nombre Nombre a buscar
     * @return Lista de ClienteDTO que coincidan
     */
    List<ClienteDTO> buscarClientesPorNombre(String nombre);
    
    /**
     * Verifica si un email ya está registrado.
     * 
     * @param email Email a verificar
     * @return true si ya existe, false caso contrario
     */
    boolean emailYaRegistrado(String email);
    
    /**
     * Cambia la contraseña de un cliente.
     * 
     * @param id ID del cliente
     * @param passwordActual Contraseña actual
     * @param nuevaPassword Nueva contraseña
     * @throws IllegalArgumentException si la contraseña actual es incorrecta
     */
    void cambiarPassword(Long id, String passwordActual, String nuevaPassword);
    
    /**
     * Obtiene la entidad Cliente por ID (para uso interno).
     * 
     * @param id ID del cliente
     * @return Cliente entity
     * @throws IllegalArgumentException si no existe
     */
    Cliente obtenerClienteEntity(Long id);
    
    /**
     * Convierte una entidad Cliente a DTO.
     * 
     * @param cliente Entidad Cliente
     * @return ClienteDTO
     */
    ClienteDTO convertirADTO(Cliente cliente);
    
    /**
     * Obtiene estadísticas básicas de clientes.
     * 
     * @return Map con estadísticas
     */
    java.util.Map<String, Object> obtenerEstadisticasClientes();
}