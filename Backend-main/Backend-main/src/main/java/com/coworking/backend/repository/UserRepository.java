package com.coworking.backend.repository;

import com.coworking.backend.model.User;
import com.coworking.backend.model.User.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs.
 * Fournit des méthodes de recherche personnalisées en plus des opérations CRUD standard.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Recherche d'un utilisateur par email
    Optional<User> findByEmail(String email);
    
    // Vérification de l'existence d'un email
    boolean existsByEmail(String email);
    
    // Vérification de l'existence d'un nom d'utilisateur
    Boolean existsByUsername(String username);
    
    // Recherche d'un utilisateur par nom d'utilisateur
    Optional<User> findByUsername(String username);
    
    // Vérification de l'existence d'un numéro de téléphone
    Boolean existsByPhone(String phone);
    
    // Recherche des utilisateurs par type
    List<User> findByType(UserType type);
}