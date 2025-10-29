package com.pnp.mesadepartes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_area")
    private Long idArea;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(unique = true, length = 20)
    private String sigla;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArea tipo = TipoArea.DEPARTAMENTO_PNP;
    
    public enum TipoArea {
        DEPARTAMENTO_PNP,  // Áreas oficiales de la PNP (para documentos)
        AREA_TRABAJO       // Áreas de trabajo del sistema (para usuarios)
    }
}
