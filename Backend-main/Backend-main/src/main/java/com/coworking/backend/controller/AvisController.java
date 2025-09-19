package com.coworking.backend.controller;

import com.coworking.backend.dto.AvisDTO;
import com.coworking.backend.service.AvisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des avis
 */
@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    /**
     * Récupère tous les avis (accessible à tous)
     */
    @GetMapping
    public ResponseEntity<List<AvisDTO>> getAllAvis() {
        return ResponseEntity.ok(avisService.getAllAvis());
    }

    /**
     * Récupère les avis par espace (accessible à tous)
     */
    @GetMapping("/espace/{espaceId}")
    public ResponseEntity<List<AvisDTO>> getAvisByEspaceId(@PathVariable Long espaceId) {
        return ResponseEntity.ok(avisService.getAvisByEspaceId(espaceId));
    }

    /**
     * Récupère un avis par son ID (accessible à tous)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AvisDTO> getAvisById(@PathVariable Long id) {
        return ResponseEntity.ok(avisService.getAvisById(id));
    }

    /**
     * Crée un nouvel avis (coworker seulement)
     */
    @PostMapping
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<AvisDTO> createAvis(@RequestBody AvisDTO avisDTO) {
        return ResponseEntity.ok(avisService.createAvis(avisDTO));
    }

    /**
     * Supprime un avis (admin et coworker)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER')")
    public ResponseEntity<Void> deleteAvis(@PathVariable Long id) {
        avisService.deleteAvis(id);
        return ResponseEntity.noContent().build();
    }
}