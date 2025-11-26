# Guía de Deploy a Railway

## ✅ Requisitos Previos

1. **Cuenta de Railway** - https://railway.app (crear cuenta gratis)
2. **Git instalado** en tu máquina
3. **Railway CLI instalado** (opcional pero recomendado)

---

## 📋 Pasos para Desplegar

### 1. **Preparar el Repositorio Git**

```bash
# Asegurate de estar en el directorio raíz del proyecto
cd c:\Users\LENOVO\Desktop\ProyectoMesaDePartes

# Inicializar Git (si no está ya inicializado)
git init
git add .
git commit -m "Preparar para Railway"
git remote add origin https://github.com/Nakusuo/ProyectoMesaDePartes.git
git branch -M main
git push -u origin main
```

### 2. **Conectar Proyecto a Railway desde Web**

1. Ir a https://railway.app/dashboard
2. Click en **New Project**
3. Seleccionar **Deploy from GitHub**
4. Autorizar Railway para acceder a tu GitHub
5. Seleccionar el repositorio `ProyectoMesaDePartes`
6. Click en **Deploy**

---

## 🗄️ Configurar Base de Datos MySQL

### Opción A: Usar MySQL de Railway (Recomendado)

1. En el dashboard de Railway, dentro del proyecto
2. Click en **+ New Service**
3. Seleccionar **MySQL**
4. Railway generará automáticamente las variables de entorno

### Opción B: Usar Base de Datos Externa

Si tienes una instancia MySQL externa:

1. En Railway, ir a **Variables**
2. Agregar las siguientes variables:

```
MYSQL_HOST=tu-host-mysql.com
MYSQL_PORT=3306
MYSQL_DATABASE=mesa_partes_db
MYSQL_USER=tu-usuario
MYSQL_PASSWORD=tu-contraseña
```

---

## 🔐 Variables de Entorno Obligatorias

En el dashboard de Railway, agregar estas variables:

```
# Base de Datos (si usas servicio MySQL de Railway, se agregan automáticamente)
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=mesa_partes_db
MYSQL_USER=root
MYSQL_PASSWORD=tu-contraseña-segura

# JWT Secret (CAMBIAR A UN VALOR SEGURO EN PRODUCCIÓN)
JWT_SECRET=tu-secret-key-segura-muy-larga-y-aleatoria

# Correo Electrónico (opcional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=tu-email@gmail.com
SMTP_PASSWORD=tu-app-password

# Otras
PORT=8080
```

---

## 🚀 Desplegar

### Automático (Recomendado)
- Cada push a `main` se despliega automáticamente
- Railway monitorea el repositorio GitHub

### Manual con Railway CLI

```bash
# Instalar Railway CLI
npm install -g @railway/cli

# Login
railway login

# Desplegar
railway up

# Ver logs
railway logs
```

---

## 🔍 Verificar el Despliegue

1. En Railway dashboard, verás la URL del proyecto (ej: `https://xxxx.railway.app`)
2. Acceder a `https://xxxx.railway.app/pages/auth/login.html`
3. Ver logs en tiempo real desde Railway

### Health Check

```bash
curl https://xxxx.railway.app/actuator/health
```

Debe retornar:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## ❌ Solución de Problemas

### Error: "Base de datos no conecta"

1. Verificar que MySQL de Railway esté corriendo
2. Revisar las variables `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PASSWORD`
3. En Railway logs, buscar:
   ```
   Cannot get a connection, pool error
   ```

**Solución:**
```
- Recrear el servicio MySQL en Railway
- O usar una instancia MySQL externa confiable
```

### Error: "CORS error" en el frontend

1. Asegurar que `mesadepartes.app.allowedOrigins=*` en `application-railway.properties`
2. Verificar la URL del frontend en el navegador
3. Agregar en variables de Railway:
   ```
   ALLOWED_ORIGINS=*
   ```

### Error: "JVM Memory"

1. El contenedor sin suficiente RAM
2. En `Dockerfile`, modificar:
   ```dockerfile
   ENV JAVA_OPTS="-Xms128m -Xmx256m"
   ```

### Aplicación tarda mucho en iniciar

1. Aumentar el `start-period` en el healthcheck del Dockerfile
2. Verificar logs: `railway logs -f`

---

## 📊 Monitoreo Continuo

Railway proporciona:
- **Logs en tiempo real**
- **Métricas de CPU y memoria**
- **Estado del despliegue**

Acceder desde: Dashboard → Proyecto → Logs

---

## 💡 Tips Importantes

1. **Nunca** dejes contraseñas en el código
2. Usa `application-railway.properties` para configs de producción
3. El perfil `railway` se activa automáticamente en Railway
4. Las variables de entorno se leen desde Railway, no desde `.env`
5. El frontend debe estar en una URL diferente a la del backend o usar CORS

---

## 🔄 Actualizar la Aplicación

```bash
# Hacer cambios locales
# ...

# Hacer commit y push
git add .
git commit -m "Actualizar funcionalidad X"
git push origin main

# Railway se despliega automáticamente
```

---

## ❓ Preguntas Frecuentes

**¿Railway es gratuito?**
- Sí, los primeros $5 USD por mes son gratis. Luego pagas por lo que uses.

**¿Puedo tener múltiples ambientes?**
- Sí, crea múltiples proyectos en Railway (desarrollo, staging, producción)

**¿Cómo respaldo mi base de datos?**
- Railway proporciona backups automáticos. También puedes exportar manualmente desde phpmyadmin.

---

**Última actualización:** 26/11/2025
