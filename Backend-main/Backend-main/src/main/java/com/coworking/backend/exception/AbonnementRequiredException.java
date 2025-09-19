package com.coworking.backend.exception;

/**
 * Exception levée lorsqu'un abonnement est requis pour effectuer une action
 */
public class AbonnementRequiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message Le message expliquant quel abonnement est requis
     */
    public AbonnementRequiredException(String message) {
        super(message);
    }
}