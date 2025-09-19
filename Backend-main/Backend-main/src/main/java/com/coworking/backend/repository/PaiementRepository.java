package com.coworking.backend.repository;

import com.coworking.backend.model.Evenement;
import com.coworking.backend.model.Paiement;
import com.coworking.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour la gestion des paiements.
 */
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    
    /**
     * Vérifie si un paiement existe pour un utilisateur et un événement donnés.
     */
    boolean existsByUserAndEvenement(User user, Evenement evenement);
}