package com.coworking.backend.exception;

/**
 * Exception levée lorsqu'un utilisateur n'est pas trouvé
 */
public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur par défaut avec un message générique
     */
    public UserNotFoundException() {
        super("Utilisateur non trouvé");
    }
    
    /**
     * @param message Le message personnalisé d'erreur
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
