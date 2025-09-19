package com.coworking.backend.controller;

import com.coworking.backend.dto.EspaceDTO;
import com.coworking.backend.service.EspaceService;
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
@RequestMapping("/api/espaces")
@RequiredArgsConstructor
public class EspaceController {

    private final EspaceService espaceService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<EspaceDTO>> getAllEspaces() {
        return ResponseEntity.ok(espaceService.getAllEspaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspaceDTO> getEspaceById(@PathVariable Long id) {
        return ResponseEntity.ok(espaceService.getEspaceById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEspace(
        @RequestPart("data") String espaceDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            EspaceDTO dto = mapper.readValue(espaceDTO, EspaceDTO.class);
            
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
            
            return ResponseEntity.ok(espaceService.createEspace(dto));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEspace(
        @PathVariable Long id,
        @RequestPart("data") String espaceDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery,
        @RequestPart(value = "imagesToDelete", required = false) String imagesToDelete) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            EspaceDTO dto = mapper.readValue(espaceDTO, EspaceDTO.class);
            
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
            
            return ResponseEntity.ok(espaceService.updateEspace(id, dto));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEspace(@PathVariable Long id) {
        espaceService.deleteEspace(id);
        return ResponseEntity.noContent().build();
    }
}