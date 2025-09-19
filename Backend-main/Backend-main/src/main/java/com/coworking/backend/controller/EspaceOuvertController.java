package com.coworking.backend.controller;

import com.coworking.backend.dto.EspaceOuvertDTO;
import com.coworking.backend.service.EspaceOuvertService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/espaces/ouverts")
@RequiredArgsConstructor
public class EspaceOuvertController {

    private final EspaceOuvertService espaceOuvertService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<EspaceOuvertDTO>> getAllEspaceOuverts() {
        return ResponseEntity.ok(espaceOuvertService.getAllEspaceOuverts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspaceOuvertDTO> getEspaceOuvertById(@PathVariable Long id) {
        return ResponseEntity.ok(espaceOuvertService.getEspaceOuvertById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEspaceOuvert(
        @RequestPart("data") String espaceOuvertDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            EspaceOuvertDTO dto = mapper.readValue(espaceOuvertDTO, EspaceOuvertDTO.class);

            if (photoPrincipal != null) {
                try {
                    String photoPath = fileStorageService.storeFile(photoPrincipal);
                    dto.setPhotoPrincipal(photoPath);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to store main photo: " + e.getMessage());
                }
            }

            if (gallery != null && gallery.length > 0) {
                List<String> galleryPaths = Arrays.stream(gallery)
                    .map(file -> {
                        try {
                            return fileStorageService.storeFile(file);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to store gallery file", e);
                        }
                    })
                    .collect(Collectors.toList());
                dto.setGallery(galleryPaths);
            }

            return ResponseEntity.ok(espaceOuvertService.createEspaceOuvert(dto));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEspaceOuvert(
        @PathVariable Long id,
        @RequestPart("data") String espaceOuvertDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery,
        @RequestPart(value = "imagesToDelete", required = false) String imagesToDelete) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            EspaceOuvertDTO dto = mapper.readValue(espaceOuvertDTO, EspaceOuvertDTO.class);

            if (photoPrincipal != null) {
                try {
                    String photoPath = fileStorageService.storeFile(photoPrincipal);
                    dto.setPhotoPrincipal(photoPath);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to store main photo: " + e.getMessage());
                }
            }

            if (gallery != null && gallery.length > 0) {
                List<String> galleryPaths = Arrays.stream(gallery)
                    .map(file -> {
                        try {
                            return fileStorageService.storeFile(file);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to store gallery file", e);
                        }
                    })
                    .collect(Collectors.toList());
                dto.setGallery(galleryPaths);
            }

            if (imagesToDelete != null) {
                List<String> imagesToDeleteList = mapper.readValue(imagesToDelete, new TypeReference<List<String>>() {});
                fileStorageService.deleteFiles(imagesToDeleteList);
            }

            return ResponseEntity.ok(espaceOuvertService.updateEspaceOuvert(id, dto));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEspaceOuvert(@PathVariable Long id) {
        espaceOuvertService.deleteEspaceOuvert(id);
        return ResponseEntity.noContent().build();
    }
}