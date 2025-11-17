package com.pnp.mesadepartes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bitacora")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bitacora {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_bitacora")
    private Long idBitacora;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false)
    private TipoOperacion tipoOperacion;
    
    @Column(name = "ID_documento", nullable = false)
    private Long idDocumento;
    
    @Column(name = "codigo_documento", nullable = false, length = 50)
    private String codigoDocumento;
    
    @Column(name = "titulo_documento", nullable = false, length = 200)
    private String tituloDocumento;
    
    @Column(name = "tipo_documento", length = 100)
    private String tipoDocumento;
    
    @Column(name = "remitente", length = 200)
    private String remitente;
    
    @Column(name = "destinatario", length = 200)
    private String destinatario;
    
    @Column(name = "fecha_operacion", nullable = false)
    private LocalDateTime fechaOperacion;
    
    @Column(name = "ID_usuario_operacion")
    private Long idUsuarioOperacion;
    
    @Column(name = "usuario_nombre", length = 200)
    private String usuarioNombre;
    
    @Column(name = "numero_documento", length = 100)
    private String numeroDocumento;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "archivo_url", length = 255)
    private String archivoUrl;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TipoOperacion {
        ENTRADA,
        SALIDA
    }
}
