package com.coworking.backend.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utilitaire pour le stockage des fichiers (version statique)
 */
@Component
public class FileStorageUtil {

    /**
     * Stocke un fichier dans le répertoire spécifié
     * @param file Fichier à stocker
     * @param uploadDir Répertoire de destination
     * @return Nom du fichier stocké
     * @throws IOException Si une erreur survient lors de l'écriture du fichier
     */
    public static String storeFile(MultipartFile file, String uploadDir) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}