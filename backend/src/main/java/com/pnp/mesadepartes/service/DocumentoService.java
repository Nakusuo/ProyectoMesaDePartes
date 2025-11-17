package com.pnp.mesadepartes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pnp.mesadepartes.dto.DocumentoRegistroDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;

@Service
public class DocumentoService {

    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TramiteRepository tramiteRepository;
    @Autowired private HojaTramiteRepository hojaTramiteRepository;
    @Autowired private NotificacionService notificacionService;

    @Transactional
    public Documento registrarDocumento(DocumentoRegistroDTO dto, Long idUsuarioRegistrador) {
        // Validar usuario registrador
        Usuario usuarioRegistrador = usuarioRepository.findById(idUsuarioRegistrador)
                .orElseThrow(() -> new RuntimeException("Error: Usuario registrador no encontrado."));

        // Validar usuario asignado
        Usuario usuarioAsignado = usuarioRepository.findById(dto.getIdUsuarioAsignado())
                .orElseThrow(() -> new RuntimeException("Error: Usuario asignado no encontrado."));

        // Validar tipo de documento
        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));

        // Generar código único secuencial
        String codigo = generarCodigoDocumento();

        // Crear documento
        Documento doc = new Documento();
        doc.setCodigo(codigo);
        doc.setTitulo(dto.getTitulo());
        doc.setDescripcion(dto.getDescripcion());
        doc.setRemitente(dto.getRemitente());
        doc.setNumeroDocumento(dto.getNumeroDocumento());
        doc.setFechaIngreso(LocalDateTime.now());
        doc.setEstado(EstadoDocumento.Asignado);
        doc.setTipoDocumento(tipoDoc);
        doc.setUsuarioRegistro(usuarioRegistrador);
        doc.setArchivoUrl(dto.getArchivoUrl());

        Documento docGuardado = documentoRepository.save(doc);

        // Crear hoja de trámite si se proporciona número
        if (dto.getNumeroHt() != null && !dto.getNumeroHt().isEmpty()) {
            HojaTramite ht = new HojaTramite();
            ht.setNumeroHt(dto.getNumeroHt());
            ht.setDocumento(docGuardado);
            hojaTramiteRepository.save(ht);
        }

        // Crear trámite inicial
        Tramite tramite = new Tramite();
        tramite.setDocumento(docGuardado);
        tramite.setUsuarioCreador(usuarioRegistrador);
        tramite.setUsuarioAsignado(usuarioAsignado);
        tramiteRepository.save(tramite);

        // Enviar notificación al usuario asignado
        notificacionService.crearNotificacion(
            usuarioAsignado.getIdUsuario(),
            docGuardado.getIdDocumento(),
            "Nuevo documento asignado",
            "Se le ha asignado el documento " + codigo + ": " + doc.getTitulo(),
            "ASIGNACION"
        );

        return docGuardado;
    }

    public synchronized String generarCodigoDocumento() {
        long totalDocumentos = documentoRepository.count();
        return String.format("DOC-%06d", totalDocumentos + 1);
    }

    public List<Documento> listarTodos() {
        return documentoRepository.findAll();
    }

    public Documento obtenerPorId(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
    }

    @Transactional
    public Documento actualizarEstado(Long idDocumento, EstadoDocumento nuevoEstado, Long idUsuario) {
        Documento doc = obtenerPorId(idDocumento);
        EstadoDocumento estadoAnterior = doc.getEstado();
        doc.setEstado(nuevoEstado);
        Documento actualizado = documentoRepository.save(doc);

        // Notificar cambio de estado
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        String nombreUsuario = usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "Sistema";
        
        notificacionService.crearNotificacion(
            doc.getUsuarioRegistro().getIdUsuario(),
            doc.getIdDocumento(),
            "Cambio de estado del documento",
            "El documento " + doc.getCodigo() + " cambió de estado de " + estadoAnterior + " a " + nuevoEstado + " por " + nombreUsuario,
            "CAMBIO_ESTADO"
        );

        return actualizado;
    }

    public List<Documento> obtenerDocumentosPorUsuario(Long idUsuario) {
        List<Tramite> tramites = tramiteRepository.findByUsuarioAsignado_IdUsuario(idUsuario);
        return tramites.stream()
                .map(Tramite::getDocumento)
                .distinct()
                .toList();
    }
}
