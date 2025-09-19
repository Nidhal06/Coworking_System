package com.coworking.backend.dto;

import lombok.Data;

/**
 * DTO pour l'affichage du profil utilisateur
 */
@Data
public class ProfilDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profileImagePath;
    private String type;
}