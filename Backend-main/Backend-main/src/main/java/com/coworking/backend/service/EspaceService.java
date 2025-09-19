package com.coworking.backend.service;

import com.coworking.backend.dto.EspaceDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.Espace;
import com.coworking.backend.repository.EspaceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des espaces de coworking.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EspaceService {

    private final EspaceRepository espaceRepository;
    private final ModelMapper modelMapper;

    /**
     * Récupère tous les espaces
     */
    public List<EspaceDTO> getAllEspaces() {
        return espaceRepository.findAll().stream()
                .map(espace -> modelMapper.map(espace, EspaceDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un espace par son ID
     */
    public EspaceDTO getEspaceById(Long id) {
        Espace espace = espaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found"));
        return modelMapper.map(espace, EspaceDTO.class);
    }
    
    /**
     * Récupère le type d'un espace
     */
    public Espace.EspaceType getEspaceTypeById(Long espaceId) {
        Espace espace = espaceRepository.findById(espaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found with id: " + espaceId));
        return espace.getType();
    }

    /**
     * Crée un nouvel espace
     */
    public EspaceDTO createEspace(EspaceDTO espaceDTO) {
        Espace espace = modelMapper.map(espaceDTO, Espace.class);
        Espace savedEspace = espaceRepository.save(espace);
        return modelMapper.map(savedEspace, EspaceDTO.class);
    }

    /**
     * Met à jour un espace existant
     */
    public EspaceDTO updateEspace(Long id, EspaceDTO espaceDTO) {
        Espace existingEspace = espaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found"));
        
        modelMapper.map(espaceDTO, existingEspace);
        Espace updatedEspace = espaceRepository.save(existingEspace);
        
        return modelMapper.map(updatedEspace, EspaceDTO.class);
    }

    /**
     * Supprime un espace
     */
    public void deleteEspace(Long id) {
        if (!espaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Espace not found");
        }
        espaceRepository.deleteById(id);
    }
}