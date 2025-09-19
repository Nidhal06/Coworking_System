package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO pour les périodes d'indisponibilité
 */
@Data
public class IndisponibiliteDTO {
    private Long id;
    private Long espaceId;
    private String espaceName;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String raison;
}