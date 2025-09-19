package com.coworking.backend.controller;

import com.coworking.backend.dto.ProfilDto;
import com.coworking.backend.dto.ProfileUpdateDTO;
import com.coworking.backend.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrôleur pour la gestion des profils utilisateurs
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Récupère le profil de l'utilisateur courant
     */
    @GetMapping
    public ResponseEntity<ProfilDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(profileService.getCurrentUser(userDetails.getUsername()));
    }

    /**
     * Met à jour le profil utilisateur
     */
    @PutMapping
    public ResponseEntity<ProfilDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateDTO updateDTO) {
        return ResponseEntity.ok(
            profileService.updateUserProfile(userDetails.getUsername(), updateDTO));
    }
 
    /**
     * Met à jour l'image de profil
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
            profileService.updateProfileImage(userDetails.getUsername(), file));
    }
}