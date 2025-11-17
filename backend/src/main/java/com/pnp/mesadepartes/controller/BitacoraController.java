package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.model.Bitacora;
import com.pnp.mesadepartes.service.BitacoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bitacora")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BitacoraController {
    
    private final BitacoraService bitacoraService;
    
    /**
     * Obtener todas las entradas de bitácora con paginación
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerBitacora(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Bitacora> bitacoraPage = bitacoraService.obtenerTodos(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", bitacoraPage.getContent());
        response.put("currentPage", bitacoraPage.getNumber());
        response.put("totalPages", bitacoraPage.getTotalPages());
        response.put("totalElements", bitacoraPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar bitácora con filtros
     */
    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura')")
    public ResponseEntity<Map<String, Object>> filtrarBitacora(
            @RequestParam(required = false) String tipoOperacion,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Bitacora.TipoOperacion tipo = tipoOperacion != null ? Bitacora.TipoOperacion.valueOf(tipoOperacion) : null;
        
        Page<Bitacora> bitacoraPage = bitacoraService.buscarConFiltros(tipo, idUsuario, fechaInicio, fechaFin, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", bitacoraPage.getContent());
        response.put("currentPage", bitacoraPage.getNumber());
        response.put("totalPages", bitacoraPage.getTotalPages());
        response.put("totalElements", bitacoraPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar bitácora por código de documento
     */
    @GetMapping("/documento/{codigo}")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura', 'Mesa de Partes')")
    public ResponseEntity<List<Bitacora>> obtenerPorCodigoDocumento(@PathVariable String codigo) {
        List<Bitacora> bitacora = bitacoraService.buscarPorCodigoDocumento(codigo);
        return ResponseEntity.ok(bitacora);
    }
    
    /**
     * Buscar bitácora por ID de documento
     */
    @GetMapping("/documento/id/{idDocumento}")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura', 'Mesa de Partes')")
    public ResponseEntity<List<Bitacora>> obtenerPorIdDocumento(@PathVariable Long idDocumento) {
        List<Bitacora> bitacora = bitacoraService.buscarPorIdDocumento(idDocumento);
        return ResponseEntity.ok(bitacora);
    }
    
    /**
     * Obtener bitácora por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Jefatura')")
    public ResponseEntity<Bitacora> obtenerPorId(@PathVariable Long id) {
        Bitacora bitacora = bitacoraService.obtenerPorId(id);
        return ResponseEntity.ok(bitacora);
    }
}
