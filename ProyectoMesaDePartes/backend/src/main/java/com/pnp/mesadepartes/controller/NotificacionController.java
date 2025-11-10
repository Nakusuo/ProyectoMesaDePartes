package com.pnp.mesadepartes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.model.Notificacion;
import com.pnp.mesadepartes.service.NotificacionService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesUsuario(@PathVariable Long idUsuario) {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesUsuario(idUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/no-leidas/{idUsuario}")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesNoLeidas(@PathVariable Long idUsuario) {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesNoLeidas(idUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/count-no-leidas/{idUsuario}")
    public ResponseEntity<Map<String, Object>> contarNotificacionesNoLeidas(@PathVariable Long idUsuario) {
        Long count = notificacionService.contarNotificacionesNoLeidas(idUsuario);
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ultimas/{idUsuario}")
    public ResponseEntity<List<Notificacion>> obtenerUltimasNotificaciones(@PathVariable Long idUsuario) {
        List<Notificacion> notificaciones = notificacionService.obtenerUltimasNotificaciones(idUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/marcar-leida/{idNotificacion}")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long idNotificacion) {
        try {
            Notificacion notificacion = notificacionService.marcarComoLeida(idNotificacion);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificacion", notificacion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al marcar notificación como leída");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/marcar-todas-leidas/{idUsuario}")
    public ResponseEntity<?> marcarTodasComoLeidas(@PathVariable Long idUsuario) {
        try {
            notificacionService.marcarTodasComoLeidas(idUsuario);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todas las notificaciones marcadas como leídas");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al marcar notificaciones como leídas");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
