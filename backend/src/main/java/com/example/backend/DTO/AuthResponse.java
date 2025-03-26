package com.example.backend.DTO;

public class AuthResponse {

    private String token;
    private String username;

    // Construtor correto para inicializar as propriedades
    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // Getters e setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
