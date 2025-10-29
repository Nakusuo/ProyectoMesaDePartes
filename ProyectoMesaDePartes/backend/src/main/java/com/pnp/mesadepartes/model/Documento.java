package com.pnp.mesadepartes.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_documento")
    private Long idDocumento;

    @Column(name = "numero_registro", nullable = false, unique = true)
    private Integer numeroRegistro;

    @Column(name = "fecha_documento", nullable = false)
    private LocalDateTime fechaDocumento;

    @Lob
    @Column(nullable = false)
    private String asunto;

    @Column(name = "numero_documento", length = 100)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Registrado','En Proceso','Observado','Finalizado','Salida') DEFAULT 'Registrado'")
    private EstadoDocumento estado = EstadoDocumento.Registrado;

    @Column(nullable = false, length = 200)
    private String remitente;

    @Column(name = "archivo_url", length = 255)
    private String archivoUrl;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_usuario_registro")
    private Usuario usuarioRegistro;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_tipo_documento")
    private TipoDocumento tipoDocumento;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
