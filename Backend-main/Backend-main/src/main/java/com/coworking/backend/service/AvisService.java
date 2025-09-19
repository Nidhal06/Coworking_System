package com.coworking.backend.service;

import com.coworking.backend.dto.AvisDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.*;
import com.coworking.backend.repository.AvisRepository;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.repository.EspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des avis sur les espaces.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AvisService {

    private final AvisRepository avisRepository;
    private final UserRepository userRepository;
    private final EspaceRepository espaceRepository;

    /**
     * Récupère tous les avis
     */
    public List<AvisDTO> getAllAvis() {
        return avisRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les avis d'un espace spécifique
     */
    public List<AvisDTO> getAvisByEspaceId(Long espaceId) {
        return avisRepository.findByEspaceId(espaceId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un avis par son ID
     */
    public AvisDTO getAvisById(Long id) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis not found"));
        return convertToDto(avis);
    }

    /**
     * Convertit une entité Avis en DTO
     */
    private AvisDTO convertToDto(Avis avis) {
        AvisDTO dto = new AvisDTO();
        dto.setId(avis.getId());
        
        if (avis.getUser() != null) {
            dto.setUserId(avis.getUser().getId());
            dto.setUserUsername(avis.getUser().getUsername());
            dto.setUserFirstName(avis.getUser().getFirstName());
            dto.setUserLastName(avis.getUser().getLastName());
        }
        
        if (avis.getEspace() != null) {
            dto.setEspaceId(avis.getEspace().getId());
            dto.setEspaceName(avis.getEspace().getName());
            dto.setEspaceType(avis.getEspace().getType().toString());
        }
        
        dto.setRating(avis.getRating());
        dto.setCommentaire(avis.getCommentaire());
        dto.setDate(avis.getDate());
        
        return dto;
    }
    
    /**
     * Crée un nouvel avis
     */
    public AvisDTO createAvis(AvisDTO avisDTO) {
        User user = userRepository.findById(avisDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Espace espace = espaceRepository.findById(avisDTO.getEspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found"));
        
        Avis avis = new Avis();
        avis.setUser(user);
        avis.setEspace(espace);
        avis.setRating(avisDTO.getRating());
        avis.setCommentaire(avisDTO.getCommentaire());
        avis.setDate(LocalDate.now());
        
        Avis savedAvis = avisRepository.save(avis);
        return convertToDto(savedAvis);
    }

    /**
     * Supprime un avis
     */
    public void deleteAvis(Long id) {
        if (!avisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avis not found");
        }
        avisRepository.deleteById(id);
    }
}