# 🚨 SOLUCIÓN: Error de Conexión MySQL en Railway

## ❌ Error Actual
```
Connection refused
Communications link failure
Unable to open JDBC Connection for DDL execution
```

**Traducción**: Tu aplicación Spring Boot en Railway **NO puede conectarse a la base de datos MySQL**.

---

## 🔍 DIAGNÓSTICO

El error `Connection refused` en Railway indica uno de estos problemas:

### 1️⃣ **El servicio MySQL no está creado en Railway**
- ❌ No existe un servicio MySQL en tu proyecto
- ❌ El servicio MySQL está detenido o falló

### 2️⃣ **Las variables de entorno no están vinculadas**
- ❌ Las variables `MYSQLHOST`, `MYSQLPORT`, etc. no están definidas
- ❌ El backend no está conectado al servicio MySQL

### 3️⃣ **El servicio MySQL está iniciándose**
- ⏳ MySQL puede tardar 2-3 minutos en estar listo
- ⏳ El backend intentó conectarse antes de que MySQL estuviera disponible

---

## ✅ SOLUCIÓN PASO A PASO

### **PASO 1: Verificar que MySQL existe en Railway**

1. Ve a tu proyecto en Railway: https://railway.app/dashboard
2. Deberías ver **DOS servicios**:
   - 🟦 **Backend** (tu aplicación Spring Boot)
   - 🟩 **MySQL** (base de datos)

**Si NO ves el servicio MySQL:**

1. Click en **"+ New"**
2. Selecciona **"Database"**
3. Click en **"Add MySQL"**
4. Espera 1-2 minutos a que se aprovisione

---

### **PASO 2: Verificar Variables de MySQL**

1. Click en el servicio **MySQL** (el verde/azul con ícono de base de datos)
2. Ve a la pestaña **"Variables"**
3. Deberías ver estas variables **generadas automáticamente**:
   ```
   MYSQLHOST=xxxx.railway.internal
   MYSQLPORT=3306
   MYSQLDATABASE=railway
   MYSQLUSER=root
   MYSQLPASSWORD=xxxxxxxxxx
   ```

Si estas variables **NO existen**, significa que MySQL no se creó correctamente. **Elimínalo y créalo de nuevo**.

---

### **PASO 3: Vincular MySQL con el Backend**

1. Click en el servicio **Backend** (tu aplicación Spring Boot)
2. Ve a la pestaña **"Variables"**
3. Verifica que existan estas **REFERENCIAS**:
   ```
   MYSQLHOST=${{MySQL.MYSQLHOST}}
   MYSQLPORT=${{MySQL.MYSQLPORT}}
   MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
   MYSQLUSER=${{MySQL.MYSQLUSER}}
   MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
   ```

**Si NO existen**, agrégalas manualmente:

#### Opción A: Agregar Referencias Automáticas (Recomendado)
1. Click en **"+ New Variable"**
2. En el campo de la derecha, click en el ícono **"Reference"** (parece $)
3. Selecciona **"MySQL"** → **"MYSQLHOST"**
4. Repite para cada variable

#### Opción B: Copiar los valores directamente
1. Copia el valor exacto desde las variables del servicio MySQL
2. Pégalos en las variables del Backend
3. ⚠️ **Menos recomendado** porque si MySQL cambia, tendrás que actualizar manualmente

---

### **PASO 4: Verificar el Perfil Spring activo**

Tu archivo `application-railway.properties` debe estar configurado correctamente:

```properties
# Configuración de Base de Datos Railway MySQL
spring.datasource.url=jdbc:mysql://${MYSQLHOST:localhost}:${MYSQLPORT:3306}/${MYSQLDATABASE:railway}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=${MYSQLUSER:root}
spring.datasource.password=${MYSQLPASSWORD:}
```

El `Dockerfile` debe activar el perfil railway:

```dockerfile
ENV SPRING_PROFILES_ACTIVE=railway
```

---

### **PASO 5: Forzar Redeploy**

Después de configurar las variables:

1. En Railway, ve al servicio **Backend**
2. Ve a **"Settings"** (engranaje)
3. Scroll hasta **"Danger Zone"**
4. Click en **"Restart Deployment"**

O simplemente haz un commit en GitHub:
```bash
git commit --allow-empty -m "Force redeploy Railway"
git push origin main
```

Railway detectará el cambio y hará redeploy automáticamente.

---

### **PASO 6: Monitorear los Logs**

1. En Railway, click en tu servicio **Backend**
2. Ve a **"Deployments"**
3. Click en el deployment en progreso
4. Click en **"View Logs"**

**Busca estas líneas en los logs:**

✅ **EXITOSO:**
```
Started MesadepartesApplication in X seconds
Tomcat started on port 8080
HikariPool-1 - Start completed
```

❌ **ERROR:**
```
Connection refused
Communications link failure
Unable to connect to database
```

---

## 🔧 CONFIGURACIÓN COMPLETA ESPERADA

### Variables en el servicio **MySQL**:
```env
MYSQLHOST=xxxxx.railway.internal
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=xxxxxxxxxxxxxx
MYSQL_URL=mysql://root:xxxxx@xxxxx.railway.internal:3306/railway
```

### Variables en el servicio **Backend**:
```env
# Referencias a MySQL
MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}

# JWT (configura manualmente)
JWT_SECRET=TuClaveSecretaSuperSegura64CaracteresOMas

# Puerto (Railway lo asigna automáticamente)
PORT=8080

# Perfil Spring (opcional, ya está en Dockerfile)
SPRING_PROFILES_ACTIVE=railway
```

---

## ⏱️ TIEMPOS ESPERADOS

- **MySQL**: 1-2 minutos para aprovisionar
- **Backend Build**: 3-5 minutos (compilar el JAR con Maven)
- **Backend Deploy**: 30 segundos - 1 minuto
- **Total**: ~5-8 minutos en el primer deploy

---

## 🎯 CHECKLIST RÁPIDO

Verifica cada punto:

- [ ] El servicio **MySQL** existe en Railway
- [ ] Las variables `MYSQL*` están generadas en el servicio MySQL
- [ ] Las variables están **vinculadas** en el Backend (`${{MySQL.MYSQLHOST}}`)
- [ ] El `Dockerfile` tiene `ENV SPRING_PROFILES_ACTIVE=railway`
- [ ] El archivo `application-railway.properties` existe en `src/main/resources/`
- [ ] Has hecho redeploy después de configurar las variables
- [ ] Los logs muestran "Started MesadepartesApplication"

---

## 🆘 SI SIGUE FALLANDO

### Opción 1: Recrear el servicio MySQL
1. Elimina el servicio MySQL actual
2. Crea uno nuevo
3. Re-vincula las variables

### Opción 2: Usar una URL de conexión directa
En lugar de usar variables separadas, usa la URL completa:

1. En el servicio MySQL, copia el valor de `MYSQL_URL`
2. En el Backend, agrega:
   ```env
   SPRING_DATASOURCE_URL=${{MySQL.MYSQL_URL}}
   ```
3. Modifica `application-railway.properties`:
   ```properties
   spring.datasource.url=${SPRING_DATASOURCE_URL}
   ```

### Opción 3: Verificar el orden de inicio
Railway puede iniciar tu backend antes que MySQL. Agrega un health check con retry:

En `railway.json`:
```json
{
  "deploy": {
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 600,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 5
  }
}
```

---

## 📞 SOPORTE ADICIONAL

Si después de seguir todos los pasos el error persiste:

1. **Comparte los logs de Railway** (censura las contraseñas)
2. **Screenshot de las variables** del Backend y MySQL
3. **Confirma que ambos servicios están en el mismo proyecto**

---

## ✅ VERIFICACIÓN FINAL

Una vez que el deploy sea exitoso, prueba:

```bash
# Obtén la URL de tu proyecto en Railway
# Ejemplo: https://tu-proyecto.up.railway.app

# Test básico
curl https://tu-proyecto.up.railway.app/

# Test health
curl https://tu-proyecto.up.railway.app/actuator/health

# Debería responder:
# {"status":"UP"}
```

🎉 **¡Listo!** Tu aplicación debería estar conectada a MySQL correctamente.
