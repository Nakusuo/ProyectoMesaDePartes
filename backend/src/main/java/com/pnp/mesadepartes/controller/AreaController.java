package com.pnp.mesadepartes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.exception.EntityNotFoundException;
import com.pnp.mesadepartes.exception.ValidationException;
import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.repository.AreaRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de Áreas/Dependencias
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Áreas", description = "API para gestión de áreas y dependencias de la institución")
public class AreaController {

    private static final Logger logger = LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private AreaRepository areaRepository;

    /**
     * Obtiene todas las áreas registradas
     * 
     * @return Lista de todas las áreas
     */
    @GetMapping
    @Operation(summary = "Listar todas las áreas", description = "Obtiene un listado completo de todas las áreas/dependencias registradas")
    @ApiResponse(responseCode = "200", description = "Lista de áreas obtenida exitosamente")
    public ResponseEntity<List<Area>> getAllAreas() {
        logger.info("Solicitando listado de todas las áreas");
        List<Area> areas = areaRepository.findAll();
        logger.info("Se encontraron {} áreas", areas.size());
        return ResponseEntity.ok(areas);
    }

    /**
     * Crea una nueva área
     * 
     * @param area Datos del área a crear
     * @return Área creada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Administrador')")
    @Operation(summary = "Crear nueva área", description = "Registra una nueva área/dependencia en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Área creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Area> createArea(
            @Valid @RequestBody @Parameter(description = "Datos del área a crear") Area area) {
        
        logger.info("Creando nueva área: {}", area.getNombre());
        
        // Validaciones adicionales
        if (area.getNombre() == null || area.getNombre().trim().isEmpty()) {
            logger.warn("Intento de crear área sin nombre");
            throw new ValidationException("El nombre del área es obligatorio");
        }
        
        if (area.getNombre().length() < 3) {
            logger.warn("Intento de crear área con nombre muy corto: {}", area.getNombre());
            throw new ValidationException("El nombre del área debe tener al menos 3 caracteres");
        }
        
        Area nuevaArea = areaRepository.save(area);
        logger.info("Área creada exitosamente con ID: {}", nuevaArea.getIdArea());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaArea);
    }

    /**
     * Actualiza un área existente
     * 
     * @param id ID del área a actualizar
     * @param areaDetails Nuevos datos del área
     * @return Área actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador')")
    @Operation(summary = "Actualizar área", description = "Actualiza los datos de un área existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Área no encontrada"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Area> updateArea(
            @PathVariable @Parameter(description = "ID del área") Long id,
            @Valid @RequestBody @Parameter(description = "Nuevos datos del área") Area areaDetails) {
        
        logger.info("Actualizando área con ID: {}", id);
        
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Área no encontrada con ID: {}", id);
                    return new EntityNotFoundException("Área no encontrada con ID: " + id);
                });
        
        // Validaciones
        if (areaDetails.getNombre() != null && !areaDetails.getNombre().trim().isEmpty()) {
            area.setNombre(areaDetails.getNombre());
        }
        
        if (areaDetails.getSigla() != null) {
            area.setSigla(areaDetails.getSigla());
        }
        
        Area updatedArea = areaRepository.save(area);
        logger.info("Área actualizada exitosamente: {}", updatedArea.getNombre());
        
        return ResponseEntity.ok(updatedArea);
    }

    /**
     * Elimina un área
     * 
     * @param id ID del área a eliminar
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador')")
    @Operation(summary = "Eliminar área", description = "Elimina un área del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Área no encontrada"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Map<String, String>> deleteArea(
            @PathVariable @Parameter(description = "ID del área a eliminar") Long id) {
        
        logger.info("Eliminando área con ID: {}", id);
        
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Área no encontrada con ID: {}", id);
                    return new EntityNotFoundException("Área no encontrada con ID: " + id);
                });
        
        areaRepository.delete(area);
        logger.info("Área eliminada exitosamente: {}", area.getNombre());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Área eliminada exitosamente");
        response.put("nombre", area.getNombre());
        
        return ResponseEntity.ok(response);
    }
}

