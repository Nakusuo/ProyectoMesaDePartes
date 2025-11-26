# ⚠️ CHECKLIST URGENTE - RAILWAY

## 🔴 **VERIFICA ESTO AHORA EN RAILWAY**

### 1️⃣ **Servicio MySQL**
```
Railway → Servicios → MySQL
```

**Debe estar**:
- ✅ Verde (corriendo)
- ✅ Con conexión activa
- ✅ Aparece en la lista de servicios

**Si NO existe**:
1. Click "+ New"
2. "Database" 
3. "Add MySQL"
4. Espera 2 minutos

---

### 2️⃣ **Variables de Entorno**
```
Railway → Backend Service → Variables
```

**DEBE tener MÍNIMO estas variables**:

```
JWT_SECRET=Q2xhdmVTZWNyZXRvUGFyYU1lc2FEZVBhcnRlc1BOUFF1ZUVzTXV5RGlmaWNpbERlQWRpdmluYXJZRXNhRXNMYUlkZWE=

MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
```

**Formato correcto**:
- NO uses `${{Mysql.*}}` (minúscula)
- SÍ usa `${{MySQL.*}}` (M mayúscula)

**Si faltan variables**:
1. Click "New Variable"
2. Copia exactamente como arriba
3. Save

---

### 3️⃣ **Deploy Logs - BUSCA ESTOS ERRORES**

```
Railway → Deployments → Click deployment → Deploy Logs
```

#### ❌ **Error 1: JWT_SECRET faltante**
```
Error: Could not resolve placeholder 'mesadepartes.app.jwtSecret'
```
**Solución**: Agrega variable `JWT_SECRET` (ver arriba)

#### ❌ **Error 2: MySQL no conecta**
```
Communications link failure
Connection refused to host: MYSQLHOST
```
**Solución**: Verifica que MySQL esté corriendo Y variables vinculadas

#### ❌ **Error 3: Puerto incorrecto**
```
Failed to bind to 0.0.0.0:8080
Port already in use
```
**Solución**: Railway asigna puerto automáticamente via `PORT` variable

#### ❌ **Error 4: Tablas no existen**
```
Table 'mesa_partes_db.usuarios' doesn't exist
```
**Solución**: Base de datos vacía (NORMAL primera vez)
- Opción A: Agrega variable `SPRING_JPA_HIBERNATE_DDL_AUTO=create`
- Opción B: Importa SQL manualmente

---

### 4️⃣ **Archivo application-railway.properties**

**Verifica en GitHub que exista**:
```
backend/src/main/resources/application-railway.properties
```

**Si NO existe**:
- Súbelo desde tu carpeta local
- Está en: `c:\Users\MARCELA\Desktop\SoftwareCore\ProyectoMesaDePartes\backend\src\main\resources\`

---

## 🚨 **ACCIÓN INMEDIATA**

1. **Captura los Deploy Logs** (últimas 50 líneas)
2. **Captura tus Variables configuradas** (screenshot)
3. **Verifica si MySQL está corriendo** (verde/rojo)
4. **Compárteme esa info**

Con eso sabré **exactamente** qué está fallando.

---

## 💡 **DIAGNÓSTICO RÁPIDO POR SÍNTOMA**

### Síntoma: "service unavailable" en healthcheck
**Significa**: Spring Boot NO levantó

**Causas comunes**:
1. MySQL no configurado (80% de los casos)
2. JWT_SECRET faltante (15% de los casos)
3. Error en código Java (5% de los casos)

### Síntoma: Build falla
**Significa**: Código Java tiene errores

**Ya lo arreglamos** ✅ (tu build es exitoso)

### Síntoma: Deploy exitoso pero healthcheck falla
**Significa**: Contenedor corre, pero app dentro no arranca

**ES TU CASO** ← Necesito ver logs

---

## 📞 **RESPONDE ESTO**

1. ¿Tienes servicio MySQL en Railway? (Sí/No)
2. ¿Configuraste JWT_SECRET? (Sí/No)
3. ¿Qué dicen los Deploy Logs? (Copia últimas líneas)
4. ¿Subiste application-railway.properties a GitHub? (Sí/No)

**Con eso lo soluciono en 5 minutos** 🚀
