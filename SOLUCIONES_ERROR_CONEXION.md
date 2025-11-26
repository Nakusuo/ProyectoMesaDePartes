# 🔧 Soluciones al Error de Conexión MySQL

## ❌ Error Actual
```
Connection refused
Communications link failure
Unable to open JDBC Connection for DDL execution
```

## 📋 Diagnóstico

El error ocurre porque la aplicación está usando el perfil `railway` que intenta conectarse a:
- Host: `localhost` (variable MYSQLHOST no definida)
- Puerto: `3306` (variable MYSQLPORT no definida)

Pero **MySQL no está corriendo localmente** o las variables de entorno no están configuradas.

---

## ✅ SOLUCIÓN 1: Ejecutar Localmente (Recomendado)

### Paso 1: Verificar MySQL Local
```cmd
mysql -u root -p
```

Si MySQL no está instalado:
- **Windows**: Descarga desde https://dev.mysql.com/downloads/installer/
- Instala MySQL Community Server
- Configura usuario `root` sin contraseña o con contraseña conocida

### Paso 2: Crear Base de Datos
```sql
CREATE DATABASE IF NOT EXISTS mesa_partes_db;
USE mesa_partes_db;
```

### Paso 3: Ejecutar con Perfil DEV
Usa el archivo `start-app.bat` (ya actualizado):
```cmd
cd backend
start-app.bat
```

O manualmente:
```cmd
cd backend
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Configuración del Perfil DEV
El archivo `application-dev.properties` usa:
- **Host**: localhost
- **Puerto**: 3306
- **Base de datos**: mesa_partes_db
- **Usuario**: root
- **Contraseña**: (vacía)

Si tu MySQL tiene contraseña, edita `backend\src\main\resources\application-dev.properties`:
```properties
spring.datasource.password=TU_PASSWORD
```

---

## ✅ SOLUCIÓN 2: Configurar Variables de Railway para Ejecución Local

Si necesitas probar el perfil railway localmente:

### Paso 1: Definir Variables de Entorno
```cmd
set MYSQLHOST=localhost
set MYSQLPORT=3306
set MYSQLDATABASE=mesa_partes_db
set MYSQLUSER=root
set MYSQLPASSWORD=
set PORT=8080
```

### Paso 2: Ejecutar con Perfil Railway
```cmd
cd backend
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=railway
```

---

## ✅ SOLUCIÓN 3: Cambiar Perfil Activo por Defecto

Si quieres que siempre use el perfil DEV localmente, edita `application.properties`:

```properties
spring.profiles.active=dev
```

---

## 🚀 Despliegue en Railway

Para Railway, asegúrate de tener estas variables configuradas en el dashboard:

1. Ve a tu proyecto en Railway
2. Selecciona el servicio MySQL
3. Copia las variables:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

4. En el servicio de la aplicación Spring Boot, agrega las referencias:
   ```
   MYSQLHOST=${{MySQL.MYSQLHOST}}
   MYSQLPORT=${{MySQL.MYSQLPORT}}
   MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
   MYSQLUSER=${{MySQL.MYSQLUSER}}
   MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
   ```

---

## 🔍 Verificar Conexión

Después de iniciar la aplicación, verifica el log:
```
✅ EXITOSO: Verás "Started MesadepartesApplication"
❌ ERROR: Verás "Communications link failure"
```

### Test de Conexión Manual
```cmd
mysql -h localhost -P 3306 -u root -p mesa_partes_db
```

---

## 📝 Resumen Rápido

**Para desarrollo local:**
```cmd
cd backend
start-app.bat
```

**Requisitos:**
1. MySQL instalado y corriendo
2. Base de datos `mesa_partes_db` creada
3. Usuario `root` con acceso

**Verificar MySQL corriendo:**
```cmd
netstat -an | findstr :3306
```
