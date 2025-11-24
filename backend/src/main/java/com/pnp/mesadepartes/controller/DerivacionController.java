package com.pnp.mesadepartes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.dto.DerivarDocumentoDTO;
import com.pnp.mesadepartes.dto.TrazabilidadDTO;
import com.pnp.mesadepartes.model.Derivacion;
import com.pnp.mesadepartes.service.DerivacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de Derivaciones de Documentos
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/derivaciones")
@Tag(name = "Derivaciones", description = "API para gestión de derivación y trazabilidad de documentos")
public class DerivacionController {

    private static final Logger logger = LoggerFactory.getLogger(DerivacionController.class);

    @Autowired
    private DerivacionService derivacionService;

    /**
     * Deriva un documento a otro usuario o área
     * 
     * @param dto Datos de la derivación
     * @param idUsuarioDeriva ID del usuario que realiza la derivación
     * @return Derivación creada
     */
    @PostMapping("/derivar")
    @Operation(summary = "Derivar documento", description = "Deriva un documento a otro usuario o área")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento derivado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en la derivación")
    })
    public ResponseEntity<?> derivarDocumento(
            @Valid @RequestBody @Parameter(description = "Datos de la derivación") DerivarDocumentoDTO dto,
            @RequestParam @Parameter(description = "ID del usuario que deriva") Long idUsuarioDeriva) {
        logger.info("Derivando documento ID: {} por usuario ID: {}", dto.getIdDocumento(), idUsuarioDeriva);
        try {
            Derivacion derivacion = derivacionService.derivarDocumento(dto, idUsuarioDeriva);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento derivado exitosamente");
            response.put("derivacion", derivacion);
            
            logger.info("Documento derivado exitosamente. Derivación ID: {}", derivacion.getIdDerivacion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al derivar documento: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al derivar documento");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Marca una derivación como recibida
     * 
     * @param idDerivacion ID de la derivación
     * @param idUsuarioRecibe ID del usuario que recibe
     * @return Derivación actualizada
     */
    @PutMapping("/recibir/{idDerivacion}")
    @Operation(summary = "Recibir derivación", description = "Marca una derivación como recibida por el usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Derivación recibida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al recibir derivación")
    })
    public ResponseEntity<?> recibirDerivacion(
            @PathVariable @Parameter(description = "ID de la derivación") Long idDerivacion,
            @RequestParam @Parameter(description = "ID del usuario que recibe") Long idUsuarioRecibe) {
        logger.info("Recibiendo derivación ID: {} por usuario ID: {}", idDerivacion, idUsuarioRecibe);
        try {
            Derivacion derivacion = derivacionService.recibirDerivacion(idDerivacion, idUsuarioRecibe);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Derivación recibida exitosamente");
            response.put("derivacion", derivacion);
            
            logger.info("Derivación recibida exitosamente: {}", idDerivacion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al recibir derivación: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al recibir derivación");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtiene todas las derivaciones de un documento
     * 
     * @param idDocumento ID del documento
     * @return Lista de derivaciones del documento
     */
    @GetMapping("/documento/{idDocumento}")
    @Operation(summary = "Derivaciones por documento", description = "Obtiene todas las derivaciones de un documento específico")
    @ApiResponse(responseCode = "200", description = "Lista de derivaciones obtenida exitosamente")
    public ResponseEntity<List<Derivacion>> obtenerDerivacionesPorDocumento(
            @PathVariable @Parameter(description = "ID del documento") Long idDocumento) {
        logger.info("Obteniendo derivaciones para documento ID: {}", idDocumento);
        List<Derivacion> derivaciones = derivacionService.obtenerDerivacionesPorDocumento(idDocumento);
        logger.info("Se encontraron {} derivaciones para el documento {}", derivaciones.size(), idDocumento);
        return ResponseEntity.ok(derivaciones);
    }

    /**
     * Obtiene todas las derivaciones de un área
     * 
     * @param idArea ID del área
     * @return Lista de derivaciones del área
     */
    @GetMapping("/area/{idArea}")
    @Operation(summary = "Derivaciones por área", description = "Obtiene todas las derivaciones de un área específica")
    @ApiResponse(responseCode = "200", description = "Lista de derivaciones obtenida exitosamente")
    public ResponseEntity<List<Derivacion>> obtenerDerivacionesPorArea(
            @PathVariable @Parameter(description = "ID del área") Long idArea) {
        logger.info("Obteniendo derivaciones para área ID: {}", idArea);
        List<Derivacion> derivaciones = derivacionService.obtenerDerivacionesPorArea(idArea);
        logger.info("Se encontraron {} derivaciones para el área {}", derivaciones.size(), idArea);
        return ResponseEntity.ok(derivaciones);
    }

    /**
     * Obtiene la trazabilidad complete de un documento
     * 
     * @param idDocumento ID del documento
     * @return Trazabilidad del documento con todas sus derivaciones
     */
    @GetMapping("/trazabilidad/{idDocumento}")
    @Operation(summary = "Obtener trazabilidad", description = "Obtiene el historial completo de derivaciones de un documento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trazabilidad obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<TrazabilidadDTO> obtenerTrazabilidad(
            @PathVariable @Parameter(description = "ID del documento") Long idDocumento) {
        logger.info("Obteniendo trazabilidad para documento ID: {}", idDocumento);
        try {
            TrazabilidadDTO trazabilidad = derivacionService.obtenerTrazabilidad(idDocumento);
            logger.info("Trazabilidad obtenida exitosamente para documento {}", idDocumento);
            return ResponseEntity.ok(trazabilidad);
        } catch (Exception e) {
            logger.error("Error al obtener trazabilidad: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
