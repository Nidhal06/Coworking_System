package com.coworking.backend.dto;

import lombok.Data;

/**
 * DTO pour la mise à jour du profil utilisateur
 */
@Data
public class ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImagePath;
}