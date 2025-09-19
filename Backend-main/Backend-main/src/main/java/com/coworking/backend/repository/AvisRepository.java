package com.coworking.backend.repository;

import com.coworking.backend.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour la gestion des avis sur les espaces.
 */
public interface AvisRepository extends JpaRepository<Avis, Long> {
    
    /**
     * Trouve tous les avis pour un espace donné.
     */
    List<Avis> findByEspaceId(Long espaceId);
}