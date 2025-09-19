package com.coworking.backend.service;

import com.coworking.backend.dto.EspaceOuvertDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.EspaceOuvert;
import com.coworking.backend.repository.EspaceOuvertRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des espaces ouverts.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EspaceOuvertService {

    private final EspaceOuvertRepository espaceOuvertRepository;
    private final ModelMapper modelMapper;

    /**
     * Récupère tous les espaces ouverts
     */
    public List<EspaceOuvertDTO> getAllEspaceOuverts() {
        return espaceOuvertRepository.findAll().stream()
                .map(espace -> modelMapper.map(espace, EspaceOuvertDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un espace ouvert par son ID
     */
    public EspaceOuvertDTO getEspaceOuvertById(Long id) {
        EspaceOuvert espaceOuvert = espaceOuvertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EspaceOuvert not found"));
        return modelMapper.map(espaceOuvert, EspaceOuvertDTO.class);
    }

    /**
     * Crée un nouvel espace ouvert
     */
    public EspaceOuvertDTO createEspaceOuvert(EspaceOuvertDTO espaceOuvertDTO) {
        EspaceOuvert espaceOuvert = modelMapper.map(espaceOuvertDTO, EspaceOuvert.class);
        EspaceOuvert savedEspaceOuvert = espaceOuvertRepository.save(espaceOuvert);
        return modelMapper.map(savedEspaceOuvert, EspaceOuvertDTO.class);
    }

    /**
     * Met à jour un espace ouvert existant
     */
    public EspaceOuvertDTO updateEspaceOuvert(Long id, EspaceOuvertDTO espaceOuvertDTO) {
        EspaceOuvert existingEspaceOuvert = espaceOuvertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EspaceOuvert not found"));
        
        modelMapper.map(espaceOuvertDTO, existingEspaceOuvert);
        EspaceOuvert updatedEspaceOuvert = espaceOuvertRepository.save(existingEspaceOuvert);
        
        return modelMapper.map(updatedEspaceOuvert, EspaceOuvertDTO.class);
    }

    /**
     * Supprime un espace ouvert
     */
    public void deleteEspaceOuvert(Long id) {
        if (!espaceOuvertRepository.existsById(id)) {
            throw new ResourceNotFoundException("EspaceOuvert not found");
        }
        espaceOuvertRepository.deleteById(id);
    }
}