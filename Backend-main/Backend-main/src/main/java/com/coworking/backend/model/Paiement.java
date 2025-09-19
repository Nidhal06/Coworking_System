package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * Entité représentant un paiement dans le système
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private PaiementType type;
    
    private double montant;
    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    private PaiementStatut statut;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne(
    mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reservation reservation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abonnement_id")
    private Abonnement abonnement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
    
    @OneToOne(mappedBy = "paiement")
    private Facture facture;
    
    /**
     * Types de paiement possibles
     */
    public enum PaiementType {
        RESERVATION, 
        EVENEMENT, 
        ABONNEMENT
    }

    /**
     * Statuts possibles d'un paiement
     */
    public enum PaiementStatut {
        EN_ATTENTE, VALIDE, ANNULE
    }
}