package com.pnp.mesadepartes.dto;

import lombok.Data;

@Data
public class DocumentoRegistroDTO {
    // Datos del Documento
    private String titulo;
    private String descripcion;
    private String remitente; // El formulario público lo necesitará
    private String numeroDocumento;
    private Long idTipoDocumento;
    private String archivoUrl; // Por ahora solo la URL, luego vemos la subida

    // Datos de la Hoja de Trámite
    private String numeroHt;
    
    // Datos del Trámite (Asignación)
    private Long idUsuarioAsignado;
}