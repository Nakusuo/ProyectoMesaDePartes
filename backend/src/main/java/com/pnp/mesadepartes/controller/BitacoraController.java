package com.pnp.mesadepartes.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.model.Bitacora;
import com.pnp.mesadepartes.service.BitacoraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de Bitácora
 * Proporciona endpoints para auditoría y trazabilidad de documentos
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@RestController
@RequestMapping("/api/bitacora")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Tag(name = "Bitácora", description = "API para auditoría y trazabilidad de documentos")
public class BitacoraController {
    
    private static final Logger logger = LoggerFactory.getLogger(BitacoraController.class);
    
    private final BitacoraService bitacoraService;
    
    /**
     * Obtener todas las entradas de bitácora con paginación
     */
    @GetMapping
    @Operation(summary = "Listar bitácora", description = "Obtiene todas las entradas de bitácora con paginación")
    @ApiResponse(responseCode = "200", description = "Lista de bitácora obtenida exitosamente")
    public ResponseEntity<Map<String, Object>> obtenerBitacora(
            @RequestParam(defaultValue = "0") @Parameter(description = "Número de página") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Tamaño de página") int size) {
        
        logger.info("Obteniendo bitácora - Página: {}, Tamaño: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Bitacora> bitacoraPage = bitacoraService.obtenerTodos(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", bitacoraPage.getContent());
        response.put("currentPage", bitacoraPage.getNumber());
        response.put("totalPages", bitacoraPage.getTotalPages());
        response.put("totalElements", bitacoraPage.getTotalElements());
        
        logger.info("Bitácora obtenida: {} elementos en {} páginas", bitacoraPage.getTotalElements(), bitacoraPage.getTotalPages());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar bitácora con filtros
     */
    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura')")
    @Operation(summary = "Filtrar bitácora", description = "Busca entradas de bitácora con filtros personalizados")
    @ApiResponse(responseCode = "200", description = "Resultados filtrados obtenidos exitosamente")
    public ResponseEntity<Map<String, Object>> filtrarBitacora(
            @RequestParam(required = false) @Parameter(description = "Filtrar por entrada") Boolean tieneEntrada,
            @RequestParam(required = false) @Parameter(description = "Filtrar por salida") Boolean tieneSalida,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Filtrando bitácora - Entrada: {}, Salida: {}, Fechas: {} a {}", tieneEntrada, tieneSalida, fechaInicio, fechaFin);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Bitacora> bitacoraPage = bitacoraService.buscarConFiltros(tieneEntrada, tieneSalida, fechaInicio, fechaFin, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", bitacoraPage.getContent());
        response.put("currentPage", bitacoraPage.getNumber());
        response.put("totalPages", bitacoraPage.getTotalPages());
        response.put("totalElements", bitacoraPage.getTotalElements());
        
        logger.info("Filtrado completado: {} resultados encontrados", bitacoraPage.getTotalElements());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar bitácora por código de documento
     */
    @GetMapping("/documento/{codigo}")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura', 'Mesa de Partes')")
    @Operation(summary = "Buscar por código", description = "Obtiene la bitácora de un documento específico por su código")
    @ApiResponse(responseCode = "200", description = "Bitácora encontrada")
    public ResponseEntity<List<Bitacora>> obtenerPorCodigoDocumento(
            @PathVariable @Parameter(description = "Código del documento") String codigo) {
        logger.info("Buscando bitácora para documento con código: {}", codigo);
        List<Bitacora> bitacora = bitacoraService.buscarPorCodigoDocumento(codigo);
        logger.info("Se encontraron {} registros de bitácora para el código: {}", bitacora.size(), codigo);
        return ResponseEntity.ok(bitacora);
    }
    
    /**
     * Buscar bitácora por ID de documento
     */
    @GetMapping("/documento/id/{idDocumento}")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura', 'Mesa de Partes')")
    @Operation(summary = "Buscar por ID de documento", description = "Obtiene la bitácora de un documento por su ID")
    @ApiResponse(responseCode = "200", description = "Bitácora encontrada")
    public ResponseEntity<Bitacora> obtenerPorIdDocumento(
            @PathVariable @Parameter(description = "ID del documento") Long idDocumento) {
        logger.info("Buscando bitácora para documento con ID: {}", idDocumento);
        Bitacora bitacora = bitacoraService.buscarPorIdDocumento(idDocumento);
        return ResponseEntity.ok(bitacora);
    }
    
    /**
     * Obtener bitácora por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura')")
    @Operation(summary = "Obtener por ID", description = "Obtiene una entrada de bitácora específica por su ID")
    @ApiResponse(responseCode = "200", description = "Bitácora encontrada")
    public ResponseEntity<Bitacora> obtenerPorId(
            @PathVariable @Parameter(description = "ID de la bitácora") Long id) {
        logger.info("Obteniendo bitácora con ID: {}", id);
        Bitacora bitacora = bitacoraService.obtenerPorId(id);
        return ResponseEntity.ok(bitacora);
    }
    
    /**
     * Exportar bitácora a PDF
     */
    @GetMapping("/exportar/pdf")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Exportar a PDF", description = "Exporta la bitácora completa a formato PDF")
    @ApiResponse(responseCode = "200", description = "PDF generado exitosamente")
    public ResponseEntity<byte[]> exportarPDF() {
        logger.info("Exportando bitácora a PDF");
        try {
            byte[] pdfBytes = bitacoraService.exportarPDF();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=bitacora.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            logger.error("Error al exportar bitácora a PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Exportar bitácora a Excel
     */
    @GetMapping("/exportar/excel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Exportar a Excel", description = "Exporta la bitácora completa a formato Excel")
    @ApiResponse(responseCode = "200", description = "Excel generado exitosamente")
    public ResponseEntity<byte[]> exportarExcel() {
        logger.info("Exportando bitácora a Excel");
        try {
            byte[] excelBytes = bitacoraService.exportarExcel();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=bitacora.xlsx")
                    .body(excelBytes);
        } catch (Exception e) {
            logger.error("Error al exportar bitácora a Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
