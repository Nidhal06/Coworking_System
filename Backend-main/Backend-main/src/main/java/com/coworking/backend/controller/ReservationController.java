package com.coworking.backend.controller;

import com.coworking.backend.dto.ReservationDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.User;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des réservations
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    /**
     * Récupère toutes les réservations (admin et réceptionnistes)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    /**
     * Récupère une réservation par son ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    /**
     * Crée une nouvelle réservation (coworker seulement)
     */
    @PostMapping
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<ReservationDTO> createReservation(
            @RequestBody ReservationDTO reservationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        reservationDTO.setUserId(user.getId());
        return ResponseEntity.ok(reservationService.createReservation(reservationDTO));
    }
    
    /**
     * Récupère les réservations d'un utilisateur (vérification du propriétaire)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('COWORKER') and #userId == principal.id")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getAllReservations().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList()));
    }
    
    
    /**
     * Récupère les réservations par espace (accessible à tous)
     */
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsBySpace(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reservationService.getReservationsBySpace(spaceId));
    }

    /**
     * Met à jour une réservation existante
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id, 
            @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationDTO));
    }

    /**
     * Supprime une réservation (admin et coworker)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}