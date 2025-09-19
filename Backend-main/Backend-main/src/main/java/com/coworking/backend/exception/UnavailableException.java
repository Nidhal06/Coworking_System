package com.coworking.backend.exception;

/**
 * Exception levée lorsqu'une ressource n'est pas disponible
 */
public class UnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message Le message détaillant la raison de l'indisponibilité
     */
    public UnavailableException(String message) {
        super(message);
    }
}