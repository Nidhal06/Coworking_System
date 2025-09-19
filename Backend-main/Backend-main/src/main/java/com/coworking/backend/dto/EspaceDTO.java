package com.coworking.backend.dto;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de base pour les espaces
 */
@Data
public class EspaceDTO {
    private Long id;
    private String name;
    private String description;
    private int capacity;
    private String photoPrincipal;
    private List<String> gallery;
    
    @JsonProperty("isActive") 
    private boolean active;
    
    private String type;
    private double prixParJour;
    private List<String> amenities;
}