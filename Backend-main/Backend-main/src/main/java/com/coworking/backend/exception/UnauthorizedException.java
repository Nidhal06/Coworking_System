package com.coworking.backend.exception;

/**
 * Exception levée pour les accès non autorisés
 */
public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message Le message détaillant la raison du refus d'accès
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}