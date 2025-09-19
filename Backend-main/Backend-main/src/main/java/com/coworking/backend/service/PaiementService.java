package com.coworking.backend.service;

import com.coworking.backend.dto.PaiementDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.*;
import com.coworking.backend.model.Paiement.PaiementStatut;
import com.coworking.backend.model.Paiement.PaiementType;
import com.coworking.backend.repository.PaiementRepository;
import com.coworking.backend.repository.ReservationRepository;
import com.coworking.backend.repository.AbonnementRepository;
import com.coworking.backend.repository.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des paiements.
 * Gère les paiements liés aux réservations, abonnements et événements.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final AbonnementRepository abonnementRepository;
    private final EvenementRepository evenementRepository;
    private final ModelMapper modelMapper;

    /**
     * Récupère tous les paiements
     */
    public List<PaiementDTO> getAllPaiements() {
        return paiementRepository.findAll().stream()
                .map(PaiementDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un paiement par son ID
     */
    public PaiementDTO getPaiementById(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found"));
        return PaiementDTO.fromEntity(paiement);
    }

    /**
     * Crée un nouveau paiement avec gestion des différents types
     */
    public PaiementDTO createPaiement(PaiementDTO paiementDTO) {
        Paiement paiement = new Paiement();
        // Map les champs de base
        paiement.setType(PaiementType.valueOf(paiementDTO.getType()));
        paiement.setMontant(paiementDTO.getMontant());
        paiement.setDate(LocalDateTime.now());
        paiement.setStatut(PaiementStatut.valueOf(paiementDTO.getStatut()));
        
        // Gestion des relations selon le type
        switch (paiement.getType()) {
            case RESERVATION:
                if (paiementDTO.getReservationId() != null) {
                    Reservation reservation = reservationRepository.findById(paiementDTO.getReservationId())
                            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
                    paiement.setReservation(reservation);
                }
                break;
            case ABONNEMENT:
                if (paiementDTO.getAbonnementId() != null) {
                    Abonnement abonnement = abonnementRepository.findById(paiementDTO.getAbonnementId())
                            .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found"));
                    paiement.setAbonnement(abonnement);
                }
                break;
            case EVENEMENT:
                if (paiementDTO.getEvenementId() != null) {
                    Evenement evenement = evenementRepository.findById(paiementDTO.getEvenementId())
                            .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));
                    paiement.setEvenement(evenement);
                }
                break;
        }
        
        Paiement savedPaiement = paiementRepository.save(paiement);
        return PaiementDTO.fromEntity(savedPaiement);
    }

    /**
     * Met à jour un paiement existant
     */
    public PaiementDTO updatePaiement(Long id, PaiementDTO paiementDTO) {
        Paiement existingPaiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found"));
        
        // Mise à jour des champs de base
        existingPaiement.setType(PaiementType.valueOf(paiementDTO.getType()));
        existingPaiement.setMontant(paiementDTO.getMontant());
        existingPaiement.setStatut(PaiementStatut.valueOf(paiementDTO.getStatut()));
        
        // Mise à jour des relations selon le type
        switch (existingPaiement.getType()) {
            case RESERVATION:
                existingPaiement.setAbonnement(null);
                existingPaiement.setEvenement(null);
                if (paiementDTO.getReservationId() != null) {
                    Reservation reservation = reservationRepository.findById(paiementDTO.getReservationId())
                            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
                    existingPaiement.setReservation(reservation);
                } else {
                    existingPaiement.setReservation(null);
                }
                break;
            case ABONNEMENT:
                existingPaiement.setReservation(null);
                existingPaiement.setEvenement(null);
                if (paiementDTO.getAbonnementId() != null) {
                    Abonnement abonnement = abonnementRepository.findById(paiementDTO.getAbonnementId())
                            .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found"));
                    existingPaiement.setAbonnement(abonnement);
                } else {
                    existingPaiement.setAbonnement(null);
                }
                break;
            case EVENEMENT:
                existingPaiement.setReservation(null);
                existingPaiement.setAbonnement(null);
                if (paiementDTO.getEvenementId() != null) {
                    Evenement evenement = evenementRepository.findById(paiementDTO.getEvenementId())
                            .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));
                    existingPaiement.setEvenement(evenement);
                } else {
                    existingPaiement.setEvenement(null);
                }
                break;
        }
        
        Paiement updatedPaiement = paiementRepository.save(existingPaiement);
        return PaiementDTO.fromEntity(updatedPaiement);
    }

    /**
     * Supprime un paiement
     */
    public void deletePaiement(Long id) {
        if (!paiementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paiement not found");
        }
        paiementRepository.deleteById(id);
    }
}