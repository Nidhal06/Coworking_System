package com.coworking.backend.repository;

import com.coworking.backend.model.Espace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour la gestion des espaces de coworking.
 */
public interface EspaceRepository extends JpaRepository<Espace, Long> {
    
    /**
     * Trouve tous les espaces d'un type donné.
     */
    List<Espace> findByType(String type);
}