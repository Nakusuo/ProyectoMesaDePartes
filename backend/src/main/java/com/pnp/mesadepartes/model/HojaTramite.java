package com.pnp.mesadepartes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "hojas_tramite")
public class HojaTramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_hoja_tramite")
    private Long idHojaTramite;

    @Column(name = "numero_ht", length = 50)
    private String numeroHt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_documento")
    private Documento documento;
}
