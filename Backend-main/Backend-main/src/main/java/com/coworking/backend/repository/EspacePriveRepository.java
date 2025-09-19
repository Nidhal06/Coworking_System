package com.coworking.backend.repository;

import com.coworking.backend.model.EspacePrive;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour la gestion des espaces privés.
 * Hérite des opérations CRUD standard de JpaRepository.
 */
public interface EspacePriveRepository extends JpaRepository<EspacePrive, Long> {
}