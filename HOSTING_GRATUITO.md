# 🆓 Opciones de Hosting GRATUITO - Mesa de Partes PNP

## 🎯 Mejores Opciones Gratuitas para tu Proyecto

### ✅ Opción 1: Railway.app (RECOMENDADO)

**🌟 Lo mejor para Spring Boot + MySQL**

**Características:**
- ✅ 500 horas gratis/mes
- ✅ MySQL incluido gratis
- ✅ Deploy automático desde GitHub
- ✅ SSL/HTTPS gratis
- ✅ Muy fácil de configurar

**Cómo usarlo:**

1. **Crear cuenta:** https://railway.app (con GitHub)

2. **Crear proyecto nuevo:**
   - Click en "New Project"
   - Seleccionar "Deploy from GitHub repo"
   - Conectar tu repositorio

3. **Agregar MySQL:**
   - En tu proyecto, click "+ New"
   - Seleccionar "Database" → "MySQL"
   - Railway creará automáticamente la base de datos

4. **Configurar variables de entorno:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_HOST=${{MySQL.MYSQL_URL}}
   DB_NAME=mesa_partes_db
   DB_USERNAME=${{MySQL.MYSQL_USER}}
   DB_PASSWORD=${{MySQL.MYSQL_PASSWORD}}
   JWT_SECRET=tu-secret-seguro
   ALLOWED_ORIGINS=https://tu-app.up.railway.app
   ```

5. **Desplegar:**
   - Railway detecta automáticamente Spring Boot
   - Hace el build y deploy solo

**Límites gratuitos:**
- 500 horas/mes (suficiente para desarrollo)
- 1GB RAM
- 1GB disco

---

### ✅ Opción 2: Render.com

**🌟 Muy bueno, con base de datos PostgreSQL gratis**

**Características:**
- ✅ Completamente gratis
- ✅ PostgreSQL gratis (necesitarías cambiar de MySQL)
- ✅ SSL automático
- ✅ Deploy desde GitHub
- ✅ Sin límite de tiempo

**Cómo usarlo:**

1. **Crear cuenta:** https://render.com

2. **Crear Web Service:**
   - "New" → "Web Service"
   - Conectar tu repo de GitHub
   - Runtime: Docker
   - Plan: Free

3. **Crear base de datos:**
   - "New" → "PostgreSQL"
   - Plan: Free

4. **Variables de entorno:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_HOST=tu-postgres.render.com
   DB_NAME=mesa_partes_db
   DB_USERNAME=usuario
   DB_PASSWORD=password
   JWT_SECRET=tu-secret
   ```

**⚠️ Nota:** Necesitarías cambiar de MySQL a PostgreSQL (cambio menor en dependencias)

**Límites gratuitos:**
- App se duerme después de 15 min de inactividad
- 750 horas/mes
- PostgreSQL: 90 días de retención

---

### ✅ Opción 3: Fly.io

**🌟 Muy técnico pero poderoso**

**Características:**
- ✅ 3 VMs pequeñas gratis
- ✅ 160GB transferencia/mes
- ✅ SSL gratis
- ✅ Deploy con Docker

**Cómo usarlo:**

1. **Instalar CLI:**
   ```cmd
   powershell -Command "iwr https://fly.io/install.ps1 -useb | iex"
   ```

2. **Login:**
   ```cmd
   fly auth login
   ```

3. **Inicializar app:**
   ```cmd
   cd backend
   fly launch
   ```

4. **Agregar MySQL:**
   - Necesitas usar PlanetScale (gratis) o MySQL externo

5. **Deploy:**
   ```cmd
   fly deploy
   ```

**Límites gratuitos:**
- 3 máquinas compartidas
- 256MB RAM por máquina
- 3GB disco

---

### ✅ Opción 4: Heroku (Con limitaciones)

**⚠️ Ya NO es totalmente gratis, pero tiene opciones económicas**

- Planes desde $5/mes
- MySQL requiere addon ClearDB ($10/mes adicional)
- **No recomendado** para presupuesto $0

---

### ✅ Opción 5: Oracle Cloud Free Tier

**🌟 Muy generoso pero más complejo**

**Características:**
- ✅ SIEMPRE gratis (no expira)
- ✅ 2 VMs ARM (4 cores, 24GB RAM)
- ✅ 200GB almacenamiento
- ✅ MySQL incluido

**Cómo usarlo:**

1. **Crear cuenta:** https://cloud.oracle.com

2. **Crear VM:**
   - Compute → Instances → Create Instance
   - Shape: Ampere A1 (ARM, gratis)
   - OS: Ubuntu 22.04

3. **Instalar Docker:**
   ```bash
   sudo apt update
   sudo apt install docker.io docker-compose -y
   ```

4. **Deploy manual similar a EC2**

**⚠️ Ventajas:**
- Totalmente gratis PARA SIEMPRE
- Muy generoso en recursos

**⚠️ Desventajas:**
- Configuración manual compleja
- Necesitas conocimientos de servidores

---

### ✅ Opción 6: PlanetScale (Solo Base de Datos)

**🌟 MySQL gratis y compatible**

Si decides hostear el backend en Railway/Render y quieres MySQL gratis:

1. **Crear cuenta:** https://planetscale.com

2. **Crear base de datos:**
   - Free tier: 5GB, 1 billón de lecturas/mes

3. **Conectar desde tu app:**
   ```
   DB_HOST=aws.connect.psdb.cloud
   DB_NAME=mesa_partes_db
   DB_USERNAME=tu-usuario
   DB_PASSWORD=tu-password
   ```

---

## 📊 Comparación Rápida

| Opción | Backend | Base de Datos | Dificultad | Límite de Tiempo |
|--------|---------|---------------|------------|------------------|
| **Railway** ⭐ | ✅ Gratis | ✅ MySQL gratis | 😊 Fácil | 500h/mes |
| **Render** | ✅ Gratis | ⚠️ PostgreSQL | 😊 Fácil | Sin límite* |
| **Fly.io** | ✅ Gratis | ❌ Externo | 🤔 Medio | Sin límite |
| **Oracle Cloud** | ✅ Gratis | ✅ MySQL gratis | 😰 Difícil | ♾️ Siempre |
| **PlanetScale** | ❌ Solo DB | ✅ MySQL gratis | 😊 Fácil | Sin límite |

*Render: La app se duerme tras 15 min de inactividad

---

## 🎯 MI RECOMENDACIÓN FINAL

### Para empezar RÁPIDO y FÁCIL:

**🥇 Railway.app**

**Por qué:**
- ✅ Soporta MySQL nativamente (no hay que cambiar nada)
- ✅ Deploy en 5 minutos
- ✅ Interfaz súper intuitiva
- ✅ Variables de entorno automáticas
- ✅ SSL incluido
- ✅ 500 horas = más de 20 días completos al mes

**Suficiente para:**
- Desarrollo y testing
- Demo del proyecto
- Uso diario moderado

---

## 🚀 GUÍA RÁPIDA: Railway (PASO A PASO)

### Paso 1: Preparar tu proyecto

1. **Subir a GitHub:**
   ```cmd
   git add .
   git commit -m "Preparar para Railway"
   git push origin main
   ```

2. **Crear Dockerfile** (si no existe):
   ```dockerfile
   FROM eclipse-temurin:21-jdk-alpine AS build
   WORKDIR /app
   COPY backend/pom.xml .
   COPY backend/src ./src
   COPY backend/mvnw .
   COPY backend/.mvn .mvn
   RUN ./mvnw clean package -DskipTests
   
   FROM eclipse-temurin:21-jre-alpine
   WORKDIR /app
   COPY --from=build /app/target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

### Paso 2: Deploy en Railway

1. **Ir a:** https://railway.app
2. **Login con GitHub**
3. **New Project** → **Deploy from GitHub repo**
4. **Seleccionar tu repo**
5. **Add Service** → **Database** → **MySQL**
6. **Configurar variables:**
   - Click en tu servicio backend
   - Tab "Variables"
   - Agregar:
     ```
     SPRING_PROFILES_ACTIVE=prod
     ALLOWED_ORIGINS=https://${{RAILWAY_PUBLIC_DOMAIN}}
     JWT_SECRET=tu-secret-muy-seguro
     ```
   - Railway auto-conecta MySQL con variables

7. **Deploy:**
   - Railway hace build automático
   - ¡Listo en 5 minutos!

### Paso 3: Importar Base de Datos

1. **Conectarse a MySQL de Railway:**
   ```cmd
   mysql -h railway.app -u root -p -P PORT DATABASE
   ```

2. **Importar SQL:**
   ```sql
   SOURCE SQL/mesa_partes_db_completa_actualizada.sql;
   ```

### Paso 4: Configurar Frontend

1. **Actualizar `config.js`:**
   ```javascript
   API_BASE_URL: 'https://tu-app.up.railway.app'
   ```

2. **Hostear frontend:**
   - **Netlify** (gratis): https://netlify.com
   - **Vercel** (gratis): https://vercel.com
   - **GitHub Pages** (gratis): https://pages.github.com

---

## 💡 Tips para Maximizar el Plan Gratuito

### Railway (500 horas/mes):

**🎯 Estrategia:** El contador se detiene cuando la app está en sleep mode

**Cómo hacer que dure más:**
1. La app entra en sleep si no hay tráfico
2. 500 horas = 20+ días de uso activo
3. Si solo usas 8 horas/día = todo el mes gratis

**Tip:** Configura Railway para hacer sleep automático

### Render (siempre gratis pero con sleep):

**🎯 Estrategia:** Mantener despierta la app

**Usar cron job gratuito:**
1. **Cron-job.org** (gratis)
2. Ping cada 14 minutos: `https://tu-app.onrender.com/actuator/health`
3. La app nunca duerme

**Pero:** Considera el impacto ambiental de mantener apps despiertas innecesariamente

---

## 🌐 Hosting para el Frontend (Gratis)

### Opción 1: Netlify ⭐
- Deploy automático desde Git
- SSL gratis
- 100GB ancho de banda/mes
- **URL:** https://app.netlify.com

### Opción 2: Vercel
- Similar a Netlify
- 100GB ancho de banda
- **URL:** https://vercel.com

### Opción 3: GitHub Pages
- Gratis ilimitado
- Solo sitios estáticos
- **URL:** https://pages.github.com

### Opción 4: Cloudflare Pages
- Ilimitado gratis
- CDN global
- **URL:** https://pages.cloudflare.com

---

## ⚠️ Limitaciones a Considerar

### Railway:
- ❌ Después de 500 horas → app se detiene hasta siguiente mes
- ❌ No apto para producción 24/7
- ✅ Perfecto para desarrollo/demo

### Render:
- ❌ App duerme tras 15 min → primera carga lenta (30 seg)
- ❌ PostgreSQL expire datos después de 90 días (plan free)
- ✅ Bueno para demos

### Oracle Cloud:
- ❌ Configuración compleja
- ❌ Requiere conocimientos de DevOps
- ✅ Mejor si sabes lo que haces

---

## 📝 Archivo de Configuración para Railway

Crear `railway.json` en la raíz:

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "backend/Dockerfile"
  },
  "deploy": {
    "startCommand": "java -jar app.jar",
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 300,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

---

## 🎓 Resumen Ejecutivo

### Para TU caso (Mesa de Partes PNP):

**🥇 Primera opción:** Railway.app
- Razón: MySQL nativo, fácil, 500h gratis
- Tiempo setup: 10 minutos
- Ideal para: Desarrollo, testing, demo

**🥈 Segunda opción:** Render.com + PlanetScale
- Razón: Siempre online, pero requiere cambio a PostgreSQL o MySQL externo
- Tiempo setup: 20 minutos
- Ideal para: Demo permanente

**🥉 Tercera opción:** Oracle Cloud Free
- Razón: Gratis para siempre, sin límites
- Tiempo setup: 2 horas
- Ideal para: Producción real (si tienes paciencia)

---

## 🚦 Próximos Pasos INMEDIATOS

### ¿Quieres empezar YA?

**Opción más rápida (10 minutos):**

1. ✅ Crear cuenta en Railway: https://railway.app
2. ✅ Conectar tu repositorio de GitHub
3. ✅ Agregar servicio MySQL
4. ✅ Configurar 3 variables de entorno
5. ✅ Deploy automático
6. ✅ ¡Funciona!

**¿Necesitas ayuda?** Dime qué opción prefieres y te guío paso a paso.

---

## 📞 Links Directos

- 🚂 **Railway:** https://railway.app
- 🎨 **Render:** https://render.com
- ✈️ **Fly.io:** https://fly.io
- ☁️ **Oracle Cloud:** https://cloud.oracle.com
- 🪐 **PlanetScale:** https://planetscale.com
- 🌐 **Netlify (frontend):** https://netlify.com
- ⚡ **Vercel (frontend):** https://vercel.com

---

**¡Listo para deployar gratis!** 🎉

¿Necesitas ayuda con Railway o alguna otra opción? ¡Pregúntame!
