package com.pnp.mesadepartes.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "salidas_documento")
public class SalidaDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_salida_documento")
    private Long idSalidaDocumento;

    @Column(name = "numero_documento_salida", length = 100)
    private String numeroDocumentoSalida;

    @Column(name = "destinatario_salida", length = 200)
    private String destinatarioSalida;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Lob
    private String observacion;

    @Column(name = "archivo_cargo_url", length = 255)
    private String archivoCargoUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_documento", nullable = false)
    private Documento documento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_tipo_documento")
    private TipoDocumento tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_usuario_salida")
    private Usuario usuarioSalida;
}
