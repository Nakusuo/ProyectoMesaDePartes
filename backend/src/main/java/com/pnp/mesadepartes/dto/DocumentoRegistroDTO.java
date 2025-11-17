package com.pnp.mesadepartes.dto;

import lombok.Data;

@Data
public class DocumentoRegistroDTO {
    private String titulo;
    private String descripcion;
    private String remitente;
    private String numeroDocumento;
    private Long idTipoDocumento;
    private String archivoUrl;
    private String numeroHt;
    private Long idUsuarioAsignado;
}