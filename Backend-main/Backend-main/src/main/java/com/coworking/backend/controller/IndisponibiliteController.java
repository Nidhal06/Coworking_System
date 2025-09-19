package com.coworking.backend.controller;

import com.coworking.backend.dto.IndisponibiliteDTO;
import com.coworking.backend.service.IndisponibiliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des périodes d'indisponibilité
 */
@RestController
@RequestMapping("/api/indisponibilites")
@RequiredArgsConstructor
public class IndisponibiliteController {

    private final IndisponibiliteService indisponibiliteService;

    /**
     * Récupère toutes les indisponibilités (accessible à tous)
     */
    @GetMapping
    public ResponseEntity<List<IndisponibiliteDTO>> getAllIndisponibilites() {
        return ResponseEntity.ok(indisponibiliteService.getAllIndisponibilites());
    }

    /**
     * Récupère une indisponibilité par son ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<IndisponibiliteDTO> getIndisponibiliteById(@PathVariable Long id) {
        return ResponseEntity.ok(indisponibiliteService.getIndisponibiliteById(id));
    }

    /**
     * Crée une nouvelle indisponibilité (admin seulement)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IndisponibiliteDTO> createIndisponibilite(
            @RequestBody IndisponibiliteDTO indisponibiliteDTO) {
        return ResponseEntity.ok(indisponibiliteService.createIndisponibilite(indisponibiliteDTO));
    }

    /**
     * Met à jour une indisponibilité existante (admin seulement)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IndisponibiliteDTO> updateIndisponibilite(
            @PathVariable Long id, 
            @RequestBody IndisponibiliteDTO indisponibiliteDTO) {
        return ResponseEntity.ok(indisponibiliteService.updateIndisponibilite(id, indisponibiliteDTO));
    }

    /**
     * Supprime une indisponibilité (admin seulement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIndisponibilite(@PathVariable Long id) {
        indisponibiliteService.deleteIndisponibilite(id);
        return ResponseEntity.noContent().build();
    }
}