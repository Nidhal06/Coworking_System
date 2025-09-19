package com.coworking.backend.exception;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée
 */
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message Le message d'erreur générique
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec des détails sur la ressource non trouvée
     * @param resourceName Le type de ressource non trouvée
     * @param fieldName Le champ utilisé pour la recherche
     * @param fieldValue La valeur du champ utilisé pour la recherche
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}