package com.coworking.backend.exception;

/**
 * Exception levée lorsqu'un token a déjà été utilisé
 */
public class TokenAlreadyUsedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur par défaut avec un message générique
     */
    public TokenAlreadyUsedException() {
        super("Ce token a déjà été utilisé");
    }
    
    /**
     * @param token Le token spécifique qui a déjà été utilisé
     */
    public TokenAlreadyUsedException(String token) {
        super("Token déjà utilisé : " + token);
    }
}
