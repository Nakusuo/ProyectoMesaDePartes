package com.pnp.mesadepartes.repository;

import com.pnp.mesadepartes.model.Bitacora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    
    // Buscar por tipo de operación
    Page<Bitacora> findByTipoOperacion(Bitacora.TipoOperacion tipoOperacion, Pageable pageable);
    
    // Buscar por código de documento
    List<Bitacora> findByCodigoDocumento(String codigoDocumento);
    
    // Buscar por ID de documento
    List<Bitacora> findByIdDocumento(Long idDocumento);
    
    // Buscar por rango de fechas
    @Query("SELECT b FROM Bitacora b WHERE b.fechaOperacion BETWEEN :fechaInicio AND :fechaFin ORDER BY b.fechaOperacion DESC")
    Page<Bitacora> findByFechaOperacionBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );
    
    // Buscar por usuario
    Page<Bitacora> findByIdUsuarioOperacion(Long idUsuario, Pageable pageable);
    
    // Buscar todas ordenadas por fecha descendente
    Page<Bitacora> findAllByOrderByFechaOperacionDesc(Pageable pageable);
    
    // Búsqueda avanzada con múltiples filtros
    @Query("SELECT b FROM Bitacora b WHERE " +
           "(:tipoOperacion IS NULL OR b.tipoOperacion = :tipoOperacion) AND " +
           "(:idUsuario IS NULL OR b.idUsuarioOperacion = :idUsuario) AND " +
           "(:fechaInicio IS NULL OR b.fechaOperacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR b.fechaOperacion <= :fechaFin) " +
           "ORDER BY b.fechaOperacion DESC")
    Page<Bitacora> buscarConFiltros(
        @Param("tipoOperacion") Bitacora.TipoOperacion tipoOperacion,
        @Param("idUsuario") Long idUsuario,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );
}
