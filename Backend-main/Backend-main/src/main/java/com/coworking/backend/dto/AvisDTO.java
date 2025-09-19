package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour les avis avec informations utilisateur et espace
 */
@Data
public class AvisDTO {
    private Long id;
    private Long userId;
    private String userUsername;
    private String userFirstName;
    private String userLastName;
    private Long espaceId;
    private String espaceName;
    private String espaceType;
    private int rating;
    private String commentaire;
    private LocalDate date;
}