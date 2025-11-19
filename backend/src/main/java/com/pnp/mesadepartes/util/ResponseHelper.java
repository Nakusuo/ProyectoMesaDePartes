package com.pnp.mesadepartes.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper para construir respuestas HTTP estandarizadas.
 * Evita código duplicado en controllers.
 */
public class ResponseHelper {

    /**
     * Respuesta de éxito genérica
     */
    public static ResponseEntity<?> success(Object data) {
        return ResponseEntity.ok(data);
    }

    /**
     * Respuesta de éxito con mensaje
     */
    public static ResponseEntity<?> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Respuesta de error genérica (400)
     */
    public static ResponseEntity<?> error(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Error de operación");
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Respuesta de error con excepción (400)
     */
    public static ResponseEntity<?> error(String message, Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("message", e.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Respuesta de no encontrado (404)
     */
    public static ResponseEntity<?> notFound(String entity) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "No encontrado");
        error.put("message", entity + " no encontrado");
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Respuesta de no autorizado (401)
     */
    public static ResponseEntity<?> unauthorized(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "No autorizado");
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Respuesta de prohibido (403)
     */
    public static ResponseEntity<?> forbidden(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Acceso prohibido");
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Respuesta de error de validación (422)
     */
    public static ResponseEntity<?> validationError(String message, Map<String, String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Error de validación");
        response.put("message", message);
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
}
