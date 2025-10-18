package com.applegym.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object para registro de Cliente.
 * 
 * Específicamente diseñado para el proceso de registro de nuevos clientes,
 * incluyendo validaciones de seguridad y campos requeridos.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class ClienteRegistroDTO {
    
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreCliente;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String password;
    
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmPassword;
    
    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;
    
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;
    
    // Constructores
    public ClienteRegistroDTO() {}
    
    public ClienteRegistroDTO(String nombreCliente, String email, String password, 
                             String confirmPassword, String telefono, String direccion) {
        this.nombreCliente = nombreCliente;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    
    // Métodos de validación
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
    
    // Getters y Setters
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    @Override
    public String toString() {
        return "ClienteRegistroDTO{" +
                "nombreCliente='" + nombreCliente + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}