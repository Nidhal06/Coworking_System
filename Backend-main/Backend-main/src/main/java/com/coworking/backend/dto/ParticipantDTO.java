package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO pour les participants aux événements
 */
@Data
public class ParticipantDTO {
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPhone;
    private LocalDateTime registrationDate = LocalDateTime.now();
}