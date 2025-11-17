package com.pnp.mesadepartes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para envío de correos electrónicos
 * Sistema Mesa de Partes Digital PNP
 */
@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    /**
     * Verificar si el servicio de email está configurado
     */
    private boolean isEmailConfigured() {
        return mailSender != null && fromEmail != null && !fromEmail.isEmpty();
    }
    
    /**
     * Enviar email simple (texto plano)
     */
    public void enviarEmail(String to, String subject, String text) {
        if (!isEmailConfigured()) {
            log.warn("Servicio de email no configurado. Email no enviado a: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", to);
            
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
        }
    }
    
    /**
     * Enviar email HTML (con formato)
     */
    public void enviarEmailHtml(String to, String subject, String htmlContent) {
        if (!isEmailConfigured()) {
            log.warn("Servicio de email no configurado. Email HTML no enviado a: {}", to);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email HTML enviado exitosamente a: {}", to);
            
        } catch (Exception e) {
            log.error("Error al enviar email HTML a {}: {}", to, e.getMessage());
        }
    }
    
    /**
     * Notificación: Documento registrado
     */
    public void notificarDocumentoRegistrado(String emailUsuario, String codigoDocumento, String titulo) {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            return;
        }
        
        String asunto = "Documento Registrado - " + codigoDocumento;
        String mensaje = String.format("""
            Estimado usuario,
            
            Su documento ha sido registrado exitosamente en el Sistema Mesa de Partes Digital PNP.
            
            Código de trámite: %s
            Título: %s
            
            Puede consultar el estado de su trámite en cualquier momento ingresando al sistema
            con el código proporcionado.
            
            Este es un correo automático, por favor no responder.
            
            Saludos cordiales,
            Sistema Mesa de Partes Digital
            Policía Nacional del Perú
            """, codigoDocumento, titulo);
        
        enviarEmail(emailUsuario, asunto, mensaje);
    }
    
    /**
     * Notificación: Documento derivado
     */
    public void notificarDocumentoDerivado(String emailUsuario, String nombreUsuario, 
                                           String codigoDocumento, String tituloDocumento, 
                                           String areaNombre) {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            return;
        }
        
        String asunto = "Nuevo Documento Asignado - " + codigoDocumento;
        String mensaje = String.format("""
            Estimado(a) %s,
            
            Se le ha derivado un nuevo documento para su atención.
            
            Código: %s
            Título: %s
            Área destino: %s
            
            Por favor, ingrese al sistema para revisar el documento y confirmar su recepción.
            
            Este es un correo automático, por favor no responder.
            
            Saludos cordiales,
            Sistema Mesa de Partes Digital
            Policía Nacional del Perú
            """, nombreUsuario, codigoDocumento, tituloDocumento, areaNombre);
        
        enviarEmail(emailUsuario, asunto, mensaje);
    }
    
    /**
     * Notificación: Cambio de estado de documento
     */
    public void notificarCambioEstado(String emailUsuario, String nombreUsuario,
                                      String codigoDocumento, String tituloDocumento,
                                      String estadoAnterior, String estadoNuevo) {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            return;
        }
        
        String asunto = "Cambio de Estado - " + codigoDocumento;
        String mensaje = String.format("""
            Estimado(a) %s,
            
            El estado de su documento ha cambiado.
            
            Código: %s
            Título: %s
            Estado anterior: %s
            Estado actual: %s
            
            Puede consultar más detalles ingresando al sistema.
            
            Este es un correo automático, por favor no responder.
            
            Saludos cordiales,
            Sistema Mesa de Partes Digital
            Policía Nacional del Perú
            """, nombreUsuario, codigoDocumento, tituloDocumento, estadoAnterior, estadoNuevo);
        
        enviarEmail(emailUsuario, asunto, mensaje);
    }
    
    /**
     * Notificación: Documento recibido/confirmado
     */
    public void notificarDocumentoRecibido(String emailUsuario, String nombreUsuario,
                                           String codigoDocumento, String areaNombre) {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            return;
        }
        
        String asunto = "Documento Recibido - " + codigoDocumento;
        String mensaje = String.format("""
            Estimado(a) %s,
            
            Su documento ha sido recibido y confirmado.
            
            Código: %s
            Área: %s
            
            El documento está siendo procesado.
            
            Este es un correo automático, por favor no responder.
            
            Saludos cordiales,
            Sistema Mesa de Partes Digital
            Policía Nacional del Perú
            """, nombreUsuario, codigoDocumento, areaNombre);
        
        enviarEmail(emailUsuario, asunto, mensaje);
    }
    
    /**
     * Email de prueba
     */
    public boolean enviarEmailPrueba(String destinatario) {
        if (!isEmailConfigured()) {
            log.error("No se puede enviar email de prueba: servicio no configurado");
            return false;
        }
        
        try {
            String asunto = "Prueba - Sistema Mesa de Partes PNP";
            String mensaje = """
                Este es un correo de prueba del Sistema Mesa de Partes Digital PNP.
                
                Si recibe este mensaje, el sistema de correo está configurado correctamente.
                
                Fecha y hora: """ + java.time.LocalDateTime.now();
            
            enviarEmail(destinatario, asunto, mensaje);
            return true;
        } catch (Exception e) {
            log.error("Error en email de prueba: {}", e.getMessage());
            return false;
        }
    }
}
