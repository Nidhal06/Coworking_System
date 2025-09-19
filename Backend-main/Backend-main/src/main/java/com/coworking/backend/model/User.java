package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entité représentant un utilisateur du système
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private boolean enabled;
    private String profileImagePath;
    
    @Enumerated(EnumType.STRING)
    private UserType type;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Abonnement> abonnements;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avis> avis;
    
    @ManyToMany(mappedBy = "participants")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Evenement> evenements = new HashSet<>();
    
    /**
     * Enumération des types d'utilisateurs
     */
    public enum UserType {
        ADMIN, COWORKER, RECEPTIONISTE
    }
}