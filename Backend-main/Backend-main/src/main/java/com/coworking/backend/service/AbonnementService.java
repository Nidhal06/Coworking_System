package com.coworking.backend.service;

import com.coworking.backend.dto.AbonnementDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.*;
import com.coworking.backend.model.Abonnement.AbonnementType;
import com.coworking.backend.model.Paiement.PaiementStatut;
import com.coworking.backend.model.Paiement.PaiementType;
import com.coworking.backend.repository.AbonnementRepository;
import com.coworking.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.coworking.backend.repository.EspaceOuvertRepository;
import com.coworking.backend.repository.PaiementRepository;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des abonnements aux espaces ouverts.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final UserRepository userRepository;
    private final EspaceOuvertRepository espaceOuvertRepository;
    private final ModelMapper modelMapper;
    private final PaiementRepository paiementRepository;

    /**
     * Récupère tous les abonnements
     */
    public List<AbonnementDTO> getAllAbonnements() {
        return abonnementRepository.findAll().stream()
                .map(abonnement -> modelMapper.map(abonnement, AbonnementDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un abonnement par son ID
     */
    public AbonnementDTO getAbonnementById(Long id) {
        Abonnement abonnement = abonnementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found"));
        return modelMapper.map(abonnement, AbonnementDTO.class);
    }
    
    /**
     * Récupère les abonnements d'un utilisateur
     */
    public List<AbonnementDTO> getAbonnementsByUser(Long userId) {
        List<Abonnement> abonnements = abonnementRepository.findByUserId(userId);
        
        if (abonnements.isEmpty()) {
            throw new ResourceNotFoundException("No subscriptions found for user with id: " + userId);
        }
        
        return abonnements.stream()
                .map(abonnement -> modelMapper.map(abonnement, AbonnementDTO.class))
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère le prix de l'abonnement actif d'un utilisateur pour un espace
     */
    public Double getCurrentAbonnementPrice(Long userId, Long espaceOuvertId) {
        return abonnementRepository.findActiveByUserAndEspace(userId, espaceOuvertId)
                .map(Abonnement::getPrix)
                .orElse(null);
    }
    
    /**
     * Crée des abonnements pour tous les coworkers
     */
    @Transactional
    public List<AbonnementDTO> createAbonnementsForAllCoworkers(AbonnementDTO abonnementDTO) {
        // Trouver l'espace ouvert
        EspaceOuvert espaceOuvert = espaceOuvertRepository.findById(abonnementDTO.getEspaceOuvertId())
                .orElseThrow(() -> new ResourceNotFoundException("EspaceOuvert not found"));
        
        // Récupérer tous les coworkers
        List<User> coworkers = userRepository.findByType(User.UserType.COWORKER);
        
        if (coworkers.isEmpty()) {
            throw new ResourceNotFoundException("No coworkers found");
        }
        
        // Créer un abonnement pour chaque coworker
        return coworkers.stream()
                .map(coworker -> {
                    Abonnement abonnement = modelMapper.map(abonnementDTO, Abonnement.class);
                    abonnement.setUser(coworker);
                    abonnement.setEspaceOuvert(espaceOuvert);
                    
                    // Set end date based on type
                    if (abonnement.getType() == AbonnementType.MENSUEL) {
                        abonnement.setDateFin(abonnement.getDateDebut().plusMonths(1));
                    } else {
                        abonnement.setDateFin(abonnement.getDateDebut().plusYears(1));
                    }
                    
                    Abonnement savedAbonnement = abonnementRepository.save(abonnement);
                    return modelMapper.map(savedAbonnement, AbonnementDTO.class);
                })
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un utilisateur a un abonnement valide pour un espace
     */
    public boolean hasValidAbonnement(Long userId, Long espaceOuvertId) {
        LocalDate today = LocalDate.now();
        return abonnementRepository.existsByUserIdAndEspaceOuvertIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                userId, espaceOuvertId, today);
    }

    /**
     * Crée un nouvel abonnement avec paiement associé
     */
    @Transactional
    public AbonnementDTO createAbonnement(AbonnementDTO abonnementDTO) {
        User user = userRepository.findById(abonnementDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        EspaceOuvert espaceOuvert = espaceOuvertRepository.findById(abonnementDTO.getEspaceOuvertId())
                .orElseThrow(() -> new ResourceNotFoundException("EspaceOuvert not found"));
        
        Abonnement abonnement = modelMapper.map(abonnementDTO, Abonnement.class);
        abonnement.setUser(user);
        abonnement.setEspaceOuvert(espaceOuvert);
        
        // Définir le prix selon le type d'abonnement si non spécifié
        if (abonnement.getPrix() <= 0) {
            if (abonnement.getType() == AbonnementType.MENSUEL) {
                abonnement.setPrix(650.0); // Prix mensuel par défaut
            } else {
                abonnement.setPrix(8000.0); // Prix annuel par défaut
            }
        }
        
        // Set end date based on type
        if (abonnement.getType() == AbonnementType.MENSUEL) {
            abonnement.setDateFin(abonnement.getDateDebut().plusMonths(1));
        } else {
            abonnement.setDateFin(abonnement.getDateDebut().plusYears(1));
        }
        
        // Créer le paiement associé
        Paiement paiement = new Paiement();
        paiement.setMontant(abonnement.getPrix());
        paiement.setStatut(PaiementStatut.EN_ATTENTE);
        paiement.setType(PaiementType.ABONNEMENT);
        paiement.setDate(LocalDateTime.now());
        paiement = paiementRepository.save(paiement);
        
        // Associer le paiement à l'abonnement
        abonnement.setPaiement(paiement);
        
        Abonnement savedAbonnement = abonnementRepository.save(abonnement);
        
        // Mettre à jour la référence de l'abonnement dans le paiement
        paiement.setAbonnement(savedAbonnement);
        paiementRepository.save(paiement);
        
        return modelMapper.map(savedAbonnement, AbonnementDTO.class);
    }

    /**
     * Met à jour un abonnement existant
     */
    public AbonnementDTO updateAbonnement(Long id, AbonnementDTO abonnementDTO) {
        Abonnement existingAbonnement = abonnementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found"));
        
        modelMapper.map(abonnementDTO, existingAbonnement);
        Abonnement updatedAbonnement = abonnementRepository.save(existingAbonnement);
        
        return modelMapper.map(updatedAbonnement, AbonnementDTO.class);
    }

    /**
     * Supprime un abonnement
     */
    public void deleteAbonnement(Long id) {
        if (!abonnementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Abonnement not found");
        }
        abonnementRepository.deleteById(id);
    }
}