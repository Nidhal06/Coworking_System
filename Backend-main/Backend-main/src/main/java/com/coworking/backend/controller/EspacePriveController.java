package com.coworking.backend.controller;

import com.coworking.backend.dto.EspacePriveDTO;
import com.coworking.backend.service.EspacePriveService;
import com.coworking.backend.util.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Contrôleur pour la gestion des espaces privés
 */
@RestController
@RequestMapping("/api/espaces/prives")
@RequiredArgsConstructor
public class EspacePriveController {

    private final EspacePriveService espacePriveService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    /**
     * Récupère tous les espaces privés (accessible à tous)
     */
    @GetMapping
    public ResponseEntity<List<EspacePriveDTO>> getAllEspacePrives() {
        return ResponseEntity.ok(espacePriveService.getAllEspacePrives());
    }

    /**
     * Récupère un espace privé par son ID (accessible à tous)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EspacePriveDTO> getEspacePriveById(@PathVariable Long id) {
        return ResponseEntity.ok(espacePriveService.getEspacePriveById(id));
    }

    /**
     * Crée un nouvel espace privé avec gestion des fichiers (admin seulement)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEspacePrive(
        @RequestPart("data") String espacePriveDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery) {

        try {
            EspacePriveDTO dto = parseEspaceDTO(espacePriveDTO);
            handleFileUploads(dto, photoPrincipal, gallery);
            return ResponseEntity.ok(espacePriveService.createEspacePrive(dto));
        } catch (JsonProcessingException e) {
            return badRequest("Invalid JSON data: " + e.getMessage());
        } catch (RuntimeException | IOException e) {
            return internalServerError(e.getMessage());
        }
    }

    /**
     * Met à jour un espace privé avec gestion des fichiers (admin seulement)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEspacePrive(
        @PathVariable Long id,
        @RequestPart("data") String espacePriveDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery,
        @RequestPart(value = "imagesToDelete", required = false) String imagesToDelete) {

        try {
            EspacePriveDTO dto = parseEspaceDTO(espacePriveDTO);
            validateIdConsistency(id, dto);
            
            handleFileUploads(dto, photoPrincipal, gallery);
            handleImageDeletion(imagesToDelete);
            
            return ResponseEntity.ok(espacePriveService.updateEspacePrive(id, dto));
        } catch (JsonProcessingException e) {
            return badRequest("Invalid JSON data: " + e.getMessage());
        } catch (RuntimeException | IOException e) {
            return internalServerError(e.getMessage());
        }
    }

    /**
     * Supprime un espace privé (admin seulement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEspacePrive(@PathVariable Long id) {
        espacePriveService.deleteEspacePrive(id);
        return ResponseEntity.noContent().build();
    }

    private EspacePriveDTO parseEspaceDTO(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, EspacePriveDTO.class);
    }

    private void handleFileUploads(EspacePriveDTO dto, MultipartFile photoPrincipal, MultipartFile[] gallery) 
            throws IOException {
        if (photoPrincipal != null) {
            dto.setPhotoPrincipal(fileStorageService.storeFile(photoPrincipal));
        }

        if (gallery != null && gallery.length > 0) {
            dto.setGallery(storeMultipleFiles(gallery));
        }
    }

    private List<String> storeMultipleFiles(MultipartFile[] files) throws IOException {
        return Arrays.stream(files)
                .map(file -> {
                    try {
                        return fileStorageService.storeFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store file", e);
                    }
                })
                .toList();
    }

    private void validateIdConsistency(Long pathId, EspacePriveDTO dto) {
        if (dto.getId() != null && !dto.getId().equals(pathId)) {
            throw new IllegalArgumentException("Path ID and DTO ID mismatch");
        }
    }

    private void handleImageDeletion(String imagesToDelete) throws JsonProcessingException {
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            List<String> images = objectMapper.readValue(imagesToDelete, 
                new TypeReference<List<String>>() {});
            fileStorageService.deleteFiles(images);
        }
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    private ResponseEntity<?> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
}