package com.pnp.mesadepartes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/derivaciones")
public class DerivacionController {

    @Autowired
    private DerivacionService derivacionService;

    @PostMapping("/derivar")
    public ResponseEntity<?> derivarDocumento(@RequestBody DerivarDocumentoDTO dto,
                                              @RequestParam Long idUsuarioDeriva) {
        try {
            Derivacion derivacion = derivacionService.derivarDocumento(dto, idUsuarioDeriva);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento derivado exitosamente");
            response.put("derivacion", derivacion);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al derivar documento");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/recibir/{idDerivacion}")
    public ResponseEntity<?> recibirDerivacion(@PathVariable Long idDerivacion,
                                               @RequestParam Long idUsuarioRecibe) {
        try {
            Derivacion derivacion = derivacionService.recibirDerivacion(idDerivacion, idUsuarioRecibe);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Derivación recibida exitosamente");
            response.put("derivacion", derivacion);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al recibir derivación");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/documento/{idDocumento}")
    public ResponseEntity<List<Derivacion>> obtenerDerivacionesPorDocumento(@PathVariable Long idDocumento) {
        List<Derivacion> derivaciones = derivacionService.obtenerDerivacionesPorDocumento(idDocumento);
        return ResponseEntity.ok(derivaciones);
    }

    @GetMapping("/area/{idArea}")
    public ResponseEntity<List<Derivacion>> obtenerDerivacionesPorArea(@PathVariable Long idArea) {
        List<Derivacion> derivaciones = derivacionService.obtenerDerivacionesPorArea(idArea);
        return ResponseEntity.ok(derivaciones);
    }

    @GetMapping("/trazabilidad/{idDocumento}")
    public ResponseEntity<TrazabilidadDTO> obtenerTrazabilidad(@PathVariable Long idDocumento) {
        try {
            TrazabilidadDTO trazabilidad = derivacionService.obtenerTrazabilidad(idDocumento);
            return ResponseEntity.ok(trazabilidad);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
