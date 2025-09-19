package com.coworking.backend.controller;

import com.coworking.backend.dto.AbonnementDTO;
import com.coworking.backend.service.AbonnementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des abonnements
 */
@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final AbonnementService abonnementService;

    /**
     * Récupère tous les abonnements (accessible à tous)
     */
    @GetMapping
    public ResponseEntity<List<AbonnementDTO>> getAllAbonnements() {
        return ResponseEntity.ok(abonnementService.getAllAbonnements());
    }

    /**
     * Récupère un abonnement par son ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<AbonnementDTO> getAbonnementById(@PathVariable Long id) {
        return ResponseEntity.ok(abonnementService.getAbonnementById(id));
    }
    
    /**
     * Récupère les abonnements d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<List<AbonnementDTO>> getAbonnementsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(abonnementService.getAbonnementsByUser(userId));
    }

    /**
     * Crée un nouvel abonnement (coworker seulement)
     */
    @PostMapping
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<AbonnementDTO> createAbonnement(@RequestBody AbonnementDTO abonnementDTO) {
        return ResponseEntity.ok(abonnementService.createAbonnement(abonnementDTO));
    }
    
    /**
     * Crée des abonnements pour tous les coworkers (admin seulement)
     */
    @PostMapping("/for-all-coworkers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AbonnementDTO>> createAbonnementsForAllCoworkers(
            @RequestBody AbonnementDTO abonnementDTO) {
        return ResponseEntity.ok(abonnementService.createAbonnementsForAllCoworkers(abonnementDTO));
    }

    /**
     * Vérifie si un utilisateur a un abonnement valide pour un espace
     */
    @GetMapping("/check-valid/{userId}/{espaceOuvertId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<Boolean> checkValidAbonnement(
            @PathVariable Long userId, 
            @PathVariable Long espaceOuvertId) {
        return ResponseEntity.ok(abonnementService.hasValidAbonnement(userId, espaceOuvertId));
    }

    /**
     * Met à jour un abonnement existant
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<AbonnementDTO> updateAbonnement(
            @PathVariable Long id, 
            @RequestBody AbonnementDTO abonnementDTO) {
        return ResponseEntity.ok(abonnementService.updateAbonnement(id, abonnementDTO));
    }

    /**
     * Supprime un abonnement (admin et coworker)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER')")
    public ResponseEntity<Void> deleteAbonnement(@PathVariable Long id) {
        abonnementService.deleteAbonnement(id);
        return ResponseEntity.noContent().build();
    }
}