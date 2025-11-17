package com.pnp.mesadepartes.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReporteDTO {
    private String tipoReporte; // DOCUMENTOS, TIEMPOS, AREAS, USUARIOS
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long idArea;
    private String estado;
    private String formato; // PDF, EXCEL
}
