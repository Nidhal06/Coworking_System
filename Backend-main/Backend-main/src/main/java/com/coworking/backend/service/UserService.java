package com.coworking.backend.service;

import com.coworking.backend.dto.UserDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.Evenement;
import com.coworking.backend.model.Reservation;
import com.coworking.backend.model.User;
import com.coworking.backend.model.Abonnement;
import com.coworking.backend.model.Avis;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.repository.AbonnementRepository;
import com.coworking.backend.repository.AvisRepository;
import com.coworking.backend.repository.EvenementRepository;
import com.coworking.backend.repository.FactureRepository;
import com.coworking.backend.repository.PaiementRepository;
import com.coworking.backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service pour la gestion des utilisateurs.
 * Gère les opérations CRUD et la logique métier associée aux utilisateurs.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EvenementRepository evenementRepository;
    private final ReservationRepository reservationRepository;
    private final PaiementRepository paiementRepository;
    private final AbonnementRepository abonnementRepository;
    private final FactureRepository factureRepository;
    private final AvisRepository avisRepository;

    /**
     * Récupère tous les utilisateurs
     * @return Liste des UserDTO
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id ID de l'utilisateur
     * @return UserDTO correspondant
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Crée un nouvel utilisateur
     * @param userDTO DTO contenant les données du nouvel utilisateur
     * @return UserDTO de l'utilisateur créé
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        
        return modelMapper.map(savedUser, UserDTO.class);
    }

    /**
     * Met à jour un utilisateur existant
     * @param id ID de l'utilisateur à mettre à jour
     * @param userDTO DTO contenant les nouvelles données
     * @return UserDTO mis à jour
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        modelMapper.map(userDTO, existingUser);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }
    
    /**
     * Active/désactive un utilisateur
     * @param id ID de l'utilisateur
     * @return UserDTO mis à jour
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    public UserDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        user.setEnabled(!user.isEnabled());
        User updatedUser = userRepository.save(user);
        
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    /**
     * Supprime un utilisateur
     * @param id ID de l'utilisateur à supprimer
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    @Transactional
    public void deleteUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    // Handle paiements first
    List<Reservation> reservations = user.getReservations();
    if (reservations != null) {
        for (Reservation reservation : reservations) {
            if (reservation.getPaiement() != null) {
                // If paiement has a facture, delete it first
                if (reservation.getPaiement().getFacture() != null) {
                    factureRepository.delete(reservation.getPaiement().getFacture());
                }
                paiementRepository.delete(reservation.getPaiement());
            }
        }
        // Then delete reservations
        reservationRepository.deleteAll(reservations);
    }
    
    // Handle abonnements
    List<Abonnement> abonnements = user.getAbonnements();
    if (abonnements != null) {
        for (Abonnement abonnement : abonnements) {
            if (abonnement.getPaiement() != null) {
                if (abonnement.getPaiement().getFacture() != null) {
                    factureRepository.delete(abonnement.getPaiement().getFacture());
                }
                paiementRepository.delete(abonnement.getPaiement());
            }
        }
        abonnementRepository.deleteAll(abonnements);
    }
    
    // Handle avis
    List<Avis> avis = user.getAvis();
    if (avis != null) {
        avisRepository.deleteAll(avis);
    }
    
    // Remove user from events
    Set<Evenement> evenements = user.getEvenements();
    if (evenements != null) {
        for (Evenement evenement : evenements) {
            evenement.getParticipants().remove(user);
        }
        evenementRepository.saveAll(evenements);
    }
    
    // Finally delete the user
    userRepository.delete(user);
  }
}