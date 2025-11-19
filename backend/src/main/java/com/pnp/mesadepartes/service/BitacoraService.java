package com.pnp.mesadepartes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pnp.mesadepartes.model.Bitacora;
import com.pnp.mesadepartes.repository.BitacoraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BitacoraService {
    
    private final BitacoraRepository bitacoraRepository;
    
    /**
     * Obtener todas las entradas de bitácora con paginación
     */
    public Page<Bitacora> obtenerTodos(Pageable pageable) {
        return bitacoraRepository.findAllByOrderByFechaDesc(pageable);
    }
    
    /**
     * Buscar bitácora con entrada
     */
    public Page<Bitacora> buscarConEntrada(Pageable pageable) {
        return bitacoraRepository.findAllWithEntrada(pageable);
    }
    
    /**
     * Buscar bitácora con salida
     */
    public Page<Bitacora> buscarConSalida(Pageable pageable) {
        return bitacoraRepository.findAllWithSalida(pageable);
    }
    
    /**
     * Buscar bitácora sin salida (pendientes)
     */
    public Page<Bitacora> buscarSinSalida(Pageable pageable) {
        return bitacoraRepository.findAllSinSalida(pageable);
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
    public Bitacora buscarPorIdDocumento(Long idDocumento) {
        return bitacoraRepository.findByIdDocumento(idDocumento);
    }
    
    /**
     * Buscar bitácora por rango de fechas
     */
    public Page<Bitacora> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return bitacoraRepository.findByFechaEntradaBetween(fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    public Page<Bitacora> buscarConFiltros(
            Boolean tieneEntrada,
            Boolean tieneSalida,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        return bitacoraRepository.buscarConFiltros(tieneEntrada, tieneSalida, fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Obtener bitácora por ID
     */
    public Bitacora obtenerPorId(Long id) {
        return bitacoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de bitácora no encontrado"));
    }
    
    /**
     * Registrar o actualizar salida de documento en bitácora
     * Este método busca el registro existente y actualiza la información de salida
     */
    public Bitacora registrarSalida(
            Long idDocumento,
            String codigoDocumento,
            String tituloDocumento,
            String tipoDocumento,
            String destinatario,
            String numeroDocumentoSalida,
            String observaciones,
            String archivoUrl,
            Long idUsuario,
            String nombreUsuario) {
        
        // Buscar registro existente
        Bitacora bitacora = bitacoraRepository.findByIdDocumento(idDocumento);
        
        if (bitacora != null) {
            // Actualizar con información de salida
            bitacora.setTieneSalida(true);
            bitacora.setDestinatario(destinatario);
            bitacora.setFechaSalida(LocalDateTime.now());
            bitacora.setUsuarioSalida(nombreUsuario);
            bitacora.setNumeroDocumentoSalida(numeroDocumentoSalida);
            bitacora.setObservacionesSalida(observaciones);
            bitacora.setArchivoSalidaUrl(archivoUrl);
        } else {
            // Crear nuevo registro solo con salida
            bitacora = new Bitacora();
            bitacora.setIdDocumento(idDocumento);
            bitacora.setCodigoDocumento(codigoDocumento);
            bitacora.setTituloDocumento(tituloDocumento);
            bitacora.setTipoDocumento(tipoDocumento);
            bitacora.setTieneEntrada(false);
            bitacora.setTieneSalida(true);
            bitacora.setDestinatario(destinatario);
            bitacora.setFechaSalida(LocalDateTime.now());
            bitacora.setUsuarioSalida(nombreUsuario);
            bitacora.setNumeroDocumentoSalida(numeroDocumentoSalida);
            bitacora.setObservacionesSalida(observaciones);
            bitacora.setArchivoSalidaUrl(archivoUrl);
        }
        
        return bitacoraRepository.save(bitacora);
    }
}
