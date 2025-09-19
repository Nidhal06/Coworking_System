package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Entité représentant un espace ouvert (hérite de Espace)
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EspaceOuvert extends Espace {
    
    @OneToMany(mappedBy = "espaceOuvert")
    private List<Abonnement> abonnements;
}