package com.pnp.mesadepartes.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TrazabilidadDTO {
    private Long idDocumento;
    private String codigo;
    private String titulo;
    private String estadoActual;
    private LocalDateTime fechaRegistro;
    private String remitente;
    private String tipoDocumento;
    private List<MovimientoDTO> movimientos;
    private EstadisticasDTO estadisticas;
    
    @Data
    public static class MovimientoDTO {
        private Long id;
        private String tipo; // REGISTRO, DERIVACION, CAMBIO_ESTADO
        private LocalDateTime fecha;
        private String descripcion;
        private String usuario;
        private String areaOrigen;
        private String areaDestino;
        private String estado;
        private Long tiempoEnArea; // en horas
    }
    
    @Data
    public static class EstadisticasDTO {
        private Long tiempoTotalHoras;
        private Integer totalDerivaciones;
        private Integer totalAreas;
        private String areaActual;
        private String usuarioActual;
    }
}
