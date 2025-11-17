# 🚀 GUÍA DE DESPLIEGUE EN PRODUCCIÓN

## Pre-requisitos

### Sistema Operativo
- **Linux:** Ubuntu 20.04/22.04 LTS o CentOS 8+ (Recomendado)
- **Windows Server:** 2019/2022 (Alternativa)

### Software Requerido
- [x] **Java 21 LTS** instalado
- [x] **MySQL 8.0.40+** corriendo
- [x] **Maven 3.9+** instalado
- [x] **Nginx** (para proxy reverso)
- [x] **Git** para clonar el proyecto

---

## FASE 1: Preparación del Servidor

### 1. Actualizar Sistema

```bash
# Ubuntu/Debian
sudo apt-get update && sudo apt-get upgrade -y

# CentOS/RHEL
sudo yum update -y
```

### 2. Instalar Java 21

```bash
# Ubuntu/Debian
sudo apt-get install openjdk-21-jdk -y

# Verificar
java -version  # Debe mostrar "openjdk version 21"
```

### 3. Instalar MySQL 8.0

```bash
# Ubuntu/Debian
sudo apt-get install mysql-server -y

# Iniciar y habilitar
sudo systemctl start mysql
sudo systemctl enable mysql

# Configuración segura
sudo mysql_secure_installation
```

### 4. Instalar Nginx

```bash
sudo apt-get install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 5. Configurar Firewall

```bash
# Ubuntu/Debian (UFW)
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw enable

# CentOS (firewalld)
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

---

## FASE 2: Configuración de Base de Datos

### 1. Crear Base de Datos

```bash
sudo mysql -u root -p
```

```sql
-- Crear base de datos
CREATE DATABASE mesa_partes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario
CREATE USER 'mesapartes'@'localhost' IDENTIFIED BY 'PASSWORD_SUPER_SEGURO_AQUI';

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON mesa_partes_db.* TO 'mesapartes'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

### 2. Importar Schema

```bash
# Opción 1: Schema completo
mysql -u mesapartes -p mesa_partes_db < SQL/mesa_partes_db_completa_con_funcionalidades.sql

# Opción 2: Solo nuevas funcionalidades
mysql -u mesapartes -p mesa_partes_db < SQL/actualizacion_nuevas_funcionalidades.sql
```

### 3. Verificar Importación

```bash
mysql -u mesapartes -p mesa_partes_db -e "SHOW TABLES;"
```

---

## FASE 3: Despliegue de la Aplicación

### 1. Clonar Repositorio

```bash
sudo mkdir -p /opt/mesa-partes
sudo chown $USER:$USER /opt/mesa-partes
cd /opt/mesa-partes

# Clonar proyecto
git clone https://github.com/TU_USUARIO/ProyectoMesaDePartes.git .
```

### 2. Configurar Variables de Entorno

```bash
cd /opt/mesa-partes
nano .env
```

Contenido:

```bash
# Base de Datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mesa_partes_db
DB_USERNAME=mesapartes
DB_PASSWORD=PASSWORD_SUPER_SEGURO_AQUI

# JWT
JWT_SECRET=GENERAR_SECRETO_ALEATORIO_64_CARACTERES_AQUI
JWT_EXPIRATION=86400000

# Email (Gmail)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=tu-email@gmail.com
SMTP_PASSWORD=APP_PASSWORD_AQUI
EMAIL_FROM=tu-email@gmail.com

# SSL (Producción)
SSL_ENABLED=true
SSL_KEYSTORE_PASSWORD=PASSWORD_KEYSTORE_AQUI

# Uploads
UPLOAD_DIR=/opt/mesa-partes/uploads
```

### 3. Generar JWT Secret

```bash
# Generar secreto aleatorio de 64 caracteres
openssl rand -base64 48
```

Copiar resultado en `.env` → `JWT_SECRET=`

### 4. Crear Directorio de Uploads

```bash
mkdir -p /opt/mesa-partes/uploads/documentos
mkdir -p /opt/mesa-partes/uploads/cargos
chown -R $USER:$USER /opt/mesa-partes/uploads
chmod 755 /opt/mesa-partes/uploads
```

### 5. Compilar Backend

```bash
cd /opt/mesa-partes/backend

# Compilar y empaquetar
./mvnw clean package -DskipTests

# JAR generado en: target/mesadepartes-0.0.1-SNAPSHOT.jar
```

### 6. Configurar SSL/HTTPS

Seguir guía completa: [CONFIGURAR_HTTPS_SSL.md](CONFIGURAR_HTTPS_SSL.md)

**Opción Recomendada:** Nginx + Let's Encrypt

```bash
# Obtener certificado SSL gratuito
sudo certbot --nginx -d tu-dominio.com -d www.tu-dominio.com
```

### 7. Crear Servicio Systemd

```bash
sudo nano /etc/systemd/system/mesa-partes.service
```

Contenido:

```ini
[Unit]
Description=Mesa de Partes PNP - Spring Boot Application
After=syslog.target network.target mysql.service

[Service]
Type=simple
User=mesapartes
Group=mesapartes

# Variables de entorno
EnvironmentFile=/opt/mesa-partes/.env

# Comando de ejecución
ExecStart=/usr/bin/java -jar /opt/mesa-partes/backend/target/mesadepartes-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=prod \
    --server.port=8080

# Directorio de trabajo
WorkingDirectory=/opt/mesa-partes

# Logs
StandardOutput=journal
StandardError=journal
SyslogIdentifier=mesa-partes

# Reinicio automático
Restart=always
RestartSec=10

# Límites de recursos
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
```

### 8. Crear Usuario de Sistema

```bash
sudo useradd -r -s /bin/false mesapartes
sudo chown -R mesapartes:mesapartes /opt/mesa-partes
```

### 9. Iniciar Servicio

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar inicio automático
sudo systemctl enable mesa-partes

# Iniciar servicio
sudo systemctl start mesa-partes

# Verificar estado
sudo systemctl status mesa-partes

# Ver logs en tiempo real
sudo journalctl -u mesa-partes -f
```

---

## FASE 4: Configuración de Nginx

### 1. Configurar Virtual Host

```bash
sudo nano /etc/nginx/sites-available/mesa-partes
```

Contenido:

```nginx
# Redirigir HTTP a HTTPS
server {
    listen 80;
    server_name tu-dominio.com www.tu-dominio.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS
server {
    listen 443 ssl http2;
    server_name tu-dominio.com www.tu-dominio.com;

    # Certificados SSL (Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/tu-dominio.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/tu-dominio.com/privkey.pem;

    # Configuración SSL segura
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_session_timeout 10m;
    ssl_session_cache shared:SSL:10m;

    # Headers de seguridad
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    # Límite de tamaño de archivos (50MB)
    client_max_body_size 50M;

    # Frontend estático
    location / {
        root /opt/mesa-partes/frontend;
        index login.html index.html;
        try_files $uri $uri/ /login.html;
    }

    # Proxy a Spring Boot (Backend API)
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        
        # Headers para proxy
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Archivos subidos
    location /uploads/ {
        alias /opt/mesa-partes/uploads/;
        expires 1y;
        add_header Cache-Control "public, immutable";
        
        # Evitar listado de directorios
        autoindex off;
    }

    # Assets estáticos (CSS, JS, imágenes)
    location ~* \.(css|js|jpg|jpeg|png|gif|ico|svg|woff|woff2|ttf|eot)$ {
        root /opt/mesa-partes/frontend;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Logs
    access_log /var/log/nginx/mesa-partes-access.log;
    error_log /var/log/nginx/mesa-partes-error.log;
}
```

### 2. Activar Configuración

```bash
# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/mesa-partes /etc/nginx/sites-enabled/

# Verificar sintaxis
sudo nginx -t

# Recargar Nginx
sudo systemctl reload nginx
```

---

## FASE 5: Configurar Backups Automáticos

### 1. Script de Backup (Linux)

```bash
sudo nano /opt/mesa-partes/scripts/backup_prod.sh
```

Contenido:

```bash
#!/bin/bash
# Backup automático para producción

# Variables
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/mesa-partes/backups"
DB_NAME="mesa_partes_db"
DB_USER="mesapartes"
DB_PASS="PASSWORD_AQUI"
UPLOADS_DIR="/opt/mesa-partes/uploads"
RETENTION_DAYS=30

# Crear directorio
mkdir -p $BACKUP_DIR

# Backup de Base de Datos
mysqldump -u$DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/db_$TIMESTAMP.sql

# Backup de Uploads
tar -czf $BACKUP_DIR/uploads_$TIMESTAMP.tar.gz $UPLOADS_DIR

# Eliminar backups antiguos
find $BACKUP_DIR -name "*.sql" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +$RETENTION_DAYS -delete

echo "Backup completado: $TIMESTAMP"
```

### 2. Permisos y Programación

```bash
# Dar permisos de ejecución
sudo chmod +x /opt/mesa-partes/scripts/backup_prod.sh

# Programar cron (diario a las 2 AM)
sudo crontab -e

# Agregar línea:
0 2 * * * /opt/mesa-partes/scripts/backup_prod.sh >> /var/log/mesa-partes-backup.log 2>&1
```

---

## FASE 6: Monitoreo y Logs

### 1. Ver Logs de Aplicación

```bash
# Logs en tiempo real
sudo tail -f /opt/mesa-partes/backend/logs/application.log

# Logs de errores
sudo tail -f /opt/mesa-partes/backend/logs/error.log

# Logs de systemd
sudo journalctl -u mesa-partes -f
```

### 2. Ver Logs de Nginx

```bash
# Access logs
sudo tail -f /var/log/nginx/mesa-partes-access.log

# Error logs
sudo tail -f /var/log/nginx/mesa-partes-error.log
```

### 3. Monitorear Recursos

```bash
# CPU y memoria
htop

# Espacio en disco
df -h

# Estado de servicios
sudo systemctl status mesa-partes mysql nginx
```

### 4. Endpoints de Salud

```bash
# Health check de Spring Boot
curl https://tu-dominio.com/api/actuator/health

# Respuesta esperada:
# {"status":"UP"}
```

---

## FASE 7: Actualizaciones

### 1. Actualizar Código

```bash
cd /opt/mesa-partes

# Detener servicio
sudo systemctl stop mesa-partes

# Actualizar desde Git
git pull origin main

# Recompilar
cd backend
./mvnw clean package -DskipTests

# Reiniciar servicio
sudo systemctl start mesa-partes
```

### 2. Actualizar Base de Datos

```bash
# Ejecutar scripts de migración
mysql -u mesapartes -p mesa_partes_db < SQL/migracion_v2.sql
```

---

## FASE 8: Seguridad Adicional

### 1. Fail2Ban (Protección contra fuerza bruta)

```bash
# Instalar
sudo apt-get install fail2ban -y

# Configurar
sudo nano /etc/fail2ban/jail.local
```

Contenido:

```ini
[nginx-noscript]
enabled = true
port = http,https
filter = nginx-noscript
logpath = /var/log/nginx/mesa-partes-access.log
maxretry = 6
bantime = 600

[nginx-badbots]
enabled = true
port = http,https
filter = nginx-badbots
logpath = /var/log/nginx/mesa-partes-access.log
maxretry = 2
bantime = 86400
```

```bash
sudo systemctl restart fail2ban
```

### 2. Actualizar Regularmente

```bash
# Crear script de actualización automática
sudo nano /etc/cron.daily/system-update
```

```bash
#!/bin/bash
apt-get update && apt-get upgrade -y && apt-get autoremove -y
```

```bash
sudo chmod +x /etc/cron.daily/system-update
```

---

## FASE 9: Testing Post-Despliegue

### Checklist de Verificación

- [ ] **Backend:** https://tu-dominio.com/api/actuator/health → `{"status":"UP"}`
- [ ] **Frontend:** https://tu-dominio.com → Página de login carga
- [ ] **Login:** Usuario admin puede iniciar sesión
- [ ] **Registro documento:** Subir archivo funciona
- [ ] **Derivación:** Asignar documento a otro usuario funciona
- [ ] **Email:** Llegan notificaciones por correo
- [ ] **PDF:** Generar cargo funciona
- [ ] **Trazabilidad:** Histórico de documento se muestra
- [ ] **SSL:** Certificado válido (sin advertencias)
- [ ] **HTTP→HTTPS:** Redirección automática funciona
- [ ] **Backup:** Script de backup ejecuta correctamente
- [ ] **Logs:** Logs se generan en `/opt/mesa-partes/backend/logs/`

### Test de Carga (Opcional)

```bash
# Instalar Apache Bench
sudo apt-get install apache2-utils -y

# Test con 1000 requests, 10 concurrentes
ab -n 1000 -c 10 https://tu-dominio.com/api/actuator/health
```

---

## Troubleshooting Común

### Error: "Connection refused" al acceder a la API

```bash
# Verificar que Spring Boot esté corriendo
sudo systemctl status mesa-partes

# Ver logs de error
sudo journalctl -u mesa-partes -n 50
```

### Error: "502 Bad Gateway" en Nginx

```bash
# Verificar puerto de backend
sudo netstat -tlnp | grep 8080

# Verificar configuración Nginx
sudo nginx -t
```

### Error: "Access denied for user"

```bash
# Verificar credenciales en .env
cat /opt/mesa-partes/.env | grep DB_

# Verificar permisos MySQL
mysql -u root -p -e "SHOW GRANTS FOR 'mesapartes'@'localhost';"
```

### Archivos no se suben

```bash
# Verificar permisos
ls -la /opt/mesa-partes/uploads

# Corregir permisos
sudo chown -R mesapartes:mesapartes /opt/mesa-partes/uploads
sudo chmod 755 /opt/mesa-partes/uploads
```

---

## Contactos de Soporte

- **Desarrollador:** [Tu nombre/email]
- **Administrador del sistema:** [Contacto IT]
- **Base de datos:** [DBA contacto]

---

## Próximos Pasos

1. **Monitoreo:** Configurar sistema de monitoreo (Prometheus + Grafana)
2. **CDN:** Integrar CloudFlare para mejor performance
3. **Balanceo de carga:** Si tráfico aumenta, configurar múltiples instancias
4. **CI/CD:** Automatizar despliegues con GitHub Actions o Jenkins

---

**Última actualización:** 17 de noviembre de 2025  
**Versión:** 1.0.0  
**Estado:** ✅ Listo para producción
