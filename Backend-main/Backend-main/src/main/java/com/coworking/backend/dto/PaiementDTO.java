package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.coworking.backend.model.Paiement;

/**
 * DTO pour les paiements avec méthode de conversion depuis l'entité
 */
@Data
public class PaiementDTO {
    private Long id;
    private String type;
    private double montant;
    private LocalDateTime date;
    private String statut;
    private Long reservationId;
    private Long abonnementId;
    private Long evenementId;
    private Long userId;
    
    /**
     * Convertit une entité Paiement en DTO
     */
    public static PaiementDTO fromEntity(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setType(paiement.getType().name());
        dto.setMontant(paiement.getMontant());
        dto.setDate(paiement.getDate());
        dto.setStatut(paiement.getStatut().name());
        
        if (paiement.getReservation() != null) {
            dto.setReservationId(paiement.getReservation().getId());
        }
        if (paiement.getAbonnement() != null) {
            dto.setAbonnementId(paiement.getAbonnement().getId());
        }
        if (paiement.getEvenement() != null) {
            dto.setEvenementId(paiement.getEvenement().getId());
        }
        if (paiement.getUser() != null) {
            dto.setUserId(paiement.getUser().getId());
        }
        
        return dto;
    }
}