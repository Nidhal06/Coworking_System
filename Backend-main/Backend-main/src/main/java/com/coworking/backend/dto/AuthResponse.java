package com.coworking.backend.dto;

import lombok.Data;

/**
 * DTO pour la réponse d'authentification
 */
@Data
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private Long userId;
    
    public AuthResponse(String token, String email, String role, Long userId) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.userId = userId;
    }
}