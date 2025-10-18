package com.applegym.service;

import com.applegym.dto.ClienteDTO;
import com.applegym.dto.ClienteRegistroDTO;
import com.applegym.entity.Cliente;
import com.applegym.exception.DuplicateResourceException;
import com.applegym.exception.InvalidDataException;
import com.applegym.exception.ResourceNotFoundException;
import com.applegym.repository.ClienteRepository;
import com.applegym.service.impl.ClienteServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ClienteServiceImpl.
 * 
 * Implementa pruebas siguiendo metodología TDD para validar
 * la lógica de negocio del servicio de clientes.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private ModelMapper modelMapper;
    
    @InjectMocks
    private ClienteServiceImpl clienteService;
    
    private ClienteRegistroDTO clienteRegistroDTO;
    private Cliente cliente;
    private ClienteDTO clienteDTO;
    
    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        clienteRegistroDTO = new ClienteRegistroDTO();
        clienteRegistroDTO.setNombreCliente("Juan Pérez");
        clienteRegistroDTO.setEmail("juan.perez@email.com");
        clienteRegistroDTO.setPassword("password123");
        clienteRegistroDTO.setConfirmPassword("password123");
        clienteRegistroDTO.setTelefono("987654321");
        clienteRegistroDTO.setDireccion("Av. Principal 123");
        
        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNombreCliente("Juan Pérez");
        cliente.setEmail("juan.perez@email.com");
        cliente.setPassword("encoded_password");
        cliente.setTelefono("987654321");
        cliente.setDireccion("Av. Principal 123");
        cliente.setActivo(true);
        cliente.setFechaRegistro(LocalDateTime.now());
        
        clienteDTO = new ClienteDTO();
        clienteDTO.setIdCliente(1L);
        clienteDTO.setNombreCliente("Juan Pérez");
        clienteDTO.setEmail("juan.perez@email.com");
        clienteDTO.setTelefono("987654321");
        clienteDTO.setDireccion("Av. Principal 123");
        clienteDTO.setActivo(true);
        clienteDTO.setFechaRegistro(LocalDateTime.now());
    }
    
    @Test
    void registrarCliente_DatosValidos_RetornaClienteDTO() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        // Act
        ClienteDTO resultado = clienteService.registrarCliente(clienteRegistroDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(clienteDTO.getNombreCliente(), resultado.getNombreCliente());
        assertEquals(clienteDTO.getEmail(), resultado.getEmail());
        verify(clienteRepository).save(any(Cliente.class));
        verify(passwordEncoder).encode("password123");
    }
    
    @Test
    void registrarCliente_EmailDuplicado_LanzaExcepcion() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateResourceException.class, 
                    () -> clienteService.registrarCliente(clienteRegistroDTO));
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void registrarCliente_PasswordsNoCoinciden_LanzaExcepcion() {
        // Arrange
        clienteRegistroDTO.setConfirmPassword("different_password");
        
        // Act & Assert
        assertThrows(InvalidDataException.class, 
                    () -> clienteService.registrarCliente(clienteRegistroDTO));
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void registrarCliente_DatosNulos_LanzaExcepcion() {
        // Act & Assert
        assertThrows(InvalidDataException.class, 
                    () -> clienteService.registrarCliente(null));
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void buscarClientePorId_IdExistente_RetornaClienteDTO() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        
        // Act
        Optional<ClienteDTO> resultado = clienteService.buscarClientePorId(1L);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(clienteDTO.getEmail(), resultado.get().getEmail());
    }
    
    @Test
    void buscarClientePorId_IdNoExistente_RetornaEmpty() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<ClienteDTO> resultado = clienteService.buscarClientePorId(1L);
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    void buscarClientePorEmail_EmailExistente_RetornaClienteDTO() {
        // Arrange
        String email = "juan.perez@email.com";
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        
        // Act
        Optional<ClienteDTO> resultado = clienteService.buscarClientePorEmail(email);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(clienteDTO.getEmail(), resultado.get().getEmail());
    }
    
    @Test
    void desactivarCliente_IdExistente_DesactivaCliente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        // Act
        clienteService.desactivarCliente(1L);
        
        // Assert
        verify(clienteRepository).save(any(Cliente.class));
        assertFalse(cliente.getActivo());
    }
    
    @Test
    void desactivarCliente_IdNoExistente_LanzaExcepcion() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                    () -> clienteService.desactivarCliente(1L));
    }
    
    @Test
    void emailYaRegistrado_EmailExistente_RetornaTrue() {
        // Arrange
        String email = "juan.perez@email.com";
        when(clienteRepository.existsByEmail(email.toLowerCase())).thenReturn(true);
        
        // Act
        boolean resultado = clienteService.emailYaRegistrado(email);
        
        // Assert
        assertTrue(resultado);
    }
    
    @Test
    void emailYaRegistrado_EmailNoExistente_RetornaFalse() {
        // Arrange
        String email = "nuevo@email.com";
        when(clienteRepository.existsByEmail(email.toLowerCase())).thenReturn(false);
        
        // Act
        boolean resultado = clienteService.emailYaRegistrado(email);
        
        // Assert
        assertFalse(resultado);
    }
    
    @Test
    void cambiarPassword_PasswordActualCorrecta_CambiaPassword() {
        // Arrange
        String passwordActual = "password_actual";
        String nuevaPassword = "nueva_password123";
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches(passwordActual, cliente.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(nuevaPassword)).thenReturn("nueva_password_encoded");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        // Act
        clienteService.cambiarPassword(1L, passwordActual, nuevaPassword);
        
        // Assert
        verify(passwordEncoder).encode(nuevaPassword);
        verify(clienteRepository).save(cliente);
    }
    
    @Test
    void cambiarPassword_PasswordActualIncorrecta_LanzaExcepcion() {
        // Arrange
        String passwordActual = "password_incorrecta";
        String nuevaPassword = "nueva_password123";
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches(passwordActual, cliente.getPassword())).thenReturn(false);
        
        // Act & Assert
        assertThrows(InvalidDataException.class, 
                    () -> clienteService.cambiarPassword(1L, passwordActual, nuevaPassword));
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void cambiarPassword_NuevaPasswordMuyCorta_LanzaExcepcion() {
        // Arrange
        String passwordActual = "password_actual";
        String nuevaPassword = "123"; // Muy corta
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches(passwordActual, cliente.getPassword())).thenReturn(true);
        
        // Act & Assert
        assertThrows(InvalidDataException.class, 
                    () -> clienteService.cambiarPassword(1L, passwordActual, nuevaPassword));
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}