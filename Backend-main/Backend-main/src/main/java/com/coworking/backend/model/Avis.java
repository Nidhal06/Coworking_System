package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entité représentant un avis/commentaire sur un espace
 */
@Data
@Entity
public class Avis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Espace espace;
    
    private int rating;
    private String commentaire;
    private LocalDate date;
}