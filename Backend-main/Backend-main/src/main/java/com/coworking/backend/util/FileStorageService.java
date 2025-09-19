package com.coworking.backend.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Service pour le stockage et la gestion des fichiers sur le système de fichiers local
 */
@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Stocke un fichier uploadé sur le système de fichiers
     * @param file Fichier à stocker
     * @return URL relative du fichier stocké
     * @throws IOException Si une erreur survient lors de l'écriture du fichier
     */
    public String storeFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String newFilename = UUID.randomUUID() + "." + extension;
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        return "/uploads/" + newFilename;
    }
    
    /**
     * Supprime une liste de fichiers du système de fichiers
     * @param fileUrls Liste des URLs des fichiers à supprimer
     * @throws RuntimeException Si une erreur survient lors de la suppression
     */
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }

        for (String fileUrl : fileUrls) {
            try {
                String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + fileUrl, e);
            }
        }
    }
}