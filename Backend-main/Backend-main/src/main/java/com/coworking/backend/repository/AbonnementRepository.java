package com.coworking.backend.repository;

import com.coworking.backend.model.Abonnement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository pour la gestion des abonnements aux espaces ouverts.
 */
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    
    /**
     * Vérifie si un abonnement actif existe pour un utilisateur et un espace donné à une date spécifique.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Abonnement a " +
           "WHERE a.user.id = :userId " +
           "AND a.espaceOuvert.id = :espaceOuvertId " +
           "AND a.dateDebut <= :date " +
           "AND a.dateFin >= :date")
    boolean existsByUserIdAndEspaceOuvertIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
        @Param("userId") Long userId, 
        @Param("espaceOuvertId") Long espaceOuvertId, 
        @Param("date") LocalDate date);
    
    /**
     * Trouve tous les abonnements pour un utilisateur donné.
     */
    List<Abonnement> findByUserId(Long userId);
    
    /**
     * Trouve l'abonnement actif pour un utilisateur et un espace donné.
     */
    @Query("SELECT a FROM Abonnement a WHERE a.user.id = :userId AND a.espaceOuvert.id = :espaceId " +
           "AND a.dateDebut <= CURRENT_TIMESTAMP AND a.dateFin >= CURRENT_TIMESTAMP")
    Optional<Abonnement> findActiveByUserAndEspace(
        @Param("userId") Long userId, 
        @Param("espaceId") Long espaceId);
}