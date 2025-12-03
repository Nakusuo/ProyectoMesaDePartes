# Control de Versiones del Proyecto
## Sistema de Mesa de Partes Digital - PNP

---

## Gestión del Repositorio

El proyecto fue gestionado utilizando **Git** como sistema de control de versiones y **GitHub** como plataforma de alojamiento remoto, permitiendo un desarrollo colaborativo, trazable y seguro.

**Repositorio:** [github.com/Nakusuo/ProyectoMesaDePartes](https://github.com/Nakusuo/ProyectoMesaDePartes)  
**Rama principal:** main  
**Total de commits:** 74+

---

## Historial de Desarrollo y Cambios Principales

A continuación se documenta el historial de desarrollo del proyecto, organizado por fases y funcionalidades implementadas.

---

### **FASE 1: Configuración Inicial del Proyecto**

#### Commit: `Initial commit`
**Descripción:** Creación del repositorio y estructura base del proyecto.

**Cambios realizados:**
- Inicialización del repositorio Git
- Creación de la estructura de carpetas del proyecto
- Configuración inicial del archivo `.gitignore`

---

### **FASE 2: Desarrollo del Backend - Spring Boot**

#### Commit: `Estructura base del backend`
**Descripción:** Implementación de la arquitectura base del servidor.

**Cambios realizados:**
- Configuración del proyecto Maven con Spring Boot 3.5.7
- Implementación de la estructura de paquetes MVC:
  - `model/` - Entidades JPA (Usuario, Documento, Area, Rol, etc.)
  - `repository/` - Repositorios Spring Data JPA
  - `service/` - Servicios de lógica de negocio
  - `controller/` - Controladores REST
  - `config/` - Configuraciones de seguridad y aplicación
  - `security/` - Implementación de JWT y Spring Security
- Configuración de conexión a MySQL
- Archivo `pom.xml` con todas las dependencias

#### Commit: `Implementación de Seguridad JWT`
**Descripción:** Sistema completo de autenticación y autorización.

**Archivos modificados:**
- `SecurityConfig.java` - Configuración de Spring Security
- `AuthTokenFilter.java` - Filtro de autenticación JWT
- `AuthEntryPointJwt.java` - Manejo de errores de autenticación
- `JwtUtils.java` - Utilidades para generación y validación de tokens
- `UserDetailsServiceImpl.java` - Servicio de carga de usuarios

**Funcionalidades implementadas:**
- Autenticación basada en JWT (JSON Web Tokens)
- Cifrado de contraseñas con BCrypt
- Control de accesos por roles (ADMIN, PERSONAL_OPERATIVO)
- Protección CSRF deshabilitada para API REST
- Configuración de CORS para frontend

#### Commit: `Controladores REST principales`
**Descripción:** Implementación de endpoints de la API.

**Archivos creados/modificados:**
- `AuthController.java` - Login y registro de usuarios
- `DocumentoController.java` - CRUD de documentos (+220 líneas)
- `UsuarioController.java` - Gestión de usuarios (+101 líneas)
- `DerivacionController.java` - Sistema de derivaciones (+102 líneas)
- `AreaController.java` - Gestión de áreas (+163 líneas)
- `ReporteController.java` - Generación de reportes (+60 líneas)
- `BitacoraController.java` - Auditoría del sistema (+54 líneas)

---

### **FASE 3: Desarrollo del Frontend**

#### Commit: `Estructura base del frontend`
**Descripción:** Creación de la interfaz de usuario.

**Estructura implementada:**
```
frontend/
├── assets/
│   ├── css/
│   │   ├── core/style.css, toast.css
│   │   ├── components/sidebar.css, custom-datepicker.css
│   │   └── pages/dashboard.css, auth/, admin/, documents/
│   ├── js/
│   │   ├── core/app-config.js, auth.js, config.js, logger.js
│   │   ├── components/sidebar.js, toast.js
│   │   └── pages/dashboard.js, auth/, admin/, documents/
│   └── images/
└── pages/
    ├── auth/login.html, registro.html
    ├── common/dashboard.html, sidebar.html
    ├── admin/bitacora.html, gestion-usuarios.html
    └── documents/documentos.html, registro-usuario.html, salida-documento.html
```

**Tecnologías utilizadas:**
- HTML5 semántico
- CSS3 con diseño responsive
- JavaScript Vanilla (ES6+)
- Fetch API para comunicación con backend
- Chart.js para gráficos estadísticos

#### Commit: `Sistema de autenticación frontend`
**Descripción:** Integración del login con el backend.

**Archivos implementados:**
- `login.html` - Formulario de inicio de sesión
- `auth.js` - Lógica de autenticación y manejo de tokens
- `permissions.js` - Control de permisos por rol
- `login.css` - Estilos de la pantalla de login

**Funcionalidades:**
- Formulario de login con validaciones
- Almacenamiento seguro del token JWT
- Redirección según rol del usuario
- Manejo de sesiones y cierre de sesión

---

### **FASE 4: Módulo de Documentos**

#### Commit: `CRUD completo de documentos`
**Descripción:** Implementación del módulo principal del sistema.

**Archivos backend:**
- `Documento.java` - Entidad con campos: código, título, descripción, estado, remitente, destinatario, archivo, fechas
- `DocumentoRepository.java` - Consultas personalizadas
- `DocumentoService.java` - Lógica de negocio
- `DocumentoController.java` - Endpoints REST

**Archivos frontend:**
- `documentos.html` - Interfaz de gestión de documentos
- `documentos.js` - Lógica del cliente
- `registro-usuario.html` - Formulario de registro de entrada
- `salida-documento.html` - Registro de documentos de salida
- `salida-documento.js` - Lógica de salida de documentos (+63 líneas)

**Funcionalidades implementadas:**
- Registro de documentos de entrada con código único automático
- Estados del documento: Asignado, Recibido, En_Proceso, Observado, Finalizado, Salida
- Carga de archivos adjuntos (PDF, JPG, PNG hasta 10MB)
- Búsqueda y filtrado por múltiples criterios
- Visualización del historial de documentos

---

### **FASE 5: Sistema de Derivaciones**

#### Commit: `Módulo de derivación de documentos`
**Descripción:** Sistema para asignar documentos a áreas y usuarios.

**Componentes implementados:**
- `Derivacion.java` - Entidad de derivación
- `DerivacionRepository.java` - Repositorio
- `DerivacionService.java` - Servicio de derivaciones
- `DerivacionController.java` - Endpoints de derivación

**Funcionalidades:**
- Derivar documentos a áreas específicas
- Asignar usuarios responsables
- Historial de derivaciones por documento
- Notificaciones automáticas al derivar

---

### **FASE 6: Sistema de Bitácora (Auditoría)**

#### Commit: `Implementación de bitácora de auditoría`
**Descripción:** Registro completo de todas las acciones del sistema.

**Archivos creados:**
- `Bitacora.java` - Entidad de registro de acciones
- `BitacoraRepository.java` - Repositorio con consultas
- `BitacoraService.java` - Servicio de auditoría (+76 líneas)
- `BitacoraController.java` - Endpoints de consulta (+42 líneas)

**Archivos frontend:**
- `bitacora.html` - Interfaz de consulta (+109 líneas modificadas)
- `bitacora.js` - Lógica de visualización (+145 líneas)
- `bitacora-export.js` - Exportación a Excel/PDF (+79 líneas)
- `bitacora.css` - Estilos específicos (+76 líneas)

**Información registrada:**
- Usuario que realizó la acción
- Tipo de acción (Crear, Editar, Eliminar, Consultar, Derivar)
- Módulo afectado
- Fecha y hora exacta
- Dirección IP del cliente
- Detalles específicos de cada acción

---

### **FASE 7: Generación de Reportes**

#### Commit: `Sistema de reportes PDF y Excel`
**Descripción:** Exportación de información del sistema.

**Dependencias agregadas (pom.xml):**
```xml
<!-- Apache POI para Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>

<!-- iText 7 para PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>kernel</artifactId>
    <version>7.2.5</version>
</dependency>
```

**Archivos implementados:**
- `ReporteService.java` - Generación de reportes
- `ReporteController.java` - Endpoints de descarga
- `reportes.js` - Integración frontend
- `reportes-global.js` - Funciones globales de reportes

**Tipos de reportes:**
- Documentos registrados (por fecha, área, estado)
- Tiempos de atención promedio
- Productividad por usuario
- Documentos por área

---

### **FASE 8: Sistema de Notificaciones**

#### Commit: `Módulo de notificaciones internas`
**Descripción:** Alertas automáticas dentro de la aplicación.

**Archivos implementados:**
- `Notificacion.java` - Entidad de notificación
- `NotificacionRepository.java` - Repositorio
- `NotificacionService.java` - Servicio de notificaciones
- `NotificacionController.java` - Endpoints
- `notificaciones.js` - Visualización en frontend

**Funcionalidades:**
- Notificación al derivar documento
- Alertas de documentos próximos a vencer
- Marcado de notificaciones como leídas
- Contador de notificaciones pendientes

---

### **FASE 9: Sistema de Backups Automáticos**

#### Commit: `Backups automatizados con Spring Scheduler`
**Descripción:** Respaldo automático de la base de datos.

**Archivos creados:**
- `SchedulingConfig.java` - Configuración de tareas programadas (+17 líneas)
- `BackupService.java` - Servicio de respaldo (+329 líneas)
- `BackupController.java` - API de backups (+160 líneas)

**Configuración (application.properties):**
```properties
# Backups Automáticos
backup.enabled=true
backup.directory=../backups
backup.schedule=0 0 2 * * ?  # Diario a las 2:00 AM
backup.retention.days=30
```

**Funcionalidades:**
- Backup automático diario a las 2:00 AM
- Backup manual mediante API REST
- Retención configurable (30 días por defecto)
- Limpieza automática de backups antiguos
- Listado de backups disponibles

---

### **FASE 10: Preparación para Despliegue en la Nube**

#### Commit: `Configuración Railway y Docker`
**Descripción:** Preparación del proyecto para deployment.

**Archivos creados:**
- `Dockerfile` - Imagen Docker del backend (+60 líneas)
- `.dockerignore` - Exclusiones de Docker (+68 líneas)
- `railway.json` - Configuración de Railway
- `application-railway.properties` - Perfil de producción

**Estructura de deployment:**
```
deployment/
└── railway/
    ├── README.md
    ├── railway.json
    ├── railwayignore
    ├── preparar-railway.bat
    └── verificar-railway.bat
```

**Configuraciones de producción:**
- Variables de entorno para credenciales
- Perfil específico para Railway
- Healthcheck del servicio
- Configuración SSL/HTTPS (preparada)

---

### **FASE 11: Documentación y Assets**

#### Commit: `Recursos visuales e imágenes del sistema`
**Descripción:** Capturas de pantalla y recursos gráficos.

**Imágenes agregadas:**
- `bitacora.png` - Captura del módulo de bitácora
- `dashboard.png` - Vista del panel principal
- `registro.png` - Formulario de registro de documentos
- `salida.png` - Módulo de salida de documentos
- `usuarios.png` - Gestión de usuarios
- `logoPNP.png` - Logo institucional
- `calendario.png` - Selector de fechas
- `hoja.png` - Icono de documentos

#### Commit: `Scripts de mantenimiento`
**Descripción:** Herramientas de administración del sistema.

**Scripts creados:**
- `cleanup-orphaned-files.ps1` - Limpieza de archivos huérfanos (+199 líneas)
- `remove-cors-annotations.ps1` - Refactorización de CORS (+26 líneas)
- `backup_windows.bat` - Backup manual en Windows
- `backup_linux.sh` - Backup manual en Linux
- `restaurar_backup_windows.bat` - Restauración de backups
- `verificar_backups.bat` - Verificación de integridad

---

### **FASE 12: Documentación API (Swagger)**

#### Commit: `Documentación OpenAPI/Swagger`
**Descripción:** Documentación interactiva de la API REST.

**Dependencia agregada:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**Archivo configuración:**
- `SwaggerConfig.java` - Configuración de OpenAPI

**Acceso a documentación:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

### **FASE 13: Logger y Monitoreo**

#### Commit: `Sistema de logging mejorado`
**Descripción:** Registro detallado de eventos y errores.

**Archivos modificados:**
- `logger.js` - Logger frontend mejorado (+211 líneas modificadas)
- `logback-spring.xml` - Configuración de logs backend
- `application.properties` - Niveles de logging

**Configuración de logs:**
```properties
logging.level.root=INFO
logging.level.com.pnp.mesadepartes=DEBUG
logging.file.name=logs/mesa-partes.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
```

**Características:**
- Logs rotativos (máximo 10MB por archivo)
- Retención de 30 días
- Niveles configurables por paquete
- Formato detallado con timestamp y thread

---

### **FASE 14: Actuator (Monitoreo en Producción)**

#### Commit: `Spring Boot Actuator`
**Descripción:** Endpoints de monitoreo y métricas.

**Dependencia:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Endpoints habilitados:**
- `/actuator/health` - Estado de salud del servicio
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas del sistema (JVM, HTTP, etc.)

---

## Estadísticas del Repositorio

| Métrica | Valor |
|---------|-------|
| Total de commits | 74+ |
| Archivos Java | 35+ |
| Archivos JavaScript | 15+ |
| Archivos HTML | 10+ |
| Archivos CSS | 10+ |
| Archivos de configuración | 8+ |
| Scripts de mantenimiento | 6+ |
| Líneas de código (aprox.) | 15,000+ |

---

## Ramas del Proyecto

| Rama | Propósito |
|------|-----------|
| `main` | Rama principal de producción |
| `dev` | Desarrollo e integración (cuando se usó) |
| `feature/*` | Desarrollo de funcionalidades específicas |

---

## Buenas Prácticas Aplicadas

- ✅ **Commits frecuentes:** Guardado regular del progreso
- ✅ **Historial preservado:** Trazabilidad completa del desarrollo
- ✅ **Archivos .gitignore:** Exclusión de archivos sensibles y generados
- ✅ **Documentación:** READMEs actualizados con instrucciones
- ✅ **Separación de entornos:** Perfiles de configuración (dev, railway)
- ✅ **Backup del código:** Repositorio remoto en GitHub

---

## Conclusión

El control de versiones permitió:
1. **Colaboración efectiva** entre los miembros del equipo
2. **Trazabilidad completa** de todos los cambios realizados
3. **Reversión segura** ante errores o problemas
4. **Documentación automática** del progreso del proyecto
5. **Respaldo permanente** del código fuente

El uso de Git y GitHub fue fundamental para el éxito del desarrollo del Sistema de Mesa de Partes Digital.

---

**Desarrollado por:**
- García Ortega Shayuri - Mantenimiento y Documentación
- López Díaz Maryafernanda - Testing
- Mantari Licapa Walter - Testing y Monitoreo
- Rodriguez Munaylla Marcela - Backend y Frontend

**Universidad Tecnológica del Perú**  
**Facultad de Ingeniería de Sistemas e Informática**  
**Curso Integrador I: Sistemas Software**  
**2025 - 2**
