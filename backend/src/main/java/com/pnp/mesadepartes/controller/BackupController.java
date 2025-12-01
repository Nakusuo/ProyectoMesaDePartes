package com.pnp.mesadepartes.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.service.BackupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestión de backups de la base de datos
 * Solo accesible por usuarios con rol ADMINISTRADOR
 * 
 * @author Mesa de Partes Digital - PNP
 */
@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class BackupController {

    private final BackupService backupService;

    /**
     * Ejecuta un backup manual de la base de datos
     * 
     * @return Respuesta con información del backup generado
     */
    @PostMapping("/execute")
    public ResponseEntity<?> executeBackup() {
        try {
            log.info("Solicitud de backup manual recibida");
            String backupFile = backupService.performBackup();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Backup realizado exitosamente");
            response.put("backupFile", backupFile);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al ejecutar backup manual: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al realizar el backup: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lista todos los archivos de backup disponibles
     * 
     * @return Lista de nombres de archivos de backup
     */
    @GetMapping("/list")
    public ResponseEntity<?> listBackups() {
        try {
            List<String> backups = backupService.listBackups();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("backups", backups);
            response.put("count", backups.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al listar backups: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al listar backups: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene información del último backup realizado
     * 
     * @return Información detallada del último backup
     */
    @GetMapping("/last")
    public ResponseEntity<?> getLastBackup() {
        try {
            BackupService.BackupInfo lastBackup = backupService.getLastBackupInfo();
            
            if (lastBackup == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "No hay backups disponibles");
                response.put("lastBackup", null);
                
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("lastBackup", Map.of(
                "fileName", lastBackup.getFileName(),
                "filePath", lastBackup.getFilePath(),
                "fileSize", lastBackup.getFileSize(),
                "formattedSize", lastBackup.getFormattedSize(),
                "createdDate", lastBackup.getCreatedDate(),
                "formattedDate", lastBackup.getFormattedDate()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener información del último backup: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener información del backup: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Limpia backups antiguos según la política de retención
     * 
     * @return Respuesta indicando el resultado de la operación
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupOldBackups() {
        try {
            log.info("Solicitud de limpieza de backups antiguos recibida");
            backupService.cleanOldBackups();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Limpieza de backups antiguos completada");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al limpiar backups antiguos: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al limpiar backups: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
