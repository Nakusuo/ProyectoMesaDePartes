# 🔒 CONFIGURAR HTTPS/SSL - GUÍA COMPLETA

## Para Desarrollo Local (Certificado Autofirmado)

### Paso 1: Generar Certificado con Keytool

```bash
# Windows/Linux/Mac
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365

# Responder las preguntas:
# Contraseña del almacén: [CREAR_PASSWORD_SEGURO]
# Nombre y apellido: Tu Nombre
# Unidad de organización: Desarrollo
# Organización: PNP
# Ciudad: Lima
# Estado: Lima
# Código de país: PE
```

### Paso 2: Mover Keystore a Resources

```bash
# Windows
move keystore.p12 backend\src\main\resources\

# Linux/Mac
mv keystore.p12 backend/src/main/resources/
```

### Paso 3: Configurar Variables de Entorno

Editar archivo `.env`:

```bash
# SSL Configuration
SSL_ENABLED=true
SSL_KEYSTORE_PASSWORD=TU_PASSWORD_AQUI
```

### Paso 4: Activar SSL en application.properties

Descomentar estas líneas en `application.properties`:

```properties
# SSL/HTTPS Configuration (Producción)
server.ssl.enabled=${SSL_ENABLED:false}
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

### Paso 5: Configurar Puerto HTTPS

```properties
# Cambiar puerto principal a HTTPS
server.port=8443
```

### Paso 6: Actualizar Frontend

Editar `frontend/assets/js/core/config.js`:

```javascript
// Detectar automáticamente el protocolo
const API_URL = window.location.protocol === 'https:' 
    ? 'https://localhost:8443/api'
    : 'http://localhost:8080/api';
```

### Paso 7: Iniciar Aplicación

```bash
cd backend
mvnw spring-boot:run
```

Acceder a:
- **HTTPS:** https://localhost:8443
- **HTTP:** http://localhost:8080 (redirige a HTTPS)

⚠️ **NOTA:** El navegador mostrará advertencia de seguridad (certificado autofirmado). Hacer click en "Avanzado" → "Aceptar riesgo y continuar"

---

## Para Producción (Let's Encrypt - GRATIS)

### Opción 1: Con Certbot (Linux)

#### Paso 1: Instalar Certbot

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install certbot

# CentOS/RHEL
sudo yum install certbot
```

#### Paso 2: Obtener Certificado

```bash
# Detener Spring Boot temporalmente
sudo certbot certonly --standalone -d tu-dominio.com -d www.tu-dominio.com

# Certificados generados en:
# /etc/letsencrypt/live/tu-dominio.com/fullchain.pem
# /etc/letsencrypt/live/tu-dominio.com/privkey.pem
```

#### Paso 3: Convertir a PKCS12

```bash
sudo openssl pkcs12 -export \
    -in /etc/letsencrypt/live/tu-dominio.com/fullchain.pem \
    -inkey /etc/letsencrypt/live/tu-dominio.com/privkey.pem \
    -out /etc/letsencrypt/live/tu-dominio.com/keystore.p12 \
    -name tomcat \
    -passout pass:TU_PASSWORD_SEGURO
```

#### Paso 4: Copiar Keystore

```bash
sudo cp /etc/letsencrypt/live/tu-dominio.com/keystore.p12 /opt/mesa-partes/
sudo chown mesapartes:mesapartes /opt/mesa-partes/keystore.p12
```

#### Paso 5: Configurar application.properties (Producción)

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=file:/opt/mesa-partes/keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

#### Paso 6: Renovación Automática

```bash
# Crear script de renovación
sudo nano /opt/mesa-partes/renew-cert.sh
```

Contenido:

```bash
#!/bin/bash
certbot renew --quiet

if [ $? -eq 0 ]; then
    # Convertir a PKCS12
    openssl pkcs12 -export \
        -in /etc/letsencrypt/live/tu-dominio.com/fullchain.pem \
        -inkey /etc/letsencrypt/live/tu-dominio.com/privkey.pem \
        -out /etc/letsencrypt/live/tu-dominio.com/keystore.p12 \
        -name tomcat \
        -passout pass:$SSL_KEYSTORE_PASSWORD
    
    # Copiar y reiniciar
    cp /etc/letsencrypt/live/tu-dominio.com/keystore.p12 /opt/mesa-partes/
    systemctl restart mesa-partes
    
    echo "Certificado renovado exitosamente"
fi
```

```bash
# Dar permisos
sudo chmod +x /opt/mesa-partes/renew-cert.sh

# Programar renovación cada 80 días
sudo crontab -e
0 0 */80 * * /opt/mesa-partes/renew-cert.sh >> /var/log/cert-renewal.log 2>&1
```

---

### Opción 2: Con Nginx Reverse Proxy (Recomendado para Producción)

#### Ventajas:
- ✅ Nginx maneja SSL (más eficiente)
- ✅ Spring Boot corre en HTTP interno
- ✅ Más fácil de configurar y mantener
- ✅ Balanceo de carga fácil

#### Paso 1: Instalar Nginx

```bash
sudo apt-get install nginx
```

#### Paso 2: Obtener Certificado Let's Encrypt

```bash
sudo certbot --nginx -d tu-dominio.com -d www.tu-dominio.com
```

#### Paso 3: Configurar Nginx

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
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Proxy a Spring Boot
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Frontend estático
    location / {
        root /var/www/mesa-partes/frontend;
        index login.html index.html;
        try_files $uri $uri/ =404;
    }

    # Uploads
    location /uploads/ {
        alias /opt/mesa-partes/uploads/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Logs
    access_log /var/log/nginx/mesa-partes-access.log;
    error_log /var/log/nginx/mesa-partes-error.log;
}
```

#### Paso 4: Activar Configuración

```bash
sudo ln -s /etc/nginx/sites-available/mesa-partes /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

#### Paso 5: Spring Boot en HTTP (interno)

En `application.properties`:

```properties
# Puerto HTTP interno (Nginx hace proxy)
server.port=8080

# NO activar SSL en Spring Boot
# server.ssl.enabled=false
```

---

## Verificar Configuración SSL

### Test con curl

```bash
# Verificar HTTPS
curl -I https://tu-dominio.com

# Verificar redirección HTTP → HTTPS
curl -I http://tu-dominio.com
```

### Test con OpenSSL

```bash
openssl s_client -connect tu-dominio.com:443 -servername tu-dominio.com
```

### Test con SSLLabs

Ir a: https://www.ssllabs.com/ssltest/analyze.html?d=tu-dominio.com

Meta: Obtener calificación **A** o **A+**

---

## Troubleshooting

### Error: "keystore password was incorrect"

```bash
# Verificar password
keytool -list -v -keystore keystore.p12

# Re-generar con password correcto
```

### Error: "Port 8443 already in use"

```bash
# Windows: Ver qué usa el puerto
netstat -ano | findstr :8443

# Linux: Ver qué usa el puerto
sudo lsof -i :8443

# Matar proceso
taskkill /PID [número] /F    # Windows
sudo kill [número]            # Linux
```

### Error: "Certificate not trusted"

- **Desarrollo:** Normal, aceptar riesgo
- **Producción:** Verificar que Let's Encrypt esté instalado correctamente

### Frontend no carga (Mixed Content)

Verificar que todas las URLs usen HTTPS:

```javascript
// ❌ MALO
const API_URL = 'http://localhost:8080/api';

// ✅ BUENO
const API_URL = window.location.protocol === 'https:'
    ? 'https://tu-dominio.com/api'
    : 'http://localhost:8080/api';
```

---

## Checklist Final SSL

- [ ] Certificado generado o instalado
- [ ] Keystore en lugar correcto
- [ ] Password en .env
- [ ] application.properties configurado
- [ ] Nginx configurado (si aplica)
- [ ] Firewall permite puerto 443
- [ ] DNS apunta al servidor
- [ ] Renovación automática configurada
- [ ] Test SSL pasando (A o A+)
- [ ] HTTP redirige a HTTPS
- [ ] Frontend carga correctamente

---

## Costos

| Opción | Costo Anual | Renovación |
|--------|-------------|------------|
| **Let's Encrypt** | **$0** | Automática |
| Certificado comercial | $50-200 | Manual |
| Wildcard SSL | $100-300 | Manual |

**Recomendación:** Usar Let's Encrypt + Nginx

---

**Última actualización:** 17 de noviembre de 2025  
**Próxima revisión:** Cada 80 días (renovación de certificado)
