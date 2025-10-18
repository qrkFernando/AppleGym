package com.applegym.security;

import com.applegym.entity.Cliente;
import com.applegym.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio personalizado para cargar detalles de usuario.
 * 
 * Implementa UserDetailsService de Spring Security para cargar
 * información de usuarios desde la base de datos.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cliente cliente = clienteRepository.findByEmailAndActivo(email, true)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        
        return cliente; // Cliente implementa UserDetails
    }
    
    /**
     * Carga un usuario por ID.
     * 
     * @param id ID del cliente
     * @return UserDetails del cliente
     * @throws UsernameNotFoundException si no se encuentra el usuario
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Usuario no encontrado con ID: " + id));
        
        if (!cliente.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo con ID: " + id);
        }
        
        return cliente;
    }
}