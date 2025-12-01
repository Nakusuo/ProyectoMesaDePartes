package com.pnp.mesadepartes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Servicio de respaldo automático de la base de datos
 * Realiza backups programados de MySQL y gestiona la retención de archivos
 * 
 * @author Mesa de Partes Digital - PNP
 */
@Service
@Slf4j
public class BackupService {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.directory:../backups}")
    private String backupDirectory;

    @Value("${backup.retention.days:30}")
    private int retentionDays;

    @Value("${backup.enabled:true}")
    private boolean backupEnabled;

    /**
     * Ejecuta un backup automático cada día a las 2:00 AM
     * Cron: segundo, minuto, hora, día, mes, día de la semana
     */
    @Scheduled(cron = "${backup.schedule:0 0 2 * * ?}")
    public void performScheduledBackup() {
        if (!backupEnabled) {
            log.info("Los backups automáticos están deshabilitados");
            return;
        }

        log.info("Iniciando backup programado de la base de datos...");
        try {
            String backupFile = performBackup();
            log.info("✅ Backup completado exitosamente: {}", backupFile);
            
            // Limpiar backups antiguos
            cleanOldBackups();
        } catch (Exception e) {
            log.error("❌ Error al realizar el backup programado: {}", e.getMessage(), e);
        }
    }

    /**
     * Realiza un backup manual de la base de datos
     * 
     * @return Ruta del archivo de backup generado
     * @throws IOException Si hay un error durante el backup
     */
    public String performBackup() throws IOException {
        // Crear directorio de backups si no existe
        Path backupPath = Paths.get(backupDirectory);
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
            log.info("Directorio de backups creado: {}", backupPath.toAbsolutePath());
        }

        // Extraer nombre de la base de datos de la URL
        String dbName = extractDatabaseName(datasourceUrl);
        
        // Generar nombre del archivo con timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFileName = String.format("backup_%s_%s.sql", dbName, timestamp);
        String backupFilePath = Paths.get(backupDirectory, backupFileName).toString();

        // Construir comando mysqldump
        List<String> command = new ArrayList<>();
        
        // Detectar sistema operativo
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows
            command.add("cmd.exe");
            command.add("/c");
            command.add(String.format(
                "mysqldump -u%s -p%s --databases %s --result-file=\"%s\" --single-transaction --quick --lock-tables=false",
                dbUsername, dbPassword, dbName, backupFilePath
            ));
        } else {
            // Linux/Mac
            command.add("sh");
            command.add("-c");
            command.add(String.format(
                "mysqldump -u%s -p%s --databases %s --result-file='%s' --single-transaction --quick --lock-tables=false",
                dbUsername, dbPassword, dbName, backupFilePath
            ));
        }

        log.info("Ejecutando backup de la base de datos '{}'...", dbName);
        
        // Ejecutar comando
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Capturar salida del proceso
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug(line);
            }
        }

        // Esperar a que termine el proceso
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("El proceso de backup finalizó con código de error: " + exitCode + "\n" + output);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("El proceso de backup fue interrumpido", e);
        }

        // Verificar que el archivo se creó correctamente
        File backupFile = new File(backupFilePath);
        if (!backupFile.exists() || backupFile.length() == 0) {
            throw new IOException("El archivo de backup no se generó correctamente");
        }

        log.info("Backup generado correctamente: {} (Tamaño: {} bytes)", backupFilePath, backupFile.length());
        return backupFilePath;
    }

    /**
     * Elimina backups antiguos según la política de retención configurada
     */
    public void cleanOldBackups() {
        try {
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                return;
            }

            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            
            try (Stream<Path> files = Files.list(backupPath)) {
                files.filter(file -> file.toString().endsWith(".sql"))
                     .filter(file -> {
                         try {
                             LocalDateTime fileDate = LocalDateTime.ofInstant(
                                 Files.getLastModifiedTime(file).toInstant(),
                                 java.time.ZoneId.systemDefault()
                             );
                             return fileDate.isBefore(cutoffDate);
                         } catch (IOException e) {
                             log.error("Error al verificar fecha del archivo: {}", file, e);
                             return false;
                         }
                     })
                     .forEach(file -> {
                         try {
                             Files.delete(file);
                             log.info("Backup antiguo eliminado: {}", file.getFileName());
                         } catch (IOException e) {
                             log.error("Error al eliminar backup antiguo: {}", file, e);
                         }
                     });
            }
            
            log.info("Limpieza de backups antiguos completada (Retención: {} días)", retentionDays);
        } catch (IOException e) {
            log.error("Error al limpiar backups antiguos: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene la lista de archivos de backup disponibles
     * 
     * @return Lista de nombres de archivos de backup
     */
    public List<String> listBackups() {
        List<String> backups = new ArrayList<>();
        try {
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                return backups;
            }

            try (Stream<Path> files = Files.list(backupPath)) {
                files.filter(file -> file.toString().endsWith(".sql"))
                     .sorted((f1, f2) -> {
                         try {
                             return Files.getLastModifiedTime(f2).compareTo(Files.getLastModifiedTime(f1));
                         } catch (IOException e) {
                             return 0;
                         }
                     })
                     .forEach(file -> backups.add(file.getFileName().toString()));
            }
        } catch (IOException e) {
            log.error("Error al listar backups: {}", e.getMessage(), e);
        }
        return backups;
    }

    /**
     * Obtiene información sobre el último backup realizado
     * 
     * @return Información del último backup o null si no hay backups
     */
    public BackupInfo getLastBackupInfo() {
        try {
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                return null;
            }

            try (Stream<Path> files = Files.list(backupPath)) {
                return files.filter(file -> file.toString().endsWith(".sql"))
                           .max((f1, f2) -> {
                               try {
                                   return Files.getLastModifiedTime(f1).compareTo(Files.getLastModifiedTime(f2));
                               } catch (IOException e) {
                                   return 0;
                               }
                           })
                           .map(file -> {
                               try {
                                   BackupInfo info = new BackupInfo();
                                   info.setFileName(file.getFileName().toString());
                                   info.setFilePath(file.toString());
                                   info.setFileSize(Files.size(file));
                                   info.setCreatedDate(Files.getLastModifiedTime(file).toInstant()
                                       .atZone(java.time.ZoneId.systemDefault())
                                       .toLocalDateTime());
                                   return info;
                               } catch (IOException e) {
                                   log.error("Error al obtener información del backup: {}", file, e);
                                   return null;
                               }
                           })
                           .orElse(null);
            }
        } catch (IOException e) {
            log.error("Error al obtener información del último backup: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extrae el nombre de la base de datos de la URL JDBC
     * 
     * @param url URL JDBC
     * @return Nombre de la base de datos
     */
    private String extractDatabaseName(String url) {
        // jdbc:mysql://localhost:3306/mesa_partes_db?params
        try {
            String[] parts = url.split("/");
            String dbPart = parts[parts.length - 1];
            return dbPart.split("\\?")[0];
        } catch (Exception e) {
            log.error("Error al extraer nombre de la base de datos de la URL: {}", url, e);
            return "database";
        }
    }

    /**
     * Clase interna para información de backup
     */
    public static class BackupInfo {
        private String fileName;
        private String filePath;
        private long fileSize;
        private LocalDateTime createdDate;

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
        public LocalDateTime getCreatedDate() { return createdDate; }
        public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
        
        public String getFormattedSize() {
            double kb = fileSize / 1024.0;
            double mb = kb / 1024.0;
            if (mb >= 1) {
                return String.format("%.2f MB", mb);
            } else {
                return String.format("%.2f KB", kb);
            }
        }
        
        public String getFormattedDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return createdDate.format(formatter);
        }
    }
}
