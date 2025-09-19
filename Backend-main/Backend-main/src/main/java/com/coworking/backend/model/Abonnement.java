package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entité représentant un abonnement à un espace ouvert
 */
@Data
@Entity
public class Abonnement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private AbonnementType type;
    
    private double prix;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "espace_ouvert_id", nullable = false)
    private EspaceOuvert espaceOuvert;
    
    @OneToOne(mappedBy = "abonnement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Paiement paiement;
    
    /**
     * Types d'abonnement possibles
     */
    public enum AbonnementType {
        MENSUEL, ANNUEL
    }
}