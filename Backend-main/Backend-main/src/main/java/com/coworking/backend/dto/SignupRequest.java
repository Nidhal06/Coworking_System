package com.coworking.backend.dto;

import lombok.Data;

/**
 * DTO pour la requête d'inscription
 */
@Data
public class SignupRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
}