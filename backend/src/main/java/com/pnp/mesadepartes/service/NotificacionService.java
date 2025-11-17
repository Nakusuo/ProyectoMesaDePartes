package com.pnp.mesadepartes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.Notificacion;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.DocumentoRepository;
import com.pnp.mesadepartes.repository.NotificacionRepository;
import com.pnp.mesadepartes.repository.UsuarioRepository;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired private NotificacionRepository notificacionRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private EmailService emailService;

    @Transactional
    public Notificacion crearNotificacion(Long idUsuario, Long idDocumento, String titulo, String mensaje, String tipo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Documento documento = null;
        if (idDocumento != null) {
            documento = documentoRepository.findById(idDocumento).orElse(null);
        }

        // Crear notificación in-app
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setDocumento(documento);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);

        Notificacion saved = notificacionRepository.save(notificacion);
        
        // NUEVO: Enviar email si el usuario tiene email configurado
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            log.debug("Enviando email de notificación a: {}", usuario.getEmail());
            emailService.enviarEmail(usuario.getEmail(), titulo, mensaje);
        }
        
        return saved;
    }

    /**
     * Crear notificación para documento registrado (con email)
     */
    @Transactional
    public Notificacion notificarDocumentoRegistrado(Long idUsuario, Long idDocumento, 
                                                     String codigoDocumento, String tituloDoc) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String titulo = "Documento Registrado - " + codigoDocumento;
        String mensaje = "Su documento '" + tituloDoc + "' ha sido registrado con código: " + codigoDocumento;
        
        // Crear notificación in-app
        Notificacion notificacion = crearNotificacion(idUsuario, idDocumento, titulo, mensaje, "DOCUMENTO_REGISTRADO");
        
        // Enviar email específico
        emailService.notificarDocumentoRegistrado(
            usuario.getEmail(), 
            codigoDocumento, 
            tituloDoc
        );
        
        return notificacion;
    }
    
    /**
     * Crear notificación para documento derivado (con email)
     */
    @Transactional
    public Notificacion notificarDocumentoDerivado(Long idUsuario, Long idDocumento,
                                                   String codigoDocumento, String tituloDoc,
                                                   String areaNombre) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String titulo = "Nuevo Documento Asignado - " + codigoDocumento;
        String mensaje = "Se le ha derivado el documento '" + tituloDoc + "' al área: " + areaNombre;
        
        // Crear notificación in-app
        Notificacion notificacion = crearNotificacion(idUsuario, idDocumento, titulo, mensaje, "DOCUMENTO_DERIVADO");
        
        // Enviar email específico
        emailService.notificarDocumentoDerivado(
            usuario.getEmail(),
            usuario.getNombre() + " " + usuario.getApellido(),
            codigoDocumento,
            tituloDoc,
            areaNombre
        );
        
        return notificacion;
    }

    public List<Notificacion> obtenerNotificacionesUsuario(Long idUsuario) {
        return notificacionRepository.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    public List<Notificacion> obtenerNotificacionesNoLeidas(Long idUsuario) {
        return notificacionRepository.findByUsuarioIdUsuarioAndLeidaOrderByFechaCreacionDesc(idUsuario, false);
    }

    public Long contarNotificacionesNoLeidas(Long idUsuario) {
        return notificacionRepository.countByUsuarioIdUsuarioAndLeida(idUsuario, false);
    }

    @Transactional
    public Notificacion marcarComoLeida(Long idNotificacion) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        if (!notificacion.getLeida()) {
            notificacion.setLeida(true);
            notificacion.setFechaLectura(LocalDateTime.now());
            return notificacionRepository.save(notificacion);
        }
        
        return notificacion;
    }

    @Transactional
    public void marcarTodasComoLeidas(Long idUsuario) {
        List<Notificacion> notificaciones = obtenerNotificacionesNoLeidas(idUsuario);
        LocalDateTime ahora = LocalDateTime.now();
        
        for (Notificacion notificacion : notificaciones) {
            notificacion.setLeida(true);
            notificacion.setFechaLectura(ahora);
        }
        
        notificacionRepository.saveAll(notificaciones);
    }

    public List<Notificacion> obtenerUltimasNotificaciones(Long idUsuario) {
        return notificacionRepository.findTop10ByUsuarioIdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }
}
