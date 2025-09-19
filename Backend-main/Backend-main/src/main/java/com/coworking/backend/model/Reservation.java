package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entité représentant une réservation d'espace
 */
@Data
@Entity
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "espace_id")
    private Espace espace;
    
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    
    @Enumerated(EnumType.STRING)
    private ReservationStatut statut;
    
    private boolean paiementValide;
    
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "paiement_id", referencedColumnName = "id")
    private Paiement paiement;
    
    /**
     * Statuts possibles d'une réservation
     */
    public enum ReservationStatut {
        EN_ATTENTE, VALIDEE, ANNULEE
    }
}