package com.coworking.backend.exception;

/**
 * Exception levée lorsqu'un token est invalide ou expiré
 */
public class InvalidTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur par défaut avec un message générique
     */
    public InvalidTokenException() {
        super("Token invalide ou expiré");
    }
    
    /**
     * Constructeur avec le token spécifique qui est invalide
     * @param token Le token invalide
     */
    public InvalidTokenException(String token) {
        super("Token invalide : " + token);
    }
}
