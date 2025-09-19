package com.coworking.backend.dto;

import lombok.Data;

/**
 * DTO pour la requête d'authentification
 */
@Data
public class AuthRequest {
    private String email;
    private String password;
}