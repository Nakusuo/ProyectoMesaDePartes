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
    
    @Column(name = "ID_documento", nullable = false, unique = true)
    private Long idDocumento;
    
    @Column(name = "codigo_documento", nullable = false, length = 50)
    private String codigoDocumento;
    
    @Column(name = "titulo_documento", nullable = false, length = 200)
    private String tituloDocumento;
    
    @Column(name = "tipo_documento", length = 100)
    private String tipoDocumento;
    
    // ========== DATOS DE ENTRADA ==========
    @Column(name = "tiene_entrada", nullable = false)
    private Boolean tieneEntrada = false;
    
    @Column(name = "remitente", length = 200)
    private String remitente;
    
    @Column(name = "fecha_entrada")
    private LocalDateTime fechaEntrada;
    
    @Column(name = "usuario_entrada", length = 200)
    private String usuarioEntrada;
    
    @Column(name = "numero_documento_entrada", length = 100)
    private String numeroDocumentoEntrada;
    
    @Column(name = "archivo_entrada_url", length = 255)
    private String archivoEntradaUrl;
    
    // ========== DATOS DE SALIDA ==========
    @Column(name = "tiene_salida", nullable = false)
    private Boolean tieneSalida = false;
    
    @Column(name = "destinatario", length = 200)
    private String destinatario;
    
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;
    
    @Column(name = "usuario_salida", length = 200)
    private String usuarioSalida;
    
    @Column(name = "numero_documento_salida", length = 100)
    private String numeroDocumentoSalida;
    
    @Column(name = "observaciones_salida", columnDefinition = "TEXT")
    private String observacionesSalida;
    
    @Column(name = "archivo_salida_url", length = 255)
    private String archivoSalidaUrl;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
