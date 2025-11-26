# 🔴 SOLUCIÓN: MySQL Connection Refused

## ❌ **ERROR CONFIRMADO**

```
Caused by: java.net.ConnectException: Connection refused
Communications link failure
```

**Traducción**: Spring Boot NO puede conectar a MySQL porque:
1. El servicio MySQL NO existe en Railway, O
2. Las variables de entorno NO están configuradas

---

## ✅ **SOLUCIÓN PASO A PASO (5 MINUTOS)**

### **PASO 1: Agregar Servicio MySQL en Railway**

1. Ve a tu proyecto en Railway Dashboard
2. Click en **"+ New"** (botón superior derecho)
3. Selecciona **"Database"**
4. Click en **"Add MySQL"**
5. **ESPERA 1-2 MINUTOS** hasta que aparezca con estado VERDE

**Resultado esperado**:
```
✅ MySQL service corriendo (ícono verde)
✅ Variables automáticas generadas
```

---

### **PASO 2: Vincular Variables MySQL al Backend**

Railway vincula automáticamente las variables, pero **verifica esto**:

1. Click en tu servicio **Backend** (NO MySQL)
2. Click en pestaña **"Variables"**
3. Busca estas variables y **cópialas EXACTAMENTE ASÍ**:

```bash
MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
```

**⚠️ IMPORTANTE**:
- Usa `${{MySQL.*}}` (M MAYÚSCULA)
- NO uses `${{Mysql.*}}` (minúscula) ❌
- NO uses valores literales como `localhost` ❌

---

### **PASO 3: Agregar JWT_SECRET**

En las mismas Variables del Backend, agrega:

```bash
JWT_SECRET=Q2xhdmVTZWNyZXRvUGFyYU1lc2FEZVBhcnRlc1BOUFF1ZUVzTXV5RGlmaWNpbERlQWRpdmluYXJZRXNhRXNMYUlkZWE=
```

**Resultado esperado en Variables**:
```
JWT_SECRET = Q2xhdmVT... (valor literal)
MYSQLHOST = ${{MySQL.MYSQLHOST}} (referencia)
MYSQLPORT = ${{MySQL.MYSQLPORT}} (referencia)
MYSQLDATABASE = ${{MySQL.MYSQLDATABASE}} (referencia)
MYSQLUSER = ${{MySQL.MYSQLUSER}} (referencia)
MYSQLPASSWORD = ${{MySQL.MYSQLPASSWORD}} (referencia)
PORT = (Railway lo agrega automáticamente)
```

---

### **PASO 4: Esperar Redeploy Automático**

1. Railway detectará los cambios en variables
2. Automáticamente hará **redeploy** (2-3 minutos)
3. Ve a **"Deployments"** → Click en el nuevo deployment → **"Deploy Logs"**

**Busca esta línea en los logs**:
```
Started MesadepartesApplication in X.XXX seconds
```

✅ Si ves eso = **ÉXITO TOTAL**

---

## 🎯 **VERIFICACIÓN RÁPIDA**

### Checklist antes de redeploy:

```
□ Servicio MySQL agregado y VERDE
□ Variables MYSQLHOST, MYSQLPORT, etc. con formato ${{MySQL.*}}
□ Variable JWT_SECRET agregada
□ Backend service tiene las 6-7 variables configuradas
```

---

## 🔍 **CÓMO VERIFICAR QUE FUNCIONÓ**

### En los Deploy Logs verás:

#### ✅ **ÉXITO** (busca estas líneas):
```
HikariPool-1 - Starting...
HikariPool-1 - Added connection
Initialized JPA EntityManagerFactory
Started MesadepartesApplication in 45.123 seconds
Tomcat started on port 8080
```

#### ❌ **FALLO** (si sigues viendo):
```
Connection refused
Communications link failure
```

**Entonces**:
- Verifica que MySQL esté VERDE (corriendo)
- Verifica las variables tienen `${{MySQL.*}}`
- Espera 2 minutos más (MySQL tarda en iniciar)

---

## 📸 **LO QUE DEBERÍAS VER EN RAILWAY**

### Panel de servicios:
```
┌─────────────────┐
│ Backend         │ ← Tu app Spring Boot
│ Status: Running │
└─────────────────┘

┌─────────────────┐
│ MySQL           │ ← Base de datos
│ Status: Running │ ← Debe estar VERDE
└─────────────────┘
```

### Variables del Backend:
```
JWT_SECRET        → Q2xhdmVTZWNy...
MYSQLHOST         → ${{MySQL.MYSQLHOST}}
MYSQLPORT         → ${{MySQL.MYSQLPORT}}
MYSQLDATABASE     → ${{MySQL.MYSQLDATABASE}}
MYSQLUSER         → ${{MySQL.MYSQLUSER}}
MYSQLPASSWORD     → ${{MySQL.MYSQLPASSWORD}}
PORT              → (auto-generado por Railway)
```

---

## ⚠️ **ERRORES COMUNES**

### Error 1: "Variable MySQL not found"
**Causa**: El servicio MySQL NO se llama "MySQL" en Railway

**Solución**: 
1. Ve al servicio de base de datos
2. Click en "Settings"
3. Verifica el nombre (por defecto es "MySQL")
4. Si es diferente, usa ese nombre: `${{NombreReal.MYSQLHOST}}`

### Error 2: Sigue dando "Connection refused" después de 5 minutos
**Causa**: MySQL no está corriendo O variables mal escritas

**Solución**:
1. Click en servicio MySQL → "Logs" → Verifica que no tenga errores
2. Verifica que las variables tengan **doble llave** `${{...}}`
3. Borra y vuelve a crear las variables

### Error 3: "Table doesn't exist"
**Causa**: Base de datos vacía (NORMAL)

**Solución**: Spring Boot creará las tablas automáticamente con:
```properties
spring.jpa.hibernate.ddl-auto=update
```
(Ya está configurado en application-railway.properties ✅)

---

## 🚀 **DESPUÉS DE CONFIGURAR**

1. Guarda las variables → Redeploy automático inicia
2. Espera **3-5 minutos**
3. Ve a Deploy Logs
4. Busca: `Started MesadepartesApplication`
5. Si aparece → **¡ÉXITO!** 🎉

---

## 💡 **¿POR QUÉ FALLÓ?**

**Tu código está perfecto** ✅

El problema era:
- Spring Boot intentó conectar a MySQL
- Pero MySQL no existía en Railway
- O las variables no estaban configuradas
- Por eso dio "Connection refused"

**Una vez agregues MySQL + variables → Todo funcionará**

---

## 📞 **SI SIGUE FALLANDO**

Compárteme:
1. Screenshot de tus servicios en Railway (Backend + MySQL)
2. Screenshot de tus Variables configuradas
3. Últimas 30 líneas de los nuevos Deploy Logs

**Con eso lo soluciono en 1 minuto** 🚀

---

## ✅ **RESUMEN DE 30 SEGUNDOS**

```bash
# 1. Agregar MySQL
Railway → + New → Database → Add MySQL → Espera 2 min

# 2. Configurar Variables en Backend
MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=Q2xhdmVTZWNyZXRvUGFyYU1lc2FEZVBhcnRlc1BOUFF1ZUVzTXV5RGlmaWNpbERlQWRpdmluYXJZRXNhRXNMYUlkZWE=

# 3. Guardar y esperar redeploy (3-5 min)

# 4. Verificar logs: "Started MesadepartesApplication"
```

**¡Hazlo ahora y en 5 minutos estará funcionando!** 🎯
