package com.pnp.mesadepartes.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 Helper para construir respuestas HTTP estandarizadas.
 Evita código duplicado en controllers.
 */
public class ResponseHelper {

    /**
     * Construye una respuesta de éxito genérica con el objeto de datos
     * 
     * @param data Objeto de datos a devolver en la respuesta
     * @return ResponseEntity con status 200 OK y los datos
     */
    public static ResponseEntity<?> success(Object data) {
        return ResponseEntity.ok(data);
    }

    /**
     * Construye una respuesta de éxito con mensaje personalizado y datos
     * 
     * @param message Mensaje descriptivo del éxito
     * @param data Objeto de datos a devolver
     * @return ResponseEntity con status 200 OK, mensaje, datos y timestamp
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
     * Construye una respuesta de error genérica con status 400
     * 
     * @param message Mensaje descriptivo del error
     * @return ResponseEntity con status 400 Bad Request y detalles del error
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
     * Construye una respuesta de error con excepción y status 400
     * 
     * @param message Mensaje descriptivo del contexto del error
     * @param e Excepción que causó el error
     * @return ResponseEntity con status 400 Bad Request, mensaje de error y detalles de la excepción
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
     * Construye una respuesta de recurso no encontrado con status 404
     * 
     * @param entity Nombre de la entidad que no fue encontrada
     * @return ResponseEntity con status 404 Not Found y mensaje descriptivo
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
     * Construye una respuesta de no autorizado con status 401
     * 
     * @param message Mensaje descriptivo del motivo de la falta de autorización
     * @return ResponseEntity con status 401 Unauthorized y detalles
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
     * Construye una respuesta de acceso prohibido con status 403
     * 
     * @param message Mensaje descriptivo del motivo del acceso prohibido
     * @return ResponseEntity con status 403 Forbidden y detalles
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
