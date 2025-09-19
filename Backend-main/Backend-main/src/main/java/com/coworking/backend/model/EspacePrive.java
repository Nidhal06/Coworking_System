package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Entité représentant un espace privé (hérite de Espace)
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EspacePrive extends Espace {
    
    private double prixParJour;
    
    @ElementCollection
    private List<String> amenities;
    
    @OneToMany(mappedBy = "espace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "espace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evenement> evenements;
}