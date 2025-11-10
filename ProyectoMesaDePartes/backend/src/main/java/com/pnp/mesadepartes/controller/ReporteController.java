package com.pnp.mesadepartes.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pnp.mesadepartes.dto.ReporteDTO;
import com.pnp.mesadepartes.service.ReporteService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping("/generar")
    public ResponseEntity<?> generarReporte(@RequestBody ReporteDTO reporteDTO) {
        try {
            byte[] reporte = reporteService.generarReporte(reporteDTO);
            
            HttpHeaders headers = new HttpHeaders();
            String filename = "reporte_" + reporteDTO.getTipoReporte() + "_" + 
                            System.currentTimeMillis();
            
            if ("EXCEL".equalsIgnoreCase(reporteDTO.getFormato())) {
                headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDispositionFormData("attachment", filename + ".xlsx");
            } else {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", filename + ".pdf");
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reporte);
                    
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar reporte");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> estadisticas = reporteService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }
}
