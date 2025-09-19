package com.coworking.backend.repository;

import com.coworking.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des réservations.
 * Contient des méthodes spécifiques pour la recherche de disponibilité.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Trouve les réservations pour un espace donné qui chevauchent les périodes spécifiées.
     * Utile pour vérifier la disponibilité d'un espace.
     */
    List<Reservation> findByEspaceIdAndDateDebutBetweenOrDateFinBetween(
            Long espaceId, 
            LocalDateTime start1, LocalDateTime end1, 
            LocalDateTime start2, LocalDateTime end2);
    
    // Trouve toutes les réservations pour un espace donné
    List<Reservation> findByEspaceId(Long espaceId);
}