package com.coworking.backend.service;

import com.coworking.backend.dto.ReservationDTO;
import com.coworking.backend.exception.*;
import com.coworking.backend.model.*;
import com.coworking.backend.model.Reservation.ReservationStatut;
import com.coworking.backend.model.Paiement.PaiementStatut;
import com.coworking.backend.model.Paiement.PaiementType;
import com.coworking.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des réservations d'espaces.
 * Gère la logique métier complexe liée aux réservations.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final EspaceRepository espaceRepository;
    private final IndisponibiliteRepository indisponibiliteRepository;
    private final PaiementRepository paiementRepository;
    private final AbonnementRepository abonnementRepository;
    private final FactureRepository factureRepository;

    /**
     * Récupère toutes les réservations
     * @return Liste des ReservationDTO
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une réservation par son ID
     * @param id ID de la réservation
     * @return ReservationDTO correspondant
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return convertToDto(reservation);
    }
    
    /**
     * Récupère les réservations d'un espace
     * @param spaceId ID de l'espace
     * @return Liste des ReservationDTO
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsBySpace(Long spaceId) {
        return reservationRepository.findByEspaceId(spaceId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Crée une nouvelle réservation avec gestion des différents types d'espaces
     * @param reservationDTO DTO contenant les données de la réservation
     * @return ReservationDTO créée
     * @throws IllegalArgumentException si les données sont invalides
     * @throws ResourceNotFoundException si les ressources associées n'existent pas
     * @throws AbonnementRequiredException pour les espaces ouverts sans abonnement valide
     * @throws UnavailableException si l'espace n'est pas disponible aux dates demandées
     */
    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        // Validation des données obligatoires
        if (reservationDTO.getEspaceId() == null || reservationDTO.getUserId() == null) {
            throw new IllegalArgumentException("Espace ID and User ID must not be null");
        }

        // Récupération des entités associées
        User user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Espace espace = espaceRepository.findById(reservationDTO.getEspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found"));

        Paiement paiement = null;
        boolean paiementValide = false;
        double montant = 0.0;

        // Gestion différente selon le type d'espace
        if (espace.getType() == Espace.EspaceType.OUVERT) {
            // Espace ouvert - nécessite un abonnement valide
            Abonnement abonnement = abonnementRepository.findActiveByUserAndEspace(user.getId(), espace.getId())
                    .orElseThrow(() -> new AbonnementRequiredException("Active subscription required"));
            
            montant = abonnement.getPrix();
            paiementValide = abonnement.getPaiement() != null && 
                            abonnement.getPaiement().getStatut() == PaiementStatut.VALIDE;
            paiement = abonnement.getPaiement();
            
            // S'assurer que le paiement a bien le user associé
            if (paiement != null && paiement.getUser() == null) {
                paiement.setUser(user);
                paiement = paiementRepository.save(paiement);
            }
            
            reservationDTO.setPaiementMontant(montant);
            reservationDTO.setPaiementValide(paiementValide);
        } else {
            // Espace privé - paiement direct
            if (reservationDTO.getPaiementMontant() == null || reservationDTO.getPaiementMontant() <= 0) {
                throw new IllegalArgumentException("Payment amount must be positive");
            }
            montant = reservationDTO.getPaiementMontant();
            paiementValide = reservationDTO.getPaiementValide() != null && reservationDTO.getPaiementValide();
            
            paiement = new Paiement();
            paiement.setMontant(montant);
            paiement.setStatut(paiementValide ? PaiementStatut.VALIDE : PaiementStatut.EN_ATTENTE);
            paiement.setType(PaiementType.RESERVATION);
            paiement.setDate(LocalDateTime.now());
            paiement.setUser(user);
            paiement = paiementRepository.save(paiement);
        }

        // Vérification disponibilité de l'espace
        if (!isEspaceAvailable(espace.getId(), reservationDTO.getDateDebut(), reservationDTO.getDateFin())) {
            throw new UnavailableException("Space not available for selected dates");
        }

        // Création et sauvegarde de la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEspace(espace);
        reservation.setDateDebut(reservationDTO.getDateDebut());
        reservation.setDateFin(reservationDTO.getDateFin());
        reservation.setStatut(ReservationStatut.EN_ATTENTE);
        reservation.setPaiement(paiement);

        Reservation savedReservation = reservationRepository.save(reservation);

        // Pour les espaces privés, lier la réservation au paiement
        if (espace.getType() == Espace.EspaceType.PRIVE) {
            paiement.setReservation(savedReservation);
            paiementRepository.save(paiement);
        }

        return convertToDto(savedReservation);
    }

    /**
     * Convertit une entité Reservation en DTO
     */
    private ReservationDTO convertToDto(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        
        // User info
        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserFirstName(reservation.getUser().getFirstName());
            dto.setUserLastName(reservation.getUser().getLastName());
            dto.setUserEmail(reservation.getUser().getEmail());
            dto.setUserPhone(reservation.getUser().getPhone());
        }
        
        // Espace info
        if (reservation.getEspace() != null) {
            dto.setEspaceId(reservation.getEspace().getId());
            dto.setEspaceName(reservation.getEspace().getName());
            dto.setEspaceType(reservation.getEspace().getType().toString());
            
            // Pour les espaces ouverts, récupérer le prix de l'abonnement
            if (reservation.getEspace().getType() == Espace.EspaceType.OUVERT && reservation.getUser() != null) {
                Optional<Abonnement> abonnement = abonnementRepository.findActiveByUserAndEspace(
                    reservation.getUser().getId(), 
                    reservation.getEspace().getId()
                );
                
                if (abonnement.isPresent() && abonnement.get().getPaiement() != null) {
                    dto.setPaiementMontant(abonnement.get().getPrix());
                    dto.setPaiementValide(abonnement.get().getPaiement().getStatut() == PaiementStatut.VALIDE);
                }
            }
        }
        
        // Dates et statut
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setStatut(reservation.getStatut().toString());
        
        // Pour les espaces privés, utiliser le paiement direct
        if (reservation.getEspace() != null && 
            reservation.getEspace().getType() == Espace.EspaceType.PRIVE && 
            reservation.getPaiement() != null) {
            dto.setPaiementMontant(reservation.getPaiement().getMontant());
            dto.setPaiementValide(reservation.getPaiement().getStatut() == PaiementStatut.VALIDE);
        }
        
        return dto;
    }
    
    /**
     * Met à jour une réservation existante
     */
    @Transactional
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
    Reservation existingReservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    
    // Update status if provided
    if (reservationDTO.getStatut() != null) {
        existingReservation.setStatut(ReservationStatut.valueOf(reservationDTO.getStatut()));
    }
    
    // Update dates if provided
    if (reservationDTO.getDateDebut() != null) {
        existingReservation.setDateDebut(reservationDTO.getDateDebut());
    }
    if (reservationDTO.getDateFin() != null) {
        existingReservation.setDateFin(reservationDTO.getDateFin());
    }
    
    // Update payment if provided
    if (reservationDTO.getPaiementMontant() != null) {
        if (existingReservation.getPaiement() != null) {
            existingReservation.getPaiement().setMontant(reservationDTO.getPaiementMontant());
            if (reservationDTO.getPaiementValide() != null) {
                existingReservation.getPaiement().setStatut(
                    reservationDTO.getPaiementValide() ? PaiementStatut.VALIDE : PaiementStatut.EN_ATTENTE
                );
            }
        } else {
            Paiement paiement = new Paiement();
            paiement.setMontant(reservationDTO.getPaiementMontant());
            paiement.setStatut(
                reservationDTO.getPaiementValide() ? PaiementStatut.VALIDE : PaiementStatut.EN_ATTENTE
            );
            paiement.setType(Paiement.PaiementType.RESERVATION);
            paiement.setDate(LocalDateTime.now());
            existingReservation.setPaiement(paiement);
        }
    }
    
    return convertToDto(reservationRepository.save(existingReservation));
  }

    /**
     * Supprime une réservation et ses dépendances
     */
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        
        // Gestion de la suppression en cascade manuelle
        if (reservation.getPaiement() != null) {
            // Si le paiement a une facture, on la supprime d'abord
            if (reservation.getPaiement().getFacture() != null) {
                factureRepository.delete(reservation.getPaiement().getFacture());
            }
            // Supprimer le paiement
            paiementRepository.delete(reservation.getPaiement());
        }
        
        // Puis supprimer la réservation
        reservationRepository.delete(reservation);
    }

    /**
     * Vérifie la disponibilité d'un espace pour une période donnée
     */
    private boolean isEspaceAvailable(Long espaceId, LocalDateTime start, LocalDateTime end) {
        // Vérifier les réservations qui se chevauchent
        List<Reservation> overlappingReservations = reservationRepository
                .findByEspaceIdAndDateDebutBetweenOrDateFinBetween(
                        espaceId, start, end, start, end);
        
        if (!overlappingReservations.isEmpty()) {
            return false;
        }
        
        // Vérifier les périodes d'indisponibilité
        List<Indisponibilite> indisponibilites = indisponibiliteRepository
                .findByEspaceIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                        espaceId, end, start);
        
        return indisponibilites.isEmpty();
    }
}