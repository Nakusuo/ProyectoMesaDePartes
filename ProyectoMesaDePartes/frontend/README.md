# Frontend - Mesa de Partes Digital PNP

## 📁 Estructura del Proyecto

```
frontend/
├── index.html              # Página principal
├── login.html              # Página de inicio de sesión
├── registro.html           # Registro de documentos externos
├── registro-usuario.html   # Registro de usuarios internos
├── dashboard.html          # Panel de control
├── documentos.html         # Gestión de documentos
├── gestion-usuarios.html   # Administración de usuarios
├── bitacora.html          # Registro de actividades
├── salida-documento.html  # Gestión de salida de documentos
├── sidebar.html           # Menú lateral compartido
└── assets/
    ├── css/               # Estilos CSS
    │   ├── style.css
    │   ├── login.css
    │   ├── dashboard.css
    │   ├── registro.css
    │   ├── gestion-usuarios.css
    │   ├── bitacora.css
    │   ├── sidebar.css
    │   └── toast.css
    └── js/                # Scripts JavaScript
        ├── config.js      # Configuración de API
        ├── auth.js        # Autenticación
        ├── permissions.js # Permisos y roles
        ├── toast.js       # Notificaciones
        ├── sidebar.js     # Menú lateral
        ├── login.js       # Login
        ├── registro.js    # Registro externo
        ├── registrar-interno.js # Registro interno
        ├── dashboard.js   # Dashboard
        ├── documentos.js  # Documentos
        ├── gestion-usuarios.js # Usuarios
        └── bitacora.js    # Bitácora
```

## 🚀 Configuración

### Spring Boot
El backend de Spring Boot está configurado para servir los archivos estáticos desde esta carpeta `frontend/` automáticamente.

**Configuración en `application.properties`:**
```properties
spring.web.resources.static-locations=file:frontend/
spring.web.resources.add-mappings=true
```

**Configuración en `FileUploadConfig.java`:**
```java
registry.addResourceHandler("/**")
        .addResourceLocations("file:frontend/")
        .setCachePeriod(0); // Sin cache para desarrollo
```

### Acceso
- **URL Base**: `http://localhost:8080/`
- **Login**: `http://localhost:8080/login.html`
- **Dashboard**: `http://localhost:8080/dashboard.html`
- **API REST**: `http://localhost:8080/api/`

## 🔧 Desarrollo

### Para modificar archivos:
1. Edita los archivos en la carpeta `frontend/`
2. Recarga el navegador (F5)
3. No necesitas recompilar Spring Boot para cambios en HTML/CSS/JS

### Para habilitar cache en producción:
Cambia en `FileUploadConfig.java`:
```java
.setCachePeriod(0);  // Desarrollo (sin cache)
```
A:
```java
.setCachePeriod(3600);  // Producción (1 hora de cache)
```

## 📝 Notas

- ✅ El directorio `backend/src/main/resources/static` ya NO se usa
- ✅ Todos los archivos estáticos están unificados en `frontend/`
- ✅ La configuración es automática al iniciar Spring Boot
- ✅ Los cambios en archivos frontend NO requieren reiniciar el servidor

## 🎯 Próximos Pasos

Si quieres eliminar completamente la carpeta `static` del backend:
```cmd
rmdir /S backend\src\main\resources\static
```

Asegúrate de que no haya archivos abiertos en el editor antes de eliminarla.
