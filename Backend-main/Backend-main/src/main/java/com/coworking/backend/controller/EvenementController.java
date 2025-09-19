package com.coworking.backend.controller;

import com.coworking.backend.dto.EvenementDTO;
import com.coworking.backend.service.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des événements
 */
@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    /**
     * Récupère tous les événements (accessible à tous)
     */
    @GetMapping
    public ResponseEntity<List<EvenementDTO>> getAllEvenements() {
        return ResponseEntity.ok(evenementService.getAllEvenements());
    }

    /**
     * Récupère un événement par son ID (accessible à tous)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvenementDTO> getEvenementById(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.getEvenementById(id));
    }

    /**
     * Crée un nouvel événement (admin seulement)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EvenementDTO> createEvenement(@RequestBody EvenementDTO evenementDTO) {
        return ResponseEntity.ok(evenementService.createEvenement(evenementDTO));
    }

    /**
     * Met à jour un événement existant (admin seulement)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EvenementDTO> updateEvenement(
            @PathVariable Long id, 
            @RequestBody EvenementDTO evenementDTO) {
        return ResponseEntity.ok(evenementService.updateEvenement(id, evenementDTO));
    }

    /**
     * Supprime un événement (admin seulement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Inscrit un participant à un événement
     */
    @PostMapping("/{evenementId}/register/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<EvenementDTO> registerParticipant(
            @PathVariable Long evenementId, 
            @PathVariable Long userId) {
        return ResponseEntity.ok(evenementService.registerParticipant(evenementId, userId));
    }

    /**
     * Annule la participation d'un utilisateur à un événement
     */
    @PostMapping("/{evenementId}/cancel/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<EvenementDTO> cancelParticipation(
            @PathVariable Long evenementId, 
            @PathVariable Long userId) {
        return ResponseEntity.ok(evenementService.cancelParticipation(evenementId, userId));
    }
}