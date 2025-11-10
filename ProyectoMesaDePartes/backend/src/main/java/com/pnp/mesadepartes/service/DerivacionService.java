package com.pnp.mesadepartes.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pnp.mesadepartes.dto.DerivarDocumentoDTO;
import com.pnp.mesadepartes.dto.TrazabilidadDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;

@Service
public class DerivacionService {

    @Autowired private DerivacionRepository derivacionRepository;
    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TramiteRepository tramiteRepository;
    @Autowired private NotificacionService notificacionService;

    @Transactional
    public Derivacion derivarDocumento(DerivarDocumentoDTO dto, Long idUsuarioDeriva) {
        // Validar documento
        Documento documento = documentoRepository.findById(dto.getIdDocumento())
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        // Validar usuario que deriva
        Usuario usuarioDeriva = usuarioRepository.findById(idUsuarioDeriva)
                .orElseThrow(() -> new RuntimeException("Usuario que deriva no encontrado"));

        // Validar área destino
        Area areaDestino = areaRepository.findById(dto.getIdAreaDestino())
                .orElseThrow(() -> new RuntimeException("Área destino no encontrada"));

        // Validar usuario receptor
        Usuario usuarioRecibe = null;
        if (dto.getIdUsuarioRecibe() != null) {
            usuarioRecibe = usuarioRepository.findById(dto.getIdUsuarioRecibe())
                    .orElseThrow(() -> new RuntimeException("Usuario receptor no encontrado"));
        }

        // Obtener área origen (del último trámite o derivación)
        Area areaOrigen = obtenerAreaActual(documento);

        // Crear derivación
        Derivacion derivacion = new Derivacion();
        derivacion.setDocumento(documento);
        derivacion.setAreaOrigen(areaOrigen);
        derivacion.setAreaDestino(areaDestino);
        derivacion.setUsuarioDeriva(usuarioDeriva);
        derivacion.setUsuarioRecibe(usuarioRecibe);
        derivacion.setObservaciones(dto.getObservaciones());
        derivacion.setPrioridad(dto.getPrioridad());
        derivacion.setEstado("PENDIENTE");

        Derivacion derivacionGuardada = derivacionRepository.save(derivacion);

        // Actualizar trámite
        List<Tramite> tramites = tramiteRepository.findByDocumento(documento);
        if (!tramites.isEmpty()) {
            Tramite tramite = tramites.get(0);
            if (usuarioRecibe != null) {
                tramite.setUsuarioAsignado(usuarioRecibe);
                tramiteRepository.save(tramite);
            }
        }

        // Enviar notificaciones
        if (usuarioRecibe != null) {
            notificacionService.crearNotificacion(
                usuarioRecibe.getIdUsuario(),
                documento.getIdDocumento(),
                "Documento derivado a su área",
                "Se le ha derivado el documento " + documento.getCodigo() + " desde " + 
                (areaOrigen != null ? areaOrigen.getNombre() : "Mesa de Partes") + 
                " con prioridad " + dto.getPrioridad(),
                "DERIVACION"
            );
        }

        // Notificar al remitente original si existe
        if (documento.getUsuarioRegistro() != null) {
            notificacionService.crearNotificacion(
                documento.getUsuarioRegistro().getIdUsuario(),
                documento.getIdDocumento(),
                "Su documento ha sido derivado",
                "El documento " + documento.getCodigo() + " fue derivado a " + areaDestino.getNombre(),
                "DERIVACION"
            );
        }

        return derivacionGuardada;
    }

    @Transactional
    public Derivacion recibirDerivacion(Long idDerivacion, Long idUsuarioRecibe) {
        Derivacion derivacion = derivacionRepository.findById(idDerivacion)
                .orElseThrow(() -> new RuntimeException("Derivación no encontrada"));

        derivacion.setEstado("RECIBIDO");
        derivacion.setFechaRecepcion(LocalDateTime.now());
        
        if (idUsuarioRecibe != null) {
            Usuario usuario = usuarioRepository.findById(idUsuarioRecibe)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            derivacion.setUsuarioRecibe(usuario);
        }

        return derivacionRepository.save(derivacion);
    }

    public List<Derivacion> obtenerDerivacionesPorDocumento(Long idDocumento) {
        return derivacionRepository.findByDocumentoIdDocumentoOrderByFechaDerivacionDesc(idDocumento);
    }

    public List<Derivacion> obtenerDerivacionesPorArea(Long idArea) {
        return derivacionRepository.findByAreaDestinoIdAreaOrderByFechaDerivacionDesc(idArea);
    }

    public TrazabilidadDTO obtenerTrazabilidad(Long idDocumento) {
        Documento documento = documentoRepository.findById(idDocumento)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        TrazabilidadDTO trazabilidad = new TrazabilidadDTO();
        trazabilidad.setIdDocumento(documento.getIdDocumento());
        trazabilidad.setCodigo(documento.getCodigo());
        trazabilidad.setTitulo(documento.getTitulo());
        trazabilidad.setEstadoActual(documento.getEstado().toString());
        trazabilidad.setFechaRegistro(documento.getFechaIngreso());
        trazabilidad.setRemitente(documento.getRemitente());
        trazabilidad.setTipoDocumento(documento.getTipoDocumento() != null ? 
                documento.getTipoDocumento().getNombre() : "N/A");

        // Obtener movimientos
        List<TrazabilidadDTO.MovimientoDTO> movimientos = new ArrayList<>();

        // Agregar registro inicial
        TrazabilidadDTO.MovimientoDTO registroInicial = new TrazabilidadDTO.MovimientoDTO();
        registroInicial.setTipo("REGISTRO");
        registroInicial.setFecha(documento.getFechaIngreso());
        registroInicial.setDescripcion("Documento registrado en el sistema");
        registroInicial.setUsuario(documento.getUsuarioRegistro() != null ? 
                documento.getUsuarioRegistro().getNombre() + " " + documento.getUsuarioRegistro().getApellido() : "Sistema");
        registroInicial.setEstado(EstadoDocumento.Asignado.toString());
        movimientos.add(registroInicial);

        // Agregar derivaciones
        List<Derivacion> derivaciones = obtenerDerivacionesPorDocumento(idDocumento);
        LocalDateTime fechaAnterior = documento.getFechaIngreso();
        
        for (Derivacion derivacion : derivaciones) {
            TrazabilidadDTO.MovimientoDTO movimiento = new TrazabilidadDTO.MovimientoDTO();
            movimiento.setId(derivacion.getIdDerivacion());
            movimiento.setTipo("DERIVACION");
            movimiento.setFecha(derivacion.getFechaDerivacion());
            movimiento.setDescripcion("Documento derivado" + 
                    (derivacion.getObservaciones() != null ? ": " + derivacion.getObservaciones() : ""));
            movimiento.setUsuario(derivacion.getUsuarioDeriva().getNombre() + " " + 
                    derivacion.getUsuarioDeriva().getApellido());
            movimiento.setAreaOrigen(derivacion.getAreaOrigen() != null ? 
                    derivacion.getAreaOrigen().getNombre() : "Mesa de Partes");
            movimiento.setAreaDestino(derivacion.getAreaDestino().getNombre());
            movimiento.setEstado(derivacion.getEstado());
            
            // Calcular tiempo en área
            long horas = Duration.between(fechaAnterior, derivacion.getFechaDerivacion()).toHours();
            movimiento.setTiempoEnArea(horas);
            
            movimientos.add(movimiento);
            fechaAnterior = derivacion.getFechaDerivacion();
        }

        trazabilidad.setMovimientos(movimientos);

        // Estadísticas
        TrazabilidadDTO.EstadisticasDTO estadisticas = new TrazabilidadDTO.EstadisticasDTO();
        estadisticas.setTotalDerivaciones(derivaciones.size());
        estadisticas.setTiempoTotalHoras(Duration.between(documento.getFechaIngreso(), 
                LocalDateTime.now()).toHours());
        
        // Área y usuario actual
        if (!derivaciones.isEmpty()) {
            Derivacion ultimaDerivacion = derivaciones.get(0);
            estadisticas.setAreaActual(ultimaDerivacion.getAreaDestino().getNombre());
            estadisticas.setUsuarioActual(ultimaDerivacion.getUsuarioRecibe() != null ? 
                    ultimaDerivacion.getUsuarioRecibe().getNombre() + " " + 
                    ultimaDerivacion.getUsuarioRecibe().getApellido() : "Sin asignar");
            
            // Contar áreas únicas
            long areasUnicas = derivaciones.stream()
                    .map(d -> d.getAreaDestino().getIdArea())
                    .distinct()
                    .count();
            estadisticas.setTotalAreas((int) areasUnicas);
        } else {
            estadisticas.setAreaActual("Mesa de Partes");
            estadisticas.setTotalAreas(0);
        }

        trazabilidad.setEstadisticas(estadisticas);

        return trazabilidad;
    }

    private Area obtenerAreaActual(Documento documento) {
        List<Derivacion> derivaciones = obtenerDerivacionesPorDocumento(documento.getIdDocumento());
        if (!derivaciones.isEmpty()) {
            return derivaciones.get(0).getAreaDestino();
        }
        return null; // Mesa de Partes
    }
}
