package com.pnp.mesadepartes.dto;

import com.pnp.mesadepartes.model.Documento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoBitacoraDTO {
    private Documento documento;
    private String usuarioAsignado; // Nombre completo del usuario asignado
    private Long idUsuarioAsignado; // ID del usuario asignado
}
