package com.pnp.mesadepartes.dto;

import lombok.Data;

@Data
public class DocumentoRegistroDTO {
    // Campos que se combinan en "asunto" en la BD
    private String titulo;       // Se combinará con descripcion
    private String descripcion;  // Se combinará con titulo
    
    private String remitente;
    private String numeroDocumento;
    private Long idTipoDocumento;
    private String archivoUrl;
    private String numeroHt;
    private Long idUsuarioAsignado;
}