# 🚨 ACCIONES INMEDIATAS PARA PRODUCCIÓN
## Tareas Críticas - Implementación Paso a Paso

**Fecha:** 17 de noviembre de 2025  
**Prioridad:** 🔴 URGENTE  
**Tiempo estimado total:** 3-5 días laborales

---

## 📋 ORDEN DE IMPLEMENTACIÓN

### PASO 1: Externalizar Credenciales (2-3 horas) ⚡

#### 1.1 Crear archivo `.env`
```bash
# .env (NO SUBIR A GIT)
# Base de Datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mesa_partes_db
DB_USERNAME=root
DB_PASSWORD=TU_PASSWORD_SEGURO_AQUI

# JWT Secret (generar uno nuevo de 256 bits)
JWT_SECRET=TU_SECRET_SUPER_LARGO_Y_ALEATORIO_256_BITS

# Email
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=tu-email@gmail.com
SMTP_PASSWORD=tu-password-app-gmail

# Configuración
SERVER_PORT=8080
ALLOWED_ORIGINS=https://tu-dominio.com
```

#### 1.2 Actualizar `.gitignore`
```bash
# Agregar a .gitignore
.env
.env.local
.env.production
*.env
```

#### 1.3 Actualizar `application.properties`
```properties
# application.properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:mesa_partes_db}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

# JWT
mesadepartes.app.jwtSecret=${JWT_SECRET}
mesadepartes.app.jwtExpirationMs=28800000

# CORS
mesadepartes.app.allowedOrigins=${ALLOWED_ORIGINS:http://localhost:5500}

# Email (para Paso 4)
spring.mail.host=${SMTP_HOST:smtp.gmail.com}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### 1.4 Generar nuevo JWT Secret
```bash
# Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object {Get-Random -Maximum 256}))

# Linux/Mac
openssl rand -base64 32

# O usar un generador online:
# https://generate-secret.vercel.app/32
```

#### 1.5 Crear `application-prod.properties`
```properties
# application-prod.properties (para producción)
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&requireSSL=true
spring.jpa.show-sql=false
logging.level.root=WARN
logging.level.com.pnp.mesadepartes=INFO
```

✅ **Verificación:**
- [ ] Archivo `.env` creado
- [ ] `.env` agregado a `.gitignore`
- [ ] Nuevo JWT secret generado
- [ ] Application.properties actualizado
- [ ] Aplicación arranca correctamente

---

### PASO 2: Configurar Backups Automáticos (3-4 horas) 💾

#### 2.1 Verificar scripts existentes
Los scripts ya existen en `scripts/`:
- ✅ `backup_windows.bat`
- ✅ `backup_linux.sh`
- ✅ `restaurar_backup_windows.bat`

#### 2.2 Configurar para Windows

**2.2.1 Editar `scripts/backup_windows.bat`:**
```batch
@echo off
echo ==========================================
echo   BACKUP AUTOMATICO - Mesa de Partes PNP
echo ==========================================
echo.

REM Configuración
SET BACKUP_DIR=C:\backups\mesa_partes
SET DB_USER=root
SET DB_PASS=TU_PASSWORD_AQUI
SET DB_NAME=mesa_partes_db
SET MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
SET UPLOADS_DIR=C:\Users\User\ProyectoMesaDePartes\backend\uploads

REM Crear fecha y hora para nombre de archivo
SET FECHA=%date:~-4%%date:~3,2%%date:~0,2%
SET HORA=%time:~0,2%%time:~3,2%%time:~6,2%
SET HORA=%HORA: =0%
SET TIMESTAMP=%FECHA%_%HORA%

REM Crear directorio de backup si no existe
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
if not exist "%BACKUP_DIR%\db" mkdir "%BACKUP_DIR%\db"
if not exist "%BACKUP_DIR%\uploads" mkdir "%BACKUP_DIR%\uploads"

echo [%date% %time%] Iniciando backup...

REM Backup de base de datos
echo Respaldando base de datos...
%MYSQL_BIN% -u %DB_USER% -p%DB_PASS% %DB_NAME% > "%BACKUP_DIR%\db\backup_%TIMESTAMP%.sql"

if %ERRORLEVEL% EQU 0 (
    echo [OK] Base de datos respaldada
) else (
    echo [ERROR] Fallo al respaldar base de datos
    exit /b 1
)

REM Backup de archivos uploads
echo Respaldando archivos...
xcopy "%UPLOADS_DIR%" "%BACKUP_DIR%\uploads\backup_%TIMESTAMP%\" /E /I /H /Y /Q

if %ERRORLEVEL% EQU 0 (
    echo [OK] Archivos respaldados
) else (
    echo [ERROR] Fallo al respaldar archivos
    exit /b 1
)

REM Comprimir backup (requiere 7-Zip o WinRAR)
if exist "C:\Program Files\7-Zip\7z.exe" (
    echo Comprimiendo backup...
    "C:\Program Files\7-Zip\7z.exe" a -tzip "%BACKUP_DIR%\backup_%TIMESTAMP%.zip" "%BACKUP_DIR%\db\backup_%TIMESTAMP%.sql" "%BACKUP_DIR%\uploads\backup_%TIMESTAMP%"
    echo [OK] Backup comprimido
)

REM Eliminar backups antiguos (más de 30 días)
echo Limpiando backups antiguos...
forfiles /p "%BACKUP_DIR%\db" /s /m *.sql /d -30 /c "cmd /c del @path" 2>nul

echo.
echo ==========================================
echo   BACKUP COMPLETADO: %TIMESTAMP%
echo ==========================================
echo Ubicación: %BACKUP_DIR%
echo.

REM Registrar en log
echo [%date% %time%] Backup completado >> "%BACKUP_DIR%\backup.log"
```

**2.2.2 Programar tarea en Windows:**
```batch
1. Presionar Win + R
2. Escribir: taskschd.msc
3. Click derecho > "Crear tarea básica"
4. Nombre: "Backup Mesa de Partes"
5. Desencadenador: Diario
6. Hora de inicio: 01:00 AM
7. Repetir cada: 5 horas
8. Acción: Iniciar programa
9. Programa: C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat
10. Finalizar
```

**Configuración avanzada:**
```
- Pestaña "General":
  ✅ Ejecutar tanto si el usuario inició sesión o no
  ✅ Ejecutar con los privilegios más altos

- Pestaña "Desencadenadores":
  ✅ Repetir cada: 5 horas
  ✅ Durante: Indefinidamente

- Pestaña "Condiciones":
  ❌ Iniciar la tarea solo si el equipo está conectado a CA
  ❌ Detener si el equipo deja de estar conectado a CA
```

#### 2.3 Configurar para Linux

**2.3.1 Dar permisos de ejecución:**
```bash
chmod +x scripts/backup_linux.sh
```

**2.3.2 Editar crontab:**
```bash
crontab -e
```

**2.3.3 Agregar línea:**
```bash
# Backup cada 5 horas
0 */5 * * * /ruta/completa/scripts/backup_linux.sh >> /var/log/backup_mesa_partes.log 2>&1
```

#### 2.4 Verificar backup manual
```bash
# Windows
cd scripts
backup_windows.bat

# Linux
./scripts/backup_linux.sh
```

✅ **Verificación:**
- [ ] Script de backup actualizado con credenciales
- [ ] Tarea programada configurada
- [ ] Backup manual ejecutado exitosamente
- [ ] Archivo SQL generado
- [ ] Carpeta uploads copiada
- [ ] Log de backup creado

---

### PASO 3: Implementar Logging Estructurado (2-3 horas) 📝

#### 3.1 Crear `logback-spring.xml`
```xml
<!-- src/main/resources/logback-spring.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Consola para desarrollo -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Archivo para todos los logs -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/mesa-partes.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/mesa-partes.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- Archivo separado para errores -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
    </appender>
    
    <!-- Configuración por paquete -->
    <logger name="com.pnp.mesadepartes" level="INFO"/>
    <logger name="org.springframework" level="WARN"/>
    <logger name="org.hibernate" level="WARN"/>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
</configuration>
```

#### 3.2 Agregar Lombok para @Slf4j (opcional)
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

#### 3.3 Actualizar controllers con logging
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// O con Lombok: @Slf4j

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentoController.class);
    // O con Lombok no necesitas esta línea
    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarDocumento(@RequestBody DocumentoRegistroDTO dto) {
        log.info("=== Registrando nuevo documento ===");
        log.info("Título: {}", dto.getTitulo());
        log.info("Remitente: {}", dto.getRemitente());
        
        try {
            // ... lógica
            log.info("Documento registrado exitosamente con código: {}", codigo);
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            log.error("Error al registrar documento: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Error al registrar documento: " + e.getMessage()));
        }
    }
}
```

#### 3.4 Crear carpeta logs
```bash
# Windows
mkdir backend\logs

# Linux/Mac
mkdir backend/logs
```

#### 3.5 Agregar logs/ al .gitignore
```bash
# .gitignore
backend/logs/
*.log
```

✅ **Verificación:**
- [ ] logback-spring.xml creado
- [ ] Carpeta logs/ creada
- [ ] Controllers actualizados con logging
- [ ] Aplicación arranca sin errores
- [ ] Archivos de log se generan
- [ ] Logs rotando correctamente

---

### PASO 4: Implementar Envío de Emails (4-6 horas) 📧

#### 4.1 Agregar dependencia
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

#### 4.2 Configurar Gmail App Password
```
1. Ir a: https://myaccount.google.com/security
2. Activar verificación en 2 pasos (si no está)
3. Ir a "Contraseñas de aplicaciones"
4. Generar contraseña para "Otra (nombre personalizado)"
5. Copiar contraseña generada (16 caracteres)
6. Agregar a .env como SMTP_PASSWORD
```

#### 4.3 Crear EmailService
```java
// EmailService.java
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

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;
    
    /**
     * Enviar email simple
     */
    public void enviarEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Email enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar email: ", e);
        }
    }
    
    /**
     * Enviar email HTML
     */
    public void enviarEmailHtml(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email HTML enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar email HTML: ", e);
        }
    }
    
    /**
     * Email para nuevo documento registrado
     */
    public void notificarDocumentoRegistrado(String emailUsuario, String codigoDocumento, String titulo) {
        String asunto = "Documento Registrado - " + codigoDocumento;
        String mensaje = String.format("""
            Estimado usuario,
            
            Su documento ha sido registrado exitosamente en el Sistema Mesa de Partes Digital.
            
            Código: %s
            Título: %s
            
            Puede consultar el estado de su trámite en cualquier momento usando el código proporcionado.
            
            Saludos,
            Sistema Mesa de Partes PNP
            """, codigoDocumento, titulo);
        
        enviarEmail(emailUsuario, asunto, mensaje);
    }
    
    /**
     * Email para documento derivado
     */
    public void notificarDocumentoDerivado(String emailUsuario, String codigoDocumento, String area) {
        String asunto = "Documento Derivado - " + codigoDocumento;
        String mensaje = String.format("""
            Estimado usuario,
            
            Se le ha derivado un nuevo documento para su atención.
            
            Código: %s
            Área destino: %s
            
            Por favor, revise el documento en el sistema.
            
            Saludos,
            Sistema Mesa de Partes PNP
            """, codigoDocumento, area);
        
        enviarEmail(emailUsuario, asunto, mensaje);
    }
}
```

#### 4.4 Actualizar NotificacionService
```java
@Service
public class NotificacionService {
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @Autowired
    private EmailService emailService; // NUEVO
    
    public void crearNotificacion(TipoNotificacion tipo, Long idUsuario, 
                                   Long idDocumento, String mensaje, String emailUsuario) {
        // Crear notificación in-app (existente)
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(tipo);
        notificacion.setIdUsuario(idUsuario);
        notificacion.setIdDocumento(idDocumento);
        notificacion.setMensaje(mensaje);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setLeida(false);
        notificacionRepository.save(notificacion);
        
        // NUEVO: Enviar email si tiene email
        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            String asunto = obtenerAsuntoSegunTipo(tipo);
            emailService.enviarEmail(emailUsuario, asunto, mensaje);
        }
    }
    
    private String obtenerAsuntoSegunTipo(TipoNotificacion tipo) {
        return switch (tipo) {
            case DOCUMENTO_REGISTRADO -> "Documento Registrado - Mesa de Partes PNP";
            case DOCUMENTO_DERIVADO -> "Nuevo Documento Asignado - Mesa de Partes PNP";
            case DOCUMENTO_RECIBIDO -> "Documento Recibido - Mesa de Partes PNP";
            case ESTADO_ACTUALIZADO -> "Estado Actualizado - Mesa de Partes PNP";
        };
    }
}
```

#### 4.5 Actualizar DocumentoController
```java
@PostMapping("/registrar")
public ResponseEntity<?> registrarDocumento(@RequestBody DocumentoRegistroDTO dto) {
    try {
        // ... lógica existente ...
        
        // NUEVO: Enviar email de confirmación
        if (dto.getEmailRemitente() != null) {
            emailService.notificarDocumentoRegistrado(
                dto.getEmailRemitente(),
                documento.getCodigo(),
                documento.getTitulo()
            );
        }
        
        return ResponseEntity.ok(documento);
    } catch (Exception e) {
        log.error("Error al registrar documento: ", e);
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
```

#### 4.6 Probar envío de email
```java
// Crear endpoint de prueba
@GetMapping("/test-email")
public ResponseEntity<String> testEmail() {
    emailService.enviarEmail(
        "tu-email@gmail.com",
        "Prueba Sistema Mesa de Partes",
        "Este es un email de prueba. Si lo recibes, el sistema funciona correctamente."
    );
    return ResponseEntity.ok("Email enviado");
}
```

✅ **Verificación:**
- [ ] Dependencia mail agregada
- [ ] Gmail App Password configurado
- [ ] EmailService creado
- [ ] NotificacionService actualizado
- [ ] Email de prueba enviado exitosamente
- [ ] Emails llegan correctamente

---

### PASO 5: Configurar HTTPS/SSL (4-6 horas) 🔒

#### 5.1 Generar certificado autofirmado (desarrollo)
```bash
# Windows/Linux/Mac
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365

# Datos requeridos:
# - Password: tu-password-seguro
# - Nombre: Tu Nombre
# - Organización: PNP
# - Ciudad: Lima
# - Estado: Lima
# - País: PE
```

#### 5.2 Mover keystore a resources
```bash
# Mover archivo generado
move keystore.p12 backend\src\main\resources\

# O copiar
cp keystore.p12 backend/src/main/resources/
```

#### 5.3 Actualizar application.properties
```properties
# HTTPS Configuration
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat

# Redirección HTTP a HTTPS (puerto 8080 -> 8443)
server.http.port=8080
```

#### 5.4 Crear configuración de redirección
```java
// HttpsRedirectConfig.java
package com.pnp.mesadepartes.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsRedirectConfig {
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }
    
    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
```

#### 5.5 Agregar password al .env
```bash
# .env
SSL_KEYSTORE_PASSWORD=tu-password-seguro
```

#### 5.6 Actualizar frontend para HTTPS
```javascript
// config.js
const API_URL = window.location.protocol === 'https:' 
    ? 'https://localhost:8443/api'
    : 'http://localhost:8080/api';
```

#### 5.7 Para producción con Let's Encrypt
```bash
# Instalar Certbot
sudo apt-get update
sudo apt-get install certbot

# Obtener certificado
sudo certbot certonly --standalone -d tu-dominio.com

# Certificados generados en:
# /etc/letsencrypt/live/tu-dominio.com/fullchain.pem
# /etc/letsencrypt/live/tu-dominio.com/privkey.pem

# Convertir a PKCS12
openssl pkcs12 -export \
    -in /etc/letsencrypt/live/tu-dominio.com/fullchain.pem \
    -inkey /etc/letsencrypt/live/tu-dominio.com/privkey.pem \
    -out keystore.p12 \
    -name tomcat

# Renovación automática
sudo certbot renew --dry-run
```

✅ **Verificación:**
- [ ] Keystore generado
- [ ] application.properties actualizado
- [ ] HttpsRedirectConfig creado
- [ ] Aplicación arranca en puerto 8443
- [ ] https://localhost:8443 accesible
- [ ] Redirección HTTP → HTTPS funciona
- [ ] Frontend actualizado para HTTPS

---

## 📊 RESUMEN DE TIEMPO Y PRIORIDADES

| Paso | Tarea | Tiempo | Prioridad | Bloqueante |
|------|-------|--------|-----------|------------|
| 1 | Externalizar credenciales | 2-3h | 🔴 CRÍTICA | ✅ SÍ |
| 2 | Backups automáticos | 3-4h | 🔴 CRÍTICA | ✅ SÍ |
| 3 | Logging estructurado | 2-3h | 🔴 CRÍTICA | ❌ NO |
| 4 | Envío de emails | 4-6h | 🟡 ALTA | ❌ NO |
| 5 | HTTPS/SSL | 4-6h | 🔴 CRÍTICA | ✅ SÍ |
| **TOTAL** | | **15-22h** | | **3 bloqueantes** |

---

## ✅ CHECKLIST DE FINALIZACIÓN

### Antes de Subir a Producción
- [ ] ✅ Paso 1: Credenciales externalizadas
- [ ] ✅ Paso 2: Backups configurados y probados
- [ ] ✅ Paso 3: Logging funcionando
- [ ] ✅ Paso 4: Emails enviándose correctamente
- [ ] ✅ Paso 5: HTTPS configurado

### Testing Final
- [ ] Aplicación arranca sin errores
- [ ] Login funciona correctamente
- [ ] Registro de documentos funciona
- [ ] Derivaciones funcionan
- [ ] Emails se envían
- [ ] Backups se ejecutan automáticamente
- [ ] Logs se generan correctamente
- [ ] HTTPS funciona sin warnings

### Documentación
- [ ] README actualizado
- [ ] Variables de entorno documentadas
- [ ] Procedimientos de backup documentados
- [ ] Contactos de soporte definidos

---

## 🆘 TROUBLESHOOTING

### Problema: Emails no se envían
```
Solución:
1. Verificar Gmail App Password
2. Verificar que 2FA esté activado
3. Revisar logs: logs/error.log
4. Probar con endpoint de prueba
```

### Problema: Backup falla
```
Solución:
1. Verificar credenciales de MySQL
2. Verificar rutas de directorios
3. Verificar permisos de escritura
4. Revisar log: backup.log
```

### Problema: HTTPS no funciona
```
Solución:
1. Verificar keystore existe en resources
2. Verificar password en .env
3. Verificar puerto 8443 disponible
4. Revisar firewall
```

### Problema: Logs no se generan
```
Solución:
1. Crear carpeta logs/ manualmente
2. Verificar permisos de escritura
3. Verificar logback-spring.xml
4. Reiniciar aplicación
```

---

## 📞 PRÓXIMOS PASOS

Después de completar estas 5 acciones:
1. ✅ Hacer pruebas exhaustivas
2. ✅ Configurar servidor de producción
3. ✅ Configurar dominio y DNS
4. ✅ Implementar monitoreo (Paso 5 del CHECKLIST_PRODUCCION.md)
5. ✅ Realizar deploy a producción

---

**Última actualización:** 17 de noviembre de 2025  
**Próxima revisión:** Después de implementar los 5 pasos
