package com.coworking.backend.repository;

import com.coworking.backend.model.Indisponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des périodes d'indisponibilité des espaces.
 */
public interface IndisponibiliteRepository extends JpaRepository<Indisponibilite, Long> {
    
    /**
     * Trouve les indisponibilités pour un espace donné qui chevauchent une période spécifique.
     */
    List<Indisponibilite> findByEspaceIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Long espaceId, LocalDateTime dateFin, LocalDateTime dateDebut);
}