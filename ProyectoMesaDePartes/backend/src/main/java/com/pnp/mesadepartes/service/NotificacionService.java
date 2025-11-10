package com.pnp.mesadepartes.service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired private NotificacionRepository notificacionRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DocumentoRepository documentoRepository;

    @Transactional
    public Notificacion crearNotificacion(Long idUsuario, Long idDocumento, String titulo, String mensaje, String tipo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Documento documento = null;
        if (idDocumento != null) {
            documento = documentoRepository.findById(idDocumento).orElse(null);
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setDocumento(documento);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);

        return notificacionRepository.save(notificacion);
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
