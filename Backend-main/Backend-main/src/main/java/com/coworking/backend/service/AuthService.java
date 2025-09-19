package com.coworking.backend.service;

import com.coworking.backend.config.JwtTokenUtil;
import com.coworking.backend.dto.AuthRequest;
import com.coworking.backend.dto.AuthResponse;
import com.coworking.backend.dto.SignupRequest;
import com.coworking.backend.exception.BadRequestException;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.exception.UnauthorizedException;
import com.coworking.backend.model.PasswordResetToken;
import com.coworking.backend.model.User;
import com.coworking.backend.repository.PasswordResetTokenRepository;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.util.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

/**
 * Service d'authentification et de gestion des comptes utilisateurs.
 * Gère :
 * - L'authentification JWT
 * - L'inscription
 * - La réinitialisation de mot de passe
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.reset-password.expiration-hours}")
    private int resetTokenExpirationHours;

    /**
     * Authentifie un utilisateur et génère un token JWT
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            String token = jwtTokenUtil.generateToken(userDetails);
            
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
            
            return new AuthResponse(token, user.getEmail(), user.getType().name(), user.getId());
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }
    
    /**
     * Enregistre un nouvel utilisateur
     */
    public User registerUser(SignupRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        if(userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new BadRequestException("Phone Number is already in use!");
        }

        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setPhone(signUpRequest.getPhone());
        user.setType(User.UserType.COWORKER);
        user.setEnabled(true);

        return userRepository.save(user);
    }
    
    
    /**
     * Lance le processus de réinitialisation de mot de passe
     */
    @Transactional
    public void processForgotPassword(String email) {
        passwordResetTokenRepository.deleteAllExpired(); // Nettoyer les tokens expirés
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Supprimer les anciens tokens de l'utilisateur
        passwordResetTokenRepository.deleteAllByUserId(user.getId());

        // Créer et sauvegarder le nouveau token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(resetTokenExpirationHours));

        // Sauvegarde FORCÉE en base avant l'envoi de l'e-mail
        PasswordResetToken savedToken = passwordResetTokenRepository.saveAndFlush(resetToken);

        // Envoyer l'e-mail avec le token
        sendResetPasswordEmail(user, savedToken.getToken());
    }

    /**
     * Réinitialise le mot de passe avec un token valide
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // First find the token without deleting expired ones
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Token not found: {}", token);
                    return new BadRequestException("Token invalide ou expiré");
                });

        // Check if token is expired
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BadRequestException("Token expiré");
        }

        // Process the password reset
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Delete the used token
        passwordResetTokenRepository.delete(resetToken);
        
        // Clean up expired tokens (but don't let it affect current operation)
        try {
            passwordResetTokenRepository.deleteAllExpired();
        } catch (Exception e) {
            log.warn("Failed to clean expired tokens", e);
        }
    }

    /**
     * Envoie l'email de réinitialisation de mot de passe
     */
    private void sendResetPasswordEmail(User user, String token) {
        try {
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
            String resetUrl = "http://localhost:4200/reset-password?token=" + encodedToken;

            Context context = new Context();
            context.setVariable("name", user.getFirstName());
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("expirationHours", resetTokenExpirationHours);

            emailService.sendTemplateEmail(
                user.getEmail(),
                "Réinitialisation de votre mot de passe",
                "email/reset-password",
                context
            );
        } catch (Exception e) {
            log.error("Failed to send reset password email", e);
            throw new RuntimeException("Failed to send reset password email", e);
        }
    }
}