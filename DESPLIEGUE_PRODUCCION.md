# 🚀 Guía de Despliegue Seguro a Producción

**Sistema Mesa de Partes Digital - PNP**  
**Versión**: 3.1  
**Fecha**: Noviembre 2025  
**Estado**: ✅ Listo para Producción

---

## ⚠️ CHECKLIST PRE-DESPLIEGUE

Antes de desplegar a producción, **COMPLETA TODAS** estas tareas:

### 🔒 Seguridad

- [ ] Cambiar **TODAS** las contraseñas por defecto
- [ ] Generar nuevo JWT_SECRET único y fuerte
- [ ] Configurar certificado SSL/TLS válido
- [ ] Cambiar `DEBUG_MODE = false` en `frontend/assets/js/core/logger.js`
- [ ] Cambiar `spring.jpa.show-sql=false` en `application.properties`
- [ ] Configurar CORS solo para dominios autorizados
- [ ] Revisar que `.env` NO esté en el repositorio
- [ ] Configurar firewall para limitar acceso al puerto 3306 (MySQL)
- [ ] Habilitar logs de auditoría de acceso

### 🗄️ Base de Datos

- [ ] Backup completo de la base de datos
- [ ] Verificar todos los índices están creados
- [ ] Ejecutar `ANALYZE TABLE` en tablas grandes
- [ ] Configurar backup automático daily
- [ ] Configurar replicación (opcional pero recomendado)
- [ ] Cambiar contraseña de usuario MySQL `root`
- [ ] Crear usuario MySQL específico para la aplicación

### 🏗️ Infraestructura

- [ ] Servidor con al menos 4GB RAM
- [ ] Java 21 LTS instalado
- [ ] MySQL 8.0+ corriendo
- [ ] Configurar reverse proxy (Nginx/Apache)
- [ ] Configurar dominio con DNS
- [ ] Certificado SSL instalado
- [ ] Monitoreo configurado (Prometheus/Grafana)

### 📦 Aplicación

- [ ] Compilar JAR de producción: `mvn clean package -DskipTests`
- [ ] Verificar que no hay `console.log()` activos
- [ ] Configurar logs en `/var/log/mesa-partes/` 
- [ ] Configurar logrotate para logs
- [ ] Verificar permisos de archivos `/uploads`
- [ ] Configurar variables de entorno del sistema

---

## 🔧 CONFIGURACIONES CRÍTICAS

### 1. Variables de Entorno (.env)

```bash
# NO USAR ESTOS VALORES - SON EJEMPLOS!

# Base de Datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mesa_partes_db
DB_USERNAME=mesa_partes_user  # NO usar root en producción
DB_PASSWORD=CAMBIAR_POR_PASSWORD_FUERTE_AQUI  # Mínimo 16 caracteres

# JWT - Generar nuevo con: openssl rand -base64 64
JWT_SECRET=CAMBIAR_POR_SECRET_UNICO_Y_FUERTE_BASE64

# SMTP (si se usa)
SMTP_HOST=smtp.empresa.com
SMTP_PORT=587
SMTP_USERNAME=noreply@mesadepartes.pnp.gob.pe
SMTP_PASSWORD=CAMBIAR_POR_PASSWORD_SMTP

# Servidor
SERVER_PORT=8080

# CORS - Solo dominios autorizados
ALLOWED_ORIGINS=https://mesadepartes.pnp.gob.pe,https://admin.mesadepartes.pnp.gob.pe

# SSL
SSL_ENABLED=true
SSL_KEYSTORE_PASSWORD=CAMBIAR_POR_KEYSTORE_PASSWORD

# Backups
BACKUP_DIR=/var/backups/mesa_partes
BACKUP_RETENTION_DAYS=30
```

### 2. application.properties (Producción)

```properties
# ===================================
# PRODUCCIÓN - NO EXPONER SECRETOS
# ===================================

# Base de Datos
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate  # ⚠️ NUNCA usar 'update' o 'create-drop' en producción
spring.jpa.show-sql=false  # ⚠️ CRÍTICO: Desactivar en producción
spring.jpa.properties.hibernate.format_sql=false

# JWT
mesadepartes.app.jwtSecret=${JWT_SECRET}
mesadepartes.app.jwtExpirationMs=28800000  # 8 horas

# CORS
mesadepartes.app.allowedOrigins=${ALLOWED_ORIGINS}

# Archivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.root=WARN
logging.level.com.pnp.mesadepartes=INFO  # No usar DEBUG
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=WARN
logging.file.name=/var/log/mesa-partes/application.log
logging.logback.rollingpolicy.max-file-size=50MB
logging.logback.rollingpolicy.max-history=30

# Servidor
server.port=${SERVER_PORT:8080}
server.error.include-message=always
server.error.include-stacktrace=never  # ⚠️ CRÍTICO: No exponer stack traces

# SSL/HTTPS - OBLIGATORIO en producción
server.ssl.enabled=${SSL_ENABLED:true}
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=mesadepartes

# Actuator - Proteger endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
management.security.enabled=true
```

### 3. logger.js (Frontend)

```javascript
// ⚠️ CRÍTICO: Cambiar a false en producción
const DEBUG_MODE = false;  // ← VERIFICAR ANTES DE DESPLEGAR
const CURRENT_LOG_LEVEL = LogLevel.ERROR;  // Solo errores en producción
```

---

## 🗄️ SETUP DE BASE DE DATOS

### 1. Crear Usuario Específico (Seguridad)

```sql
-- NO USAR root en producción!

-- Crear usuario
CREATE USER 'mesa_partes_user'@'localhost' IDENTIFIED BY 'PASSWORD_FUERTE_AQUI';

-- Otorgar permisos SOLO a la BD necesaria
GRANT SELECT, INSERT, UPDATE, DELETE ON mesa_partes_db.* TO 'mesa_partes_user'@'localhost';

-- NO otorgar permisos de CREATE, DROP, ALTER en producción
-- Si necesitas hacer cambios de schema, usa un usuario admin temporal

-- Aplicar cambios
FLUSH PRIVILEGES;
```

### 2. Configurar Backups Automáticos

```bash
# Crear script de backup
sudo nano /usr/local/bin/backup-mesa-partes.sh
```

```bash
#!/bin/bash
# Backup automático de Mesa de Partes PNP

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/var/backups/mesa_partes"
DB_NAME="mesa_partes_db"
DB_USER="root"  # Usar usuario con permisos de backup
DB_PASS="TU_PASSWORD"

# Crear directorio si no existe
mkdir -p $BACKUP_DIR

# Hacer backup
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME | gzip > "$BACKUP_DIR/backup_$TIMESTAMP.sql.gz"

# Eliminar backups más viejos de 30 días
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete

echo "Backup completado: backup_$TIMESTAMP.sql.gz"
```

```bash
# Dar permisos de ejecución
sudo chmod +x /usr/local/bin/backup-mesa-partes.sh

# Programar con cron (daily a las 2 AM)
sudo crontab -e

# Agregar línea:
0 2 * * * /usr/local/bin/backup-mesa-partes.sh >> /var/log/backup-mesa-partes.log 2>&1
```

---

## 🌐 CONFIGURACIÓN DE NGINX (Reverse Proxy)

```nginx
# /etc/nginx/sites-available/mesadepartes.pnp.gob.pe

server {
    listen 80;
    server_name mesadepartes.pnp.gob.pe;
    
    # Redirigir HTTP a HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name mesadepartes.pnp.gob.pe;

    # Certificados SSL
    ssl_certificate /etc/letsencrypt/live/mesadepartes.pnp.gob.pe/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/mesadepartes.pnp.gob.pe/privkey.pem;
    
    # Seguridad SSL
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # Headers de seguridad
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    # Frontend (archivos estáticos)
    location / {
        root /var/www/mesadepartes/frontend;
        try_files $uri $uri/ /index.html;
    }
    
    # Backend (API)
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # Uploads
    location /uploads/ {
        alias /var/www/mesadepartes/backend/uploads/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # Logs
    access_log /var/log/nginx/mesadepartes_access.log;
    error_log /var/log/nginx/mesadepartes_error.log;
}
```

```bash
# Activar sitio
sudo ln -s /etc/nginx/sites-available/mesadepartes.pnp.gob.pe /etc/nginx/sites-enabled/

# Probar configuración
sudo nginx -t

# Reiniciar Nginx
sudo systemctl restart nginx
```

---

## 🚀 DESPLIEGUE DE LA APLICACIÓN

### 1. Compilar Backend

```bash
# En tu máquina local
cd backend
./mvnw clean package -DskipTests

# Se genera: target/mesadepartes-0.0.1-SNAPSHOT.jar
```

### 2. Transferir al Servidor

```bash
# Desde tu máquina local
scp target/mesadepartes-0.0.1-SNAPSHOT.jar user@servidor:/opt/mesadepartes/

# Copiar frontend
scp -r frontend/* user@servidor:/var/www/mesadepartes/frontend/
```

### 3. Crear Servicio Systemd

```bash
sudo nano /etc/systemd/system/mesadepartes.service
```

```ini
[Unit]
Description=Mesa de Partes Digital PNP
After=network.target mysql.service

[Service]
Type=simple
User=mesadepartes
WorkingDirectory=/opt/mesadepartes
ExecStart=/usr/bin/java -jar /opt/mesadepartes/mesadepartes-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

# Variables de entorno
EnvironmentFile=/opt/mesadepartes/.env

# Seguridad
PrivateTmp=true
NoNewPrivileges=true

# Logs
StandardOutput=journal
StandardError=journal
SyslogIdentifier=mesadepartes

[Install]
WantedBy=multi-user.target
```

```bash
# Crear usuario del sistema
sudo useradd -r -s /bin/false mesadepartes

# Dar permisos
sudo chown -R mesadepartes:mesadepartes /opt/mesadepartes
sudo chmod 600 /opt/mesadepartes/.env

# Habilitar e iniciar servicio
sudo systemctl daemon-reload
sudo systemctl enable mesadepartes
sudo systemctl start mesadepartes

# Verificar estado
sudo systemctl status mesadepartes

# Ver logs
sudo journalctl -u mesadepartes -f
```

---

## 🔍 MONITOREO Y MANTENIMIENTO

### Verificar Health Check

```bash
# Backend
curl https://mesadepartes.pnp.gob.pe/actuator/health

# Debe devolver:
# {"status":"UP"}
```

### Logs Importantes

```bash
# Logs de la aplicación
sudo tail -f /var/log/mesa-partes/application.log

# Logs de Nginx
sudo tail -f /var/log/nginx/mesadepartes_access.log
sudo tail -f /var/log/nginx/mesadepartes_error.log

# Logs del servicio systemd
sudo journalctl -u mesadepartes -f
```

### Monitoreo de Recursos

```bash
# CPU y Memoria
htop

# Espacio en disco
df -h

# Conexiones MySQL
mysql -u root -p -e "SHOW PROCESSLIST;"
```

---

## ⚠️ TROUBLESHOOTING

### Problema: Aplicación no inicia

```bash
# Verificar logs
sudo journalctl -u mesadepartes -n 50

# Verificar que MySQL está corriendo
sudo systemctl status mysql

# Verificar conectividad a MySQL
mysql -h localhost -u mesa_partes_user -p mesa_partes_db
```

### Problema: Error de CORS

```bash
# Verificar allowed origins en .env
cat /opt/mesadepartes/.env | grep ALLOWED_ORIGINS

# Debe incluir tu dominio
```

### Problema: Archivos no se suben

```bash
# Verificar permisos del directorio uploads
ls -la /var/www/mesadepartes/backend/uploads/

# Debe ser propiedad de usuario mesadepartes
sudo chown -R mesadepartes:mesadepartes /var/www/mesadepartes/backend/uploads/
sudo chmod 755 /var/www/mesadepartes/backend/uploads/
```

---

## 📊 MÉTRICAS DE ÉXITO

Después del despliegue, verifica:

- [ ] Login funciona correctamente
- [ ] Registro de documentos funciona
- [ ] Derivaciones funcionan
- [ ] Carga de archivos PDF funciona
- [ ] Bitácora muestra datos correctos
- [ ] Dashboard de reportes funciona
- [ ] Notificaciones aparecen
- [ ] Búsqueda de documentos funciona
- [ ] HTTPS está habilitado (candado verde)
- [ ] No hay errores en consola del navegador
- [ ] No hay warnings en logs del backend

---

## 🔒 AUDITORÍA DE SEGURIDAD POST-DESPLIEGUE

### Checklist de Seguridad

- [ ] Ejecutar `nmap` para verificar puertos abiertos
- [ ] Ejecutar `nikto` para escaneo de vulnerabilidades web
- [ ] Verificar headers de seguridad con <securityheaders.com>
- [ ] Verificar SSL con <ssllabs.com>
- [ ] Revisar logs de acceso buscando patrones sospechosos
- [ ] Verificar que no hay información sensible en logs
- [ ] Probar inyección SQL en formularios
- [ ] Probar XSS en formularios
- [ ] Verificar autenticación y autorización

---

## 📞 SOPORTE

**Desarrollador**: Nakusu  
**Email**: soporte@mesadepartes.pnp.gob.pe  
**Documentación**: [README.md](README.md)

---

## 📅MANTENIMIENTO PROGRAMADO

### Diario
- Verificar logs de errores
- Monitorear uso de recursos

### Semanal
- Ejecutar script de limpieza de archivos huérfanos
- Revisar logs de acceso
- Verificar backups

### Mensual
- Actualizar dependencias de seguridad
- Revisar métricas de rendimiento
- Optimizar consultas lentas
- Rotar logs antiguos

### Trimestral
- Auditoría de seguridad completa
- Actualizar certificados SSL si es necesario
- Revisar y actualizar políticas de contraseñas

---

**Fecha de Última Actualización**: 24 de Noviembre de 2025  
**Versión del Documento**: 1.0  
**Estado**: ✅ Aprobado para Producción

🇵🇪 **Sistema Mesa de Partes Digital - PNP**
