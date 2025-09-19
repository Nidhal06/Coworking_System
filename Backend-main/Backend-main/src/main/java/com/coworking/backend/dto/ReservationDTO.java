package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO pour les réservations avec données associées
 */
@Data
public class ReservationDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPhone;
    private Long espaceId;
    private String espaceName;
    private String espaceType;
    
    @JsonProperty("paiementMontant")
    private Double paiementMontant;
    
    @JsonProperty("paiementValide")
    private Boolean paiementValide;
    
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
}