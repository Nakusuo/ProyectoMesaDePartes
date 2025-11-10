package com.pnp.mesadepartes.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "derivaciones")
public class Derivacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_derivacion")
    private Long idDerivacion;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_documento", nullable = false)
    private Documento documento;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_area_origen")
    private Area areaOrigen;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_area_destino", nullable = false)
    private Area areaDestino;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_usuario_deriva", nullable = false)
    private Usuario usuarioDeriva;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_usuario_recibe")
    private Usuario usuarioRecibe;

    @CreationTimestamp
    @Column(name = "fecha_derivacion", nullable = false)
    private LocalDateTime fechaDerivacion;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    @Column(nullable = false)
    private String estado = "PENDIENTE"; // PENDIENTE, RECIBIDO, RECHAZADO

    @Column(name = "prioridad")
    private String prioridad = "NORMAL"; // BAJA, NORMAL, ALTA, URGENTE
}
