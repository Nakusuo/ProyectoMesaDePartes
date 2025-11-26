## 🔧 CONFIGURACIÓN MANUAL EN RAILWAY - Opción 1

Sigue estos pasos EXACTAMENTE en Railway Dashboard:

---

### PASO 1: Ir a Settings del Servicio

1. En Railway Dashboard, click en tu **servicio backend** (el que está fallando)
2. Click en la pestaña **"Settings"** (arriba)

---

### PASO 2: Configurar el Builder

Scroll hacia abajo hasta encontrar la sección **"Build"**

En esta sección verás:

**Builder:**
- Cambia de "Nixpacks" a **"Dockerfile"**
- Click en el dropdown y selecciona **"Dockerfile"**

**Dockerfile Path:**
- Asegúrate que diga: `Dockerfile`
- Si está vacío, escribe: `Dockerfile`

**Root Directory:**
- Déjalo vacío o asegúrate que diga `/` o `./`

---

### PASO 3: Configurar el Deploy

Scroll un poco más abajo hasta la sección **"Deploy"**

**Custom Start Command:**
- Escribe: `java -jar app.jar`

**Health Check Path:**
- Escribe: `/actuator/health`

**Health Check Timeout:**
- Déjalo en 300 segundos (5 minutos)

**Restart Policy:**
- Selecciona: **"On Failure"**

---

### PASO 4: Guardar y Redeploy

1. Los cambios se guardan automáticamente
2. Ve arriba a la derecha
3. Click en los **tres puntos (•••)** o busca el botón **"Redeploy"**
4. Confirma el redeploy

---

### PASO 5: Monitorear el Deploy

1. Ve a la pestaña **"Deployments"**
2. Verás el nuevo deployment en progreso
3. Click en él para ver los logs en tiempo real

**Deberías ver:**
```
Building with Dockerfile...
Step 1/15: FROM maven:3.9-eclipse-temurin-21-alpine AS build
Step 2/15: WORKDIR /build
...
Successfully built
Starting deployment...
Application started on port 8080
```

---

### ⏱️ Tiempo Estimado:
- Configuración: 2 minutos
- Build: 5-8 minutos
- Deploy: 1-2 minutos

**Total: ~10 minutos**

---

### ✅ Si Todo Sale Bien:

Verás en logs:
```
Started MesadepartesApplication in X seconds
Application is ready
```

Y el status cambiará a: **🟢 Active**

---

### ❌ Si Sigue Fallando:

Dime exactamente qué mensaje de error ves en los logs y te ayudo a solucionarlo.

---

**¿Ya encontraste la opción de cambiar a "Dockerfile" en Settings?**
