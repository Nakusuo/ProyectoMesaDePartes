package com.pnp.mesadepartes.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Bitacora;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    
    // Buscar por código de documento
    List<Bitacora> findByCodigoDocumento(String codigoDocumento);
    
    // Buscar por ID de documento
    Bitacora findByIdDocumento(Long idDocumento);
    
    // Buscar por rango de fechas de entrada
    @Query("SELECT b FROM Bitacora b WHERE b.tieneEntrada = true AND b.fechaEntrada BETWEEN :fechaInicio AND :fechaFin ORDER BY b.fechaEntrada DESC")
    Page<Bitacora> findByFechaEntradaBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );
    
    // Buscar registros con entrada
    @Query("SELECT b FROM Bitacora b WHERE b.tieneEntrada = true ORDER BY b.fechaEntrada DESC")
    Page<Bitacora> findAllWithEntrada(Pageable pageable);
    
    // Buscar registros con salida
    @Query("SELECT b FROM Bitacora b WHERE b.tieneSalida = true ORDER BY b.fechaSalida DESC")
    Page<Bitacora> findAllWithSalida(Pageable pageable);
    
    // Buscar registros sin salida
    @Query("SELECT b FROM Bitacora b WHERE b.tieneEntrada = true AND b.tieneSalida = false ORDER BY b.fechaEntrada DESC")
    Page<Bitacora> findAllSinSalida(Pageable pageable);
    
    // Buscar todas ordenadas por fecha más reciente (entrada o salida)
    @Query("SELECT b FROM Bitacora b ORDER BY COALESCE(b.fechaSalida, b.fechaEntrada) DESC")
    Page<Bitacora> findAllByOrderByFechaDesc(Pageable pageable);
    
    // Búsqueda avanzada con múltiples filtros
    @Query("SELECT b FROM Bitacora b WHERE " +
           "(:tieneEntrada IS NULL OR b.tieneEntrada = :tieneEntrada) AND " +
           "(:tieneSalida IS NULL OR b.tieneSalida = :tieneSalida) AND " +
           "(:fechaInicio IS NULL OR b.fechaEntrada >= :fechaInicio OR b.fechaSalida >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR b.fechaEntrada <= :fechaFin OR b.fechaSalida <= :fechaFin) " +
           "ORDER BY COALESCE(b.fechaSalida, b.fechaEntrada) DESC")
    Page<Bitacora> buscarConFiltros(
        @Param("tieneEntrada") Boolean tieneEntrada,
        @Param("tieneSalida") Boolean tieneSalida,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );
}
