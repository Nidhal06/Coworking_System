package com.coworking.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les données utilisateur
 */
@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private boolean enabled;
    private String profileImagePath;
    private String type;
    
    /**
     * Constructeur pour les cas d'utilisation courants
     */
    public UserDTO(String username, String email, String profileImagePath, String type) {
        this.username = username;
        this.email = email;
        this.profileImagePath = profileImagePath;
        this.type = type;
    }
}