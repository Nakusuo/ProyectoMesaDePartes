package com.pnp.mesadepartes.service;

import com.pnp.mesadepartes.model.Bitacora;
import com.pnp.mesadepartes.repository.BitacoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BitacoraService {
    
    private final BitacoraRepository bitacoraRepository;
    
    /**
     * Obtener todas las entradas de bitácora con paginación
     */
    public Page<Bitacora> obtenerTodos(Pageable pageable) {
        return bitacoraRepository.findAllByOrderByFechaOperacionDesc(pageable);
    }
    
    /**
     * Buscar bitácora por tipo de operación
     */
    public Page<Bitacora> buscarPorTipoOperacion(Bitacora.TipoOperacion tipoOperacion, Pageable pageable) {
        return bitacoraRepository.findByTipoOperacion(tipoOperacion, pageable);
    }
    
    /**
     * Buscar bitácora por código de documento
     */
    public List<Bitacora> buscarPorCodigoDocumento(String codigoDocumento) {
        return bitacoraRepository.findByCodigoDocumento(codigoDocumento);
    }
    
    /**
     * Buscar bitácora por ID de documento
     */
    public List<Bitacora> buscarPorIdDocumento(Long idDocumento) {
        return bitacoraRepository.findByIdDocumento(idDocumento);
    }
    
    /**
     * Buscar bitácora por rango de fechas
     */
    public Page<Bitacora> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return bitacoraRepository.findByFechaOperacionBetween(fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Buscar bitácora por usuario
     */
    public Page<Bitacora> buscarPorUsuario(Long idUsuario, Pageable pageable) {
        return bitacoraRepository.findByIdUsuarioOperacion(idUsuario, pageable);
    }
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    public Page<Bitacora> buscarConFiltros(
            Bitacora.TipoOperacion tipoOperacion,
            Long idUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        return bitacoraRepository.buscarConFiltros(tipoOperacion, idUsuario, fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Obtener bitácora por ID
     */
    public Bitacora obtenerPorId(Long id) {
        return bitacoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de bitácora no encontrado"));
    }
}
