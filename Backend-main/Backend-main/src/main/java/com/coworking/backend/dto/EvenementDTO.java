package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les événements avec liste de participants
 */
@Data
public class EvenementDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double price;
    private Integer maxParticipants;
    private Boolean isActive;
    private List<ParticipantDTO> participants;
    private Long espaceId;
    private String espaceName;


    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean active) {
        isActive = active;
    }
}