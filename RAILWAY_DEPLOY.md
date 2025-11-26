# 🚂 Guía Completa de Despliegue en Railway - Mesa de Partes PNP

## 📋 Tabla de Contenidos
1. [Prerequisitos](#prerequisitos)
2. [Paso 1: Preparar el Proyecto](#paso-1-preparar-el-proyecto)
3. [Paso 2: Crear Cuenta en Railway](#paso-2-crear-cuenta-en-railway)
4. [Paso 3: Configurar Proyecto](#paso-3-configurar-proyecto)
5. [Paso 4: Agregar Base de Datos MySQL](#paso-4-agregar-base-de-datos-mysql)
6. [Paso 5: Configurar Variables de Entorno](#paso-5-configurar-variables-de-entorno)
7. [Paso 6: Deploy Automático](#paso-6-deploy-automático)
8. [Paso 7: Importar Base de Datos](#paso-7-importar-base-de-datos)
9. [Paso 8: Verificar Funcionamiento](#paso-8-verificar-funcionamiento)
10. [Paso 9: Configurar Frontend](#paso-9-configurar-frontend)
11. [Troubleshooting](#troubleshooting)
12. [Monitoreo y Logs](#monitoreo-y-logs)

---

## 📦 Prerequisitos

- ✅ Cuenta de GitHub (para conectar con Railway)
- ✅ Git instalado
- ✅ Tu proyecto subido a GitHub
- ✅ 10-15 minutos de tiempo

---

## 🚀 Paso 1: Preparar el Proyecto

### 1.1 Verificar archivos creados

Asegúrate de tener estos archivos (ya están creados):

- ✅ `Dockerfile` - En la raíz del proyecto
- ✅ `.dockerignore` - En la raíz del proyecto
- ✅ `railway.json` - Configuración de Railway
- ✅ `.env.railway` - Ejemplo de variables

### 1.2 Subir a GitHub

```cmd
cd c:\Users\MARCELA\Desktop\SoftwareCore\ProyectoMesaDePartes

# Agregar todos los cambios
git add .

# Commit
git commit -m "Preparar proyecto para Railway"

# Push (si no lo has hecho)
git push origin main
```

**⚠️ IMPORTANTE:** Asegúrate de que `.env` esté en `.gitignore` (ya debería estarlo)

---

## 🎯 Paso 2: Crear Cuenta en Railway

### 2.1 Registrarse

1. Ve a: **https://railway.app**
2. Click en **"Start a New Project"** o **"Login"**
3. Selecciona **"Login with GitHub"**
4. Autoriza Railway a acceder a tu cuenta de GitHub

### 2.2 Verificar cuenta (si es necesario)

- Railway puede pedirte verificación por email
- También puede solicitar método de pago (pero NO te cobrará en el plan gratis)
- El plan gratis incluye **$5 de crédito/mes** o **500 horas de ejecución**

---

## 📁 Paso 3: Configurar Proyecto

### 3.1 Crear nuevo proyecto

1. En el Dashboard de Railway, click **"New Project"**
2. Selecciona **"Deploy from GitHub repo"**
3. Busca y selecciona tu repositorio: **`ProyectoMesaDePartes`**
4. Railway comenzará a detectar tu proyecto

### 3.2 Railway detecta automáticamente

Railway verá tu `Dockerfile` y `railway.json` y sabrá cómo hacer el deploy.

**Lo que Railway hace automáticamente:**
- ✅ Detecta Dockerfile
- ✅ Construye la imagen Docker
- ✅ Asigna un puerto público
- ✅ Genera URL pública con HTTPS
- ✅ Configura health checks

---

## 🗄️ Paso 4: Agregar Base de Datos MySQL

### 4.1 Agregar servicio MySQL

1. En tu proyecto de Railway, click **"+ New"**
2. Selecciona **"Database"**
3. Selecciona **"Add MySQL"**

Railway creará automáticamente:
- Una instancia MySQL
- Variables de entorno para conectarse
- Conexión privada entre servicios

### 4.2 Variables automáticas de MySQL

Railway crea estas variables automáticamente:
```
MYSQL_URL
MYSQL_HOST
MYSQL_PORT
MYSQL_DATABASE
MYSQL_USER
MYSQL_PASSWORD
MYSQL_PRIVATE_URL
```

**No necesitas copiarlas manualmente**, Railway las conecta automáticamente.

---

## ⚙️ Paso 5: Configurar Variables de Entorno

### 5.1 Ir a configuración de variables

1. Click en tu servicio **backend** (no en MySQL)
2. Ve a la pestaña **"Variables"**
3. Click **"New Variable"** o **"Raw Editor"**

### 5.2 Agregar variables necesarias

**Método 1: Una por una**

Agregar estas variables:

```env
# Conectar a MySQL (Railway las referencia automáticamente)
DB_HOST=${{MySQL.MYSQL_HOST}}
DB_PORT=${{MySQL.MYSQL_PORT}}
DB_NAME=${{MySQL.MYSQL_DATABASE}}
DB_USERNAME=${{MySQL.MYSQL_USER}}
DB_PASSWORD=${{MySQL.MYSQL_PASSWORD}}

# JWT Secret (OBLIGATORIO - genera uno nuevo)
JWT_SECRET=TU_SECRET_AQUI_GENERALO_CON_OPENSSL

# CORS (Railway proporciona RAILWAY_PUBLIC_DOMAIN)
ALLOWED_ORIGINS=https://${{RAILWAY_PUBLIC_DOMAIN}}
```

**Método 2: Raw Editor (más rápido)**

Click en **"Raw Editor"** y pega todo junto:

```env
DB_HOST=${{MySQL.MYSQL_HOST}}
DB_PORT=${{MySQL.MYSQL_PORT}}
DB_NAME=${{MySQL.MYSQL_DATABASE}}
DB_USERNAME=${{MySQL.MYSQL_USER}}
DB_PASSWORD=${{MySQL.MYSQL_PASSWORD}}
JWT_SECRET=TU_SECRET_GENERALO_NUEVO
ALLOWED_ORIGINS=https://${{RAILWAY_PUBLIC_DOMAIN}}
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=
SMTP_PASSWORD=
```

### 5.3 Generar JWT_SECRET seguro

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256}))
```

**Git Bash o WSL:**
```bash
openssl rand -base64 64
```

**Online (menos seguro):**
https://www.browserling.com/tools/random-base64

Copia el resultado y úsalo como `JWT_SECRET`.

### 5.4 Guardar

Click **"Save"** o simplemente sal de la pantalla (Railway guarda automáticamente).

**Railway redesplegará automáticamente** tu aplicación con las nuevas variables.

---

## 🚢 Paso 6: Deploy Automático

### 6.1 Monitorear el deploy

1. Ve a la pestaña **"Deployments"** de tu servicio backend
2. Verás el progreso en tiempo real:
   - 📦 Building image
   - 🔨 Running build
   - 🚀 Deploying
   - ✅ Active

### 6.2 Tiempo estimado

- **Primera vez:** 5-8 minutos
- **Deploys posteriores:** 2-4 minutos (usa cache)

### 6.3 Ver logs en tiempo real

Click en el deployment activo para ver logs:
```
Building...
[+] Building 45.2s
Successfully built image
Starting deployment...
Deployment successful!
```

### 6.4 Obtener URL pública

1. Ve a la pestaña **"Settings"**
2. En **"Domains"** verás tu URL:
   ```
   https://tu-proyecto-production.up.railway.app
   ```
3. Copia esta URL, la necesitarás

**Railway proporciona HTTPS automáticamente** 🔒

---

## 💾 Paso 7: Importar Base de Datos

### 7.1 Conectarse a MySQL de Railway

**Opción A: Desde Railway Dashboard**

1. Click en tu servicio **MySQL**
2. Ve a **"Data"**
3. Click **"Connect"** → Abre una consola web

**Opción B: Desde tu computadora**

1. Click en MySQL service
2. Ve a **"Variables"**
3. Copia las credenciales:
   ```
   Host: containers-us-west-XXX.railway.app
   Port: 6379
   Database: railway
   Username: root
   Password: XXXXXXXX
   ```

4. Conectar con MySQL Workbench o terminal:

```cmd
mysql -h containers-us-west-XXX.railway.app -P 6379 -u root -p
```

### 7.2 Crear base de datos y tablas

```sql
-- Crear base de datos
CREATE DATABASE IF NOT EXISTS mesa_partes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar base de datos
USE mesa_partes_db;

-- Importar tu SQL
SOURCE C:/Users/MARCELA/Desktop/SoftwareCore/ProyectoMesaDePartes/SQL/mesa_partes_db_completa_actualizada.sql;
```

**O desde PowerShell:**

```cmd
mysql -h HOST -P PORT -u root -p railway < SQL\mesa_partes_db_completa_actualizada.sql
```

### 7.3 Verificar importación

```sql
USE mesa_partes_db;
SHOW TABLES;
SELECT COUNT(*) FROM usuarios;
```

---

## ✅ Paso 8: Verificar Funcionamiento

### 8.1 Health Check

Abre tu navegador y ve a:
```
https://tu-proyecto-production.up.railway.app/actuator/health
```

Deberías ver:
```json
{
  "status": "UP"
}
```

### 8.2 Probar endpoint de login

**Con cURL:**
```cmd
curl -X POST https://tu-proyecto-production.up.railway.app/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

**Con navegador/Postman:**
```
POST https://tu-proyecto-production.up.railway.app/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Deberías recibir un token JWT.

### 8.3 Ver logs de la aplicación

En Railway Dashboard:
1. Click en tu servicio backend
2. Tab **"Deployments"**
3. Click en el deployment activo
4. Verás logs en tiempo real

---

## 🌐 Paso 9: Configurar Frontend

### 9.1 Actualizar config.js

Edita `frontend/assets/js/core/config.js`:

```javascript
const APP_CONFIG = {
    API_BASE_URL: 'https://tu-proyecto-production.up.railway.app',
    
    API_ENDPOINTS: {
        AUTH: {
            LOGIN: '/api/auth/login',
            REGISTRO: '/api/auth/registro',
            LOGOUT: '/api/auth/logout'
        },
        // ... resto de endpoints
    },
    // ... resto de configuración
};
```

### 9.2 Desplegar frontend (3 opciones)

#### Opción A: Netlify (Recomendado)

1. Ve a: **https://app.netlify.com**
2. **"Add new site"** → **"Import from Git"**
3. Conecta tu repositorio
4. Configuración:
   - **Base directory:** `frontend`
   - **Build command:** (dejar vacío)
   - **Publish directory:** `.` o `/`
5. Deploy

Tu frontend estará en: `https://tu-app.netlify.app`

#### Opción B: Vercel

1. Ve a: **https://vercel.com**
2. **"New Project"** → Conectar GitHub
3. Configuración:
   - **Root Directory:** `frontend`
4. Deploy

#### Opción C: GitHub Pages

1. Ve a Settings de tu repo → Pages
2. Source: Deploy from branch `main`
3. Folder: `/frontend`
4. Save

**⚠️ Actualizar CORS:**

Después de desplegar el frontend, actualiza la variable `ALLOWED_ORIGINS` en Railway:

```env
ALLOWED_ORIGINS=https://tu-app.netlify.app,https://tu-proyecto-production.up.railway.app
```

---

## 🔧 Troubleshooting

### Problema 1: "Application failed to respond"

**Causa:** La app no inicia correctamente

**Solución:**
1. Ver logs en Railway
2. Verificar que las variables de entorno estén correctas
3. Verificar que MySQL esté corriendo
4. Revisar que `DB_HOST` apunte a `${{MySQL.MYSQL_HOST}}`

### Problema 2: "Connection refused" a MySQL

**Causa:** Variables de conexión incorrectas

**Solución:**
```env
# Asegúrate de usar las referencias correctas
DB_HOST=${{MySQL.MYSQL_HOST}}
DB_PORT=${{MySQL.MYSQL_PORT}}
DB_NAME=${{MySQL.MYSQL_DATABASE}}
DB_USERNAME=${{MySQL.MYSQL_USER}}
DB_PASSWORD=${{MySQL.MYSQL_PASSWORD}}
```

### Problema 3: CORS Error en frontend

**Causa:** `ALLOWED_ORIGINS` no incluye tu dominio de frontend

**Solución:**
```env
ALLOWED_ORIGINS=https://tu-frontend.netlify.app,https://tu-backend.railway.app
```

Redeploy después de cambiar.

### Problema 4: "Out of credits"

**Causa:** Superaste las 500 horas del plan gratis

**Solución:**
- Railway resetea las horas cada mes
- Considera agregar método de pago (solo te cobran lo que uses)
- O espera al próximo mes

### Problema 5: Build fails

**Causa:** Error en Maven build

**Solución:**
1. Ver logs completos del build
2. Verificar que `pom.xml` esté correcto
3. Verificar que Java 21 esté especificado
4. Intenta rebuild: Settings → Redeploy

---

## 📊 Monitoreo y Logs

### Ver logs en tiempo real

**Método 1: Railway Dashboard**
1. Click en tu servicio
2. Tab "Deployments"
3. Click en deployment activo
4. Logs aparecen automáticamente

**Método 2: Railway CLI**

Instalar CLI:
```cmd
npm install -g @railway/cli
```

Login:
```cmd
railway login
```

Ver logs:
```cmd
railway logs
```

### Métricas disponibles

En Railway Dashboard → Metrics:
- 📈 CPU Usage
- 💾 Memory Usage
- 🌐 Network I/O
- ⏱️ Response Time

### Alarmas y notificaciones

Railway te notifica automáticamente por email si:
- ❌ Deploy falla
- ⚠️ App crashea
- 💰 Te acercas al límite de créditos

---

## 🎉 ¡Listo!

### Checklist Final

- [x] Proyecto en GitHub
- [x] Railway proyecto creado
- [x] MySQL agregado y configurado
- [x] Variables de entorno configuradas
- [x] Deploy exitoso
- [x] Base de datos importada
- [x] Health check funciona
- [x] API responde
- [x] Frontend actualizado y desplegado
- [x] CORS configurado correctamente

### URLs importantes

- **Backend:** `https://tu-proyecto-production.up.railway.app`
- **API Health:** `https://tu-proyecto-production.up.railway.app/actuator/health`
- **Frontend:** `https://tu-app.netlify.app` (o tu elección)
- **Railway Dashboard:** `https://railway.app/project/TU_PROJECT_ID`

### Comandos útiles

```cmd
# Ver logs
railway logs

# Conectar a MySQL
railway connect mysql

# Abrir en navegador
railway open
```

---

## 💰 Administrar Uso y Costos

### Ver uso actual

1. Railway Dashboard
2. Tab "Usage"
3. Verás:
   - Horas consumidas
   - Créditos usados
   - Proyección mensual

### Plan Gratis incluye:

- ✅ 500 horas de ejecución/mes
- ✅ $5 de crédito/mes
- ✅ 1GB RAM
- ✅ 1GB disco
- ✅ Builds ilimitados

**Tip:** Con 500 horas puedes tener tu app corriendo ~20 días completos al mes.

### Optimizar uso

Para que dure todo el mes:
- La app entra en "sleep" automáticamente cuando no hay tráfico
- Solo consume horas cuando está activa
- Ideal para desarrollo/demo

---

## 📚 Recursos Adicionales

- 📖 **Railway Docs:** https://docs.railway.app
- 💬 **Railway Discord:** https://discord.gg/railway
- 🎓 **Railway Blog:** https://blog.railway.app
- 🐛 **Report Issues:** https://github.com/railwayapp/railway/issues

---

## 🆘 Soporte

**¿Problemas?**

1. Revisa los logs en Railway
2. Verifica las variables de entorno
3. Consulta esta guía nuevamente
4. Busca en Railway Discord
5. O pregúntame directamente 😊

---

**¡Tu aplicación Mesa de Partes está en la nube con Railway!** 🚂🎉

**Tiempo total:** ~15 minutos
**Costo:** $0 (plan gratis)
**Resultado:** Aplicación en producción con HTTPS
