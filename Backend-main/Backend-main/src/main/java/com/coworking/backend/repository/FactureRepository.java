package com.coworking.backend.repository;

import com.coworking.backend.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour la gestion des factures.
 * Hérite des opérations CRUD standard de JpaRepository.
 */
public interface FactureRepository extends JpaRepository<Facture, Long> {
}