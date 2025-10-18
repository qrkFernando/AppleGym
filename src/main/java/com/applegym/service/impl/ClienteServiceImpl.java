package com.applegym.service.impl;

import com.applegym.dto.ClienteDTO;
import com.applegym.dto.ClienteRegistroDTO;
import com.applegym.entity.Cliente;
import com.applegym.repository.ClienteRepository;
import com.applegym.service.ClienteService;
import com.applegym.exception.ResourceNotFoundException;
import com.applegym.exception.DuplicateResourceException;
import com.applegym.exception.InvalidDataException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Cliente.
 * 
 * Implementa la lógica de negocio para la gestión de clientes,
 * siguiendo principios SOLID y buenas prácticas de desarrollo.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public ClienteDTO registrarCliente(ClienteRegistroDTO clienteRegistroDTO) {
        // Validar datos primero (incluye validación de null)
        validarDatosRegistro(clienteRegistroDTO);
        
        logger.info("Iniciando registro de cliente con email: {}", clienteRegistroDTO.getEmail());
        
        // Verificar que el email no esté registrado
        if (emailYaRegistrado(clienteRegistroDTO.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + clienteRegistroDTO.getEmail());
        }
        
        // Crear entidad Cliente
        Cliente cliente = new Cliente();
        cliente.setNombreCliente(clienteRegistroDTO.getNombreCliente());
        cliente.setEmail(clienteRegistroDTO.getEmail().toLowerCase().trim());
        cliente.setPassword(passwordEncoder.encode(clienteRegistroDTO.getPassword()));
        cliente.setTelefono(clienteRegistroDTO.getTelefono());
        cliente.setDireccion(clienteRegistroDTO.getDireccion());
        
        // Guardar cliente
        Cliente clienteGuardado = clienteRepository.save(cliente);
        
        logger.info("Cliente registrado exitosamente con ID: {}", clienteGuardado.getIdCliente());
        
        return convertirADTO(clienteGuardado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarClientePorId(Long id) {
        logger.debug("Buscando cliente por ID: {}", id);
        
        return clienteRepository.findById(id)
                .map(this::convertirADTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarClientePorEmail(String email) {
        logger.debug("Buscando cliente por email: {}", email);
        
        return clienteRepository.findByEmail(email.toLowerCase().trim())
                .map(this::convertirADTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerClientesActivos() {
        logger.debug("Obteniendo todos los clientes activos");
        
        return clienteRepository.findByActivo(true)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        logger.info("Actualizando cliente con ID: {}", id);
        
        Cliente cliente = obtenerClienteEntity(id);
        
        // Validar email único si cambió
        if (!cliente.getEmail().equals(clienteDTO.getEmail().toLowerCase().trim())) {
            if (emailYaRegistrado(clienteDTO.getEmail())) {
                throw new DuplicateResourceException("El email ya está en uso: " + clienteDTO.getEmail());
            }
        }
        
        // Actualizar campos
        cliente.setNombreCliente(clienteDTO.getNombreCliente());
        cliente.setEmail(clienteDTO.getEmail().toLowerCase().trim());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setDireccion(clienteDTO.getDireccion());
        
        Cliente clienteActualizado = clienteRepository.save(cliente);
        
        logger.info("Cliente actualizado exitosamente: {}", clienteActualizado.getIdCliente());
        
        return convertirADTO(clienteActualizado);
    }
    
    @Override
    public void desactivarCliente(Long id) {
        logger.info("Desactivando cliente con ID: {}", id);
        
        Cliente cliente = obtenerClienteEntity(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
        
        logger.info("Cliente desactivado exitosamente: {}", id);
    }
    
    @Override
    public void activarCliente(Long id) {
        logger.info("Activando cliente con ID: {}", id);
        
        Cliente cliente = obtenerClienteEntity(id);
        cliente.setActivo(true);
        clienteRepository.save(cliente);
        
        logger.info("Cliente activado exitosamente: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarClientesPorNombre(String nombre) {
        logger.debug("Buscando clientes por nombre: {}", nombre);
        
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }
        
        return clienteRepository.findByNombreContainingIgnoreCase(nombre.trim())
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean emailYaRegistrado(String email) {
        return clienteRepository.existsByEmail(email.toLowerCase().trim());
    }
    
    @Override
    public void cambiarPassword(Long id, String passwordActual, String nuevaPassword) {
        logger.info("Cambiando contraseña para cliente ID: {}", id);
        
        Cliente cliente = obtenerClienteEntity(id);
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, cliente.getPassword())) {
            throw new InvalidDataException("La contraseña actual es incorrecta");
        }
        
        // Validar nueva contraseña
        if (nuevaPassword == null || nuevaPassword.length() < 8) {
            throw new InvalidDataException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        
        // Actualizar contraseña
        cliente.setPassword(passwordEncoder.encode(nuevaPassword));
        clienteRepository.save(cliente);
        
        logger.info("Contraseña cambiada exitosamente para cliente: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerClienteEntity(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }
    
    @Override
    public ClienteDTO convertirADTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setIdCliente(cliente.getIdCliente());
        clienteDTO.setNombreCliente(cliente.getNombreCliente());
        clienteDTO.setEmail(cliente.getEmail());
        clienteDTO.setTelefono(cliente.getTelefono());
        clienteDTO.setDireccion(cliente.getDireccion());
        clienteDTO.setActivo(cliente.getActivo());
        clienteDTO.setFechaRegistro(cliente.getFechaRegistro());
        clienteDTO.setFechaActualizacion(cliente.getFechaActualizacion());
        
        return clienteDTO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasClientes() {
        logger.debug("Obteniendo estadísticas de clientes");
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalClientes", clienteRepository.count());
        estadisticas.put("clientesActivos", clienteRepository.countClientesActivos());
        estadisticas.put("clientesConCarritos", clienteRepository.findClientesConCarritosActivos().size());
        estadisticas.put("clientesConCompras", clienteRepository.findClientesConVentas().size());
        
        return estadisticas;
    }
    
    // Métodos privados de validación
    private void validarDatosRegistro(ClienteRegistroDTO clienteRegistroDTO) {
        if (clienteRegistroDTO == null) {
            throw new InvalidDataException("Los datos del cliente son obligatorios");
        }
        
        if (!clienteRegistroDTO.isPasswordMatch()) {
            throw new InvalidDataException("Las contraseñas no coinciden");
        }
        
        // Validaciones adicionales pueden ir aquí
        validarFormatoEmail(clienteRegistroDTO.getEmail());
        validarFortalezaPassword(clienteRegistroDTO.getPassword());
    }
    
    private void validarFormatoEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidDataException("El formato del email es inválido");
        }
    }
    
    private void validarFortalezaPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new InvalidDataException("La contraseña debe tener al menos 8 caracteres");
        }
        
        // Opcional: agregar más validaciones de fortaleza de contraseña
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            logger.warn("Contraseña débil detectada - se recomienda incluir mayúsculas, minúsculas y números");
        }
    }
}