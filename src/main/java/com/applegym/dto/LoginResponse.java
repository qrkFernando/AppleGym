package com.applegym.dto;

/**
 * DTO para respuestas de inicio de sesión exitoso.
 * 
 * Contiene el token JWT y la información del usuario autenticado.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
public class LoginResponse {
    
    private String accessToken;
    private String tokenType;
    private ClienteDTO cliente;
    
    // Constructores
    public LoginResponse() {}
    
    public LoginResponse(String accessToken, String tokenType, ClienteDTO cliente) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.cliente = cliente;
    }
    
    // Getters y Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public ClienteDTO getCliente() {
        return cliente;
    }
    
    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", cliente=" + cliente +
                '}';
    }
}