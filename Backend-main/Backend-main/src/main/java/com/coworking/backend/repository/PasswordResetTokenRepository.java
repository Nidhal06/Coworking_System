package com.coworking.backend.repository;

import com.coworking.backend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * Repository pour la gestion des tokens de réinitialisation de mot de passe.
 * Contient des méthodes pour gérer le cycle de vie des tokens.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Trouve un token valide (non expiré) par sa valeur.
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token AND t.expiryDate > CURRENT_TIMESTAMP")
    Optional<PasswordResetToken> findValidByToken(@Param("token") String token);
    
    /**
     * Supprime tous les tokens pour un utilisateur donné.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
    
    /**
     * Supprime tous les tokens expirés.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate <= CURRENT_TIMESTAMP")
    void deleteAllExpired();
    
    // Trouve un token par sa valeur (sans vérification d'expiration)
    Optional<PasswordResetToken> findByToken(String token);
}