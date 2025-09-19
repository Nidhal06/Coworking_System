package com.coworking.backend.controller;

import com.coworking.backend.dto.PaiementDTO;
import com.coworking.backend.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des paiements
 */
@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    /**
     * Récupère tous les paiements (admin et réceptionnistes)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<List<PaiementDTO>> getAllPaiements() {
        return ResponseEntity.ok(paiementService.getAllPaiements());
    }

    /**
     * Récupère un paiement par son ID (admin et réceptionnistes)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<PaiementDTO> getPaiementById(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getPaiementById(id));
    }

    /**
     * Crée un nouveau paiement (admin et réceptionnistes)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<PaiementDTO> createPaiement(@RequestBody PaiementDTO paiementDTO) {
        return ResponseEntity.ok(paiementService.createPaiement(paiementDTO));
    }

    /**
     * Met à jour un paiement existant (admin et réceptionnistes)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<PaiementDTO> updatePaiement(
            @PathVariable Long id, 
            @RequestBody PaiementDTO paiementDTO) {
        return ResponseEntity.ok(paiementService.updatePaiement(id, paiementDTO));
    }

    /**
     * Supprime un paiement (admin seulement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePaiement(@PathVariable Long id) {
        paiementService.deletePaiement(id);
        return ResponseEntity.noContent().build();
    }
}