package com.coworking.backend.repository;

import com.coworking.backend.model.EspaceOuvert;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour la gestion des espaces ouverts.
 * Hérite des opérations CRUD standard de JpaRepository.
 */
public interface EspaceOuvertRepository extends JpaRepository<EspaceOuvert, Long> {
}