package com.coworking.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée pour les requêtes incorrectes (annotée avec le statut HTTP 400)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message Le message détaillant l'erreur dans la requête
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * @param message Le message détaillant l'erreur
     * @param cause L'exception à l'origine de cette erreur
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
