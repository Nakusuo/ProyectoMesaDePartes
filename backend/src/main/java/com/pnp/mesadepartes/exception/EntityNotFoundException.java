package com.pnp.mesadepartes.exception;

/**
 * Excepción lanzada cuando una entidad no es encontrada.
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entity, Long id) {
        super(String.format("%s no encontrado con ID: %d", entity, id));
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
