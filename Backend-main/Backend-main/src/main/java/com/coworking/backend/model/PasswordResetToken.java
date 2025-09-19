package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entité pour la gestion des tokens de réinitialisation de mot de passe
 */
@Data
@Entity
@Table(name = "password_reset_token", indexes = {
    @Index(columnList = "user_id"),
    @Index(columnList = "token", unique = true)
})
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
}