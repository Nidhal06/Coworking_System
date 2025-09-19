package com.coworking.backend.repository;

import com.coworking.backend.model.Evenement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Repository pour la gestion des événements.
 * Contient des méthodes pour charger les événements avec leurs relations.
 */
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    
    /**
     * Trouve un événement par son ID avec ses participants chargés en eager.
     */
    @EntityGraph(attributePaths = {"participants"})
    @Query("SELECT e FROM Evenement e WHERE e.id = :id")
    Optional<Evenement> findByIdWithParticipants(@Param("id") Long id);
    
    /**
     * Trouve un événement par son ID avec ses participants et paiements chargés en eager.
     */
    @EntityGraph(attributePaths = {"participants", "paiements", "paiements.user"})
    @Query("SELECT e FROM Evenement e WHERE e.id = :id")
    Optional<Evenement> findByIdWithParticipantsAndPayments(@Param("id") Long id);
}