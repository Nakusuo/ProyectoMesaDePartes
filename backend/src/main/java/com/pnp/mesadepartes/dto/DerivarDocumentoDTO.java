package com.pnp.mesadepartes.dto;

import lombok.Data;

@Data
public class DerivarDocumentoDTO {
    private Long idDocumento;
    private Long idAreaDestino;
    private Long idUsuarioRecibe;
    private String observaciones;
    private String prioridad = "NORMAL"; // BAJA, NORMAL, ALTA, URGENTE
}
