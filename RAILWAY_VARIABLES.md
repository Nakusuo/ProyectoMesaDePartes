# 🚂 CONFIGURACIÓN DE VARIABLES DE ENTORNO EN RAILWAY

## ⚠️ IMPORTANTE: Configura estas variables ANTES de hacer el deploy

### 📍 Cómo configurar en Railway:

1. Ve a tu proyecto en Railway
2. Click en tu servicio Backend
3. Ve a la pestaña **Variables**
4. Agrega las siguientes variables:

---

## 🔧 VARIABLES REQUERIDAS

### 🗄️ Base de Datos MySQL (Railway automático)

Railway genera automáticamente estas variables cuando agregas el servicio MySQL. **NO necesitas configurarlas manualmente**, pero verifica que existan:

```
✅ MYSQLHOST       (Automático)
✅ MYSQLPORT       (Automático)
✅ MYSQLDATABASE   (Automático)
✅ MYSQLUSER       (Automático)
✅ MYSQLPASSWORD   (Automático)
```

### 🔐 Seguridad JWT (MANUAL - REQUERIDO)

**JWT_SECRET**: Token secreto para firmar JWT
```
JWT_SECRET=TuClaveSecretaSuperSeguraQueDebesTener64CaracteresOmas123456789
```

💡 **Cómo generar un JWT_SECRET seguro**:
```bash
# Opción 1: Usando OpenSSL (recomendado)
openssl rand -base64 64

# Opción 2: Usando Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"

# Opción 3: Online
https://generate-secret.vercel.app/64
```

### 🌐 Configuración de Puerto (Railway automático)

```
✅ PORT            (Railway lo asigna automáticamente)
```

---

## 📋 VARIABLES OPCIONALES

### 📧 Email SMTP (Para notificaciones)

```
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=tu-email@gmail.com
SMTP_PASSWORD=tu-contraseña-app
```

💡 **Para Gmail**: Necesitas generar una "Contraseña de aplicación"
1. Ve a https://myaccount.google.com/security
2. Activa "Verificación en 2 pasos"
3. Genera una "Contraseña de aplicación"

---

## ✅ CHECKLIST DE CONFIGURACIÓN

### Paso 1: Crear servicio MySQL en Railway

1. En tu proyecto Railway, click en **"+ New"**
2. Selecciona **"Database" → "Add MySQL"**
3. Espera a que se aprovisione (1-2 minutos)
4. Railway generará automáticamente las variables MYSQL*

### Paso 2: Configurar variables del Backend

1. Click en tu servicio **Backend**
2. Ve a **Variables**
3. Agrega estas variables:

```env
# JWT Secret (genera uno nuevo)
JWT_SECRET=Q2xhdmVTZWNyZXRvUGFyYU1lc2FEZVBhcnRlc1BOUFF1ZUVzTXV5RGlmaWNpbERlQWRpdmluYXJZRXNhRXNMYUlkZWE=

# Email (opcional, solo si vas a usar notificaciones)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=
SMTP_PASSWORD=
```

### Paso 3: Vincular Base de Datos con Backend

Railway debería vincular automáticamente las variables del servicio MySQL con tu Backend. Verifica que en las **Variables del Backend** veas:

```
${{MySQL.MYSQLHOST}}
${{MySQL.MYSQLPORT}}
${{MySQL.MYSQLDATABASE}}
${{MySQL.MYSQLUSER}}
${{MySQL.MYSQLPASSWORD}}
```

Si NO aparecen, agrégalas manualmente con estos valores.

### Paso 4: Deploy

1. Asegúrate de haber subido los archivos corregidos a GitHub:
   - ✅ `Dockerfile` (con perfil railway)
   - ✅ `railway.json` (sin healthcheck temporal)
   - ✅ `application-railway.properties` (nuevo archivo)
   - ✅ `DocumentoController.java` (con imports corregidos)
   - ✅ `ReporteController.java` (con DateTimeFormatter)
   - ✅ `ReporteService.java` (con switch corregido)

2. Railway detectará el commit y hará redeploy automáticamente

3. Espera 3-5 minutos para el build

---

## 🔍 VERIFICACIÓN POST-DEPLOY

### 1. Ver logs en tiempo real

En Railway:
1. Click en tu servicio Backend
2. Ve a **Deployments**
3. Click en el deployment activo
4. Ve a **View Logs**

### 2. Probar endpoints

Una vez que el servicio esté corriendo, prueba:

```bash
# Obtener la URL de Railway (algo como: https://tu-proyecto.up.railway.app)

# Test básico
curl https://tu-proyecto.up.railway.app/

# Test health (si está habilitado)
curl https://tu-proyecto.up.railway.app/actuator/health

# Test login
curl -X POST https://tu-proyecto.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## ❌ TROUBLESHOOTING

### Error: "Application failed to start"

**Causa**: Variables de entorno faltantes

**Solución**:
1. Verifica que JWT_SECRET esté configurado
2. Verifica que las variables MySQL estén vinculadas
3. Revisa los logs completos

### Error: "Connection refused" a MySQL

**Causa**: Base de datos no está lista o no vinculada

**Solución**:
1. Asegúrate de que el servicio MySQL esté corriendo (verde)
2. Verifica las variables ${{MySQL.*}}
3. Espera 30 segundos más después del deploy

### Error: "Port already in use"

**Causa**: Railway no puede asignar el puerto

**Solución**:
- No hagas nada, Railway maneja esto automáticamente
- Asegúrate de que application-railway.properties use ${PORT:8080}

---

## 📞 PRÓXIMOS PASOS

Una vez que el servicio esté corriendo:

1. ✅ Accede a los logs y verifica que Spring Boot levantó correctamente
2. ✅ Anota la URL pública de Railway
3. ✅ Crea un usuario admin inicial (puede ser vía SQL o endpoint)
4. ✅ Prueba el login desde Postman/Insomnia
5. ✅ Configura el frontend para apuntar a la URL de Railway

---

## 📝 RESUMEN DE ARCHIVOS MODIFICADOS

```
ProyectoMesaDePartes/
├── Dockerfile                                    ✅ Actualizado (perfil railway)
├── railway.json                                  ✅ Actualizado (sin healthcheck)
└── backend/
    └── src/
        └── main/
            ├── java/
            │   └── com/pnp/mesadepartes/
            │       ├── controller/
            │       │   ├── DocumentoController.java     ✅ Imports corregidos
            │       │   └── ReporteController.java       ✅ DateTimeFormatter
            │       └── service/
            │           └── ReporteService.java          ✅ Switch corregido
            └── resources/
                └── application-railway.properties        ✅ Nuevo archivo
```

---

**💡 TIP FINAL**: Si después de configurar todo sigue fallando, deshabilita temporalmente Spring Security para debuggear más fácilmente. Pero recuerda reactivarla después.

---

**🎯 ¿TODO LISTO?**

✅ MySQL servicio creado en Railway  
✅ JWT_SECRET configurado  
✅ Variables MySQL vinculadas  
✅ Archivos subidos a GitHub  
✅ Deploy automático iniciado  

**Ahora solo espera 3-5 minutos y revisa los logs** 📊
