# 📄 INFORME TÉCNICO DEL PROYECTO
## Sistema de Mesa de Partes Digital - PNP

**🌐 Versión:** Pública 1.0  
**📅 Fecha:** 3 de Diciembre de 2025  
**👥 Autor:** Equipo de Desarrollo

---

> ⚠️ **NOTA IMPORTANTE:** Esta es una **COPIA PÚBLICA** del informe técnico completo del sistema.
> 
> **Propósito:** Facilitar el acceso y revisión por parte de compañeras y colaboradores internos del proyecto.
> 
> **Advertencia de seguridad:** Antes de compartir este documento fuera de la organización, verifica que no contenga:
> - Credenciales de acceso o contraseñas
> - Direcciones IP privadas o configuraciones sensibles
> - Claves secretas (JWT Secret, API Keys, etc.)
> - Información confidencial de la institución

---

# INFORME TÉCNICO DEL PROYECTO
## Sistema de Mesa de Partes Digital - PNP

**Fecha:** 3 de Diciembre de 2025  
**Versión:** 1.0  
**Autor:** Equipo de Desarrollo  

---

## 1. RSUMEN EJECUTIVO

El Sistema de Mesa de Partes Digital es una plataforma web diseñada para modernizar y digitalizar la gestión documental de la Policía Nacional del Perú (PNP). El sistema permite el registro, seguimiento, derivación y control de documentos administrativos, reemplazando el proceso manual tradicional por una solución digital eficiente, trazable y segura.

### 1.1 Objetivos del Sistema
- Digitalizar el proceso de recepción y registro de documentos
- Automatizar el flujo de derivaciones entre áreas
- Proporcionar trazabilidad completa del ciclo de vida de los documentos
- Generar reportes y estadísticas en tiempo real
- Mejorar la eficiencia operativa y reducir tiempos de respuesta

---

## 2. ARQUITECTURA DEL SISTEMA

### 2.1 Arquitectura General
El sistema implementa una arquitectura cliente-servidor de tres capas:

```
┌─────────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN                │
│    (Frontend - Vanilla JavaScript)          │
│  - HTML5, CSS3, JavaScript ES6+             │
│  - Interface responsive                     │
└─────────────────────────────────────────────┘
                    ↕ HTTP/REST
┌─────────────────────────────────────────────┐
│         CAPA DE LÓGICA DE NEGOCIO           │
│      (Backend - Spring Boot 3.5.7)          │
│  - API REST                                 │
│  - Autenticación JWT                        │
│  - Lógica de negocio                        │
└─────────────────────────────────────────────┘
                    ↕ JDBC
┌─────────────────────────────────────────────┐
│         CAPA DE DATOS                       │
│         (MySQL 8.0+)                        │
│  - Base de datos relacional                 │
│  - Almacenamiento persistente               │
└─────────────────────────────────────────────┘
```

### 2.2 Stack Tecnológico

#### **Backend**
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 LTS | Lenguaje de programación |
| Spring Boot | 3.5.7 | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Persistencia de datos |
| MySQL Connector | 8.x | Driver de base de datos |
| JWT (JJWT) | 0.12.6 | Generación de tokens |
| Lombok | Latest | Reducción de código boilerplate |
| Apache POI | 5.3.0 | Generación de archivos Excel |
| iText 7 | 7.2.5 | Generación de archivos PDF |
| SpringDoc OpenAPI | 2.3.0 | Documentación API |
| Spring Boot Mail | 3.x | Notificaciones por email |
| Spring Boot Actuator | 3.x | Monitoreo y métricas |

#### **Frontend**
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| HTML5 | - | Estructura de páginas |
| CSS3 | - | Estilos y diseño responsive |
| JavaScript | ES6+ | Lógica del cliente |
| Chart.js | 4.x | Gráficas y visualización |
| Fetch API | Nativa | Comunicación HTTP |

#### **Base de Datos**
| Componente | Especificación |
|------------|----------------|
| DBMS | MySQL 8.0+ |
| Motor de almacenamiento | InnoDB |
| Charset | utf8mb4 |
| Collation | utf8mb4_unicode_ci |

---

## 3. ESTRUCTURA DEL PROYECTO

### 3.1 Organización del Backend

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/pnp/mesadepartes/
│   │   │   ├── config/          # Configuración Spring
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   └── BackupScheduler.java
│   │   │   ├── controller/      # Controladores REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DocumentoController.java
│   │   │   │   ├── UsuarioController.java
│   │   │   │   ├── DerivacionController.java
│   │   │   │   ├── SalidaDocumentoController.java
│   │   │   │   ├── BitacoraController.java
│   │   │   │   ├── NotificacionController.java
│   │   │   │   ├── ReporteController.java
│   │   │   │   ├── BackupController.java
│   │   │   │   └── AreaController.java
│   │   │   ├── model/           # Entidades JPA
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Rol.java
│   │   │   │   ├── Documento.java
│   │   │   │   ├── TipoDocumento.java
│   │   │   │   ├── HojaTramite.java
│   │   │   │   ├── Derivacion.java
│   │   │   │   ├── SalidaDocumento.java
│   │   │   │   ├── Bitacora.java
│   │   │   │   ├── Notificacion.java
│   │   │   │   └── Area.java
│   │   │   ├── repository/      # Repositorios JPA
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── security/        # JWT y autenticación
│   │   │   ├── dto/             # Objetos de transferencia
│   │   │   └── exception/       # Manejo de excepciones
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-railway.properties
│   │       └── logback-spring.xml
│   └── test/                    # Tests unitarios
├── pom.xml                      # Dependencias Maven
└── uploads/                     # Archivos subidos
```

### 3.2 Organización del Frontend

```
frontend/
├── assets/
│   ├── css/
│   │   ├── core/               # Estilos base
│   │   │   ├── style.css       # Estilos globales
│   │   │   └── toast.css       # Notificaciones
│   │   ├── components/         # Componentes reutilizables
│   │   │   ├── sidebar.css
│   │   │   └── custom-datepicker.css
│   │   ├── pages/              # Estilos por página
│   │   │   ├── dashboard.css
│   │   │   ├── admin/
│   │   │   └── documents/
│   │   └── features/           # Funcionalidades específicas
│   └── js/
│       ├── core/               # Configuración base
│       │   ├── config.js       # URLs y constantes
│       │   ├── auth.js         # Autenticación
│       │   ├── logger.js       # Sistema de logs
│       │   └── permissions.js  # Control de permisos
│       ├── components/         # Componentes JS
│       │   ├── sidebar.js
│       │   ├── toast.js
│       │   └── custom-datepicker.js
│       ├── modules/            # Módulos funcionales
│       │   ├── reportes.js
│       │   ├── reportes-global.js
│       │   └── notificaciones.js
│       └── pages/              # Lógica por página
│           ├── dashboard.js
│           ├── admin/
│           │   ├── gestion-usuarios.js
│           │   └── bitacora.js
│           ├── auth/
│           │   ├── login.js
│           │   └── registro.js
│           └── documents/
│               ├── documentos.js
│               ├── registro-usuario.js
│               └── salida-documento.js
└── pages/
    ├── auth/                   # Autenticación
    ├── common/                 # Páginas comunes
    ├── admin/                  # Administración
    └── documents/              # Gestión documental
```

---

## 4. MODELO DE DATOS

### 4.1 Diagrama Entidad-Relación (Simplificado)

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   ROLES     │───────│USUARIO_ROLES │───────│  USUARIOS   │
└─────────────┘   N:M └──────────────┘   N:M └─────────────┘
                                                    │
                                                    │ 1:N
                                                    ↓
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   AREAS     │───────│  DOCUMENTOS  │───────│ TIPO_DOC    │
└─────────────┘   1:N └──────────────┘   N:1 └─────────────┘
                           │      │
                    ┌──────┘      └──────┐
                    │ 1:1                │ 1:N
                    ↓                    ↓
            ┌──────────────┐     ┌─────────────┐
            │HOJA_TRAMITE  │     │DERIVACIONES │
            └──────────────┘     └─────────────┘
                                        │ 1:N
                                        ↓
                                 ┌─────────────┐
                                 │  BITACORA   │
                                 └─────────────┘
```

### 4.2 Tablas Principales

#### **usuarios**
Almacena información de los usuarios del sistema.
- **Campos clave:** ID_usuario, username, password_hash, email, tipo_contrato (CAS/LOCADOR/PNP)
- **Relaciones:** N:M con roles, N:1 con areas
- **Índices:** username, email, activo

#### **documentos**
Registro central de todos los documentos del sistema.
- **Campos clave:** ID_documento, codigo (único), titulo, estado, remitente, destinatario
- **Estados:** Asignado, Recibido, En_Proceso, Observado, Finalizado, Salida
- **Relaciones:** N:1 con tipos_documento, 1:1 con hojas_tramite, 1:N con derivaciones

#### **hojas_tramite**
Hojas de trámite asociadas a documentos de entrada.
- **Campos clave:** ID_hoja_tramite, numero_ht, ID_documento
- **Formato HT:** AñoNúmeros (ej: 20250235689)
- **Relación:** 1:1 con documentos

#### **derivaciones**
Trazabilidad de movimientos de documentos entre áreas.
- **Campos clave:** ID_derivacion, origen, destino, estado, prioridad
- **Estados:** Pendiente, Aceptada, Rechazada, En_Proceso, Finalizada
- **Relaciones:** N:1 con documentos, N:1 con usuarios (asignado_a)

#### **salidas_documento**
Registro de salida de documentos del sistema.
- **Campos clave:** ID_salida, codigo_salida, destino_externo, fecha_salida
- **Relaciones:** N:1 con documentos, N:1 con usuarios (registrado_por)

#### **bitacora**
Auditoría unificada de todas las operaciones del sistema.
- **Campos clave:** ID_bitacora, accion, detalles, IP_usuario, fecha_hora
- **Tipos de acción:** LOGIN, LOGOUT, REGISTRO_DOC, DERIVACION, etc.
- **Relaciones:** N:1 con usuarios

#### **notificaciones**
Sistema de notificaciones en tiempo real.
- **Campos clave:** ID_notificacion, tipo, mensaje, leida
- **Tipos:** DERIVACION, ASIGNACION, CAMBIO_ESTADO, ALERTA
- **Relaciones:** N:1 con usuarios, N:1 con documentos

---

## 5. FUNCIONALIDADES PRINCIPALES

### 5.1 Módulo de Autenticación y Seguridad

#### **Características:**
- ✅ Login con JWT (JSON Web Tokens)
- ✅ Sesiones con expiración configurable (8 horas por defecto)
- ✅ Hash de contraseñas con BCrypt
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Roles disponibles: Administrador, Jefatura, Digitador, Usuario

#### **Endpoints principales:**
```
POST /api/auth/login          # Autenticación
POST /api/auth/registro       # Registro de nuevos usuarios
POST /api/auth/logout         # Cierre de sesión
GET  /api/auth/validate       # Validación de token
```

#### **Flujo de autenticación:**
```
1. Usuario ingresa credenciales
2. Backend valida usuario/contraseña (BCrypt)
3. Genera token JWT con roles y permisos
4. Frontend almacena token en localStorage
5. Cada petición incluye token en header Authorization
6. Backend valida token antes de procesar request
```

### 5.2 Módulo de Gestión Documental

#### **Características:**
- ✅ Registro de documentos con información detallada
- ✅ Generación automática de códigos únicos (DOC-XXXXXX)
- ✅ Carga de archivos adjuntos (PDF, imágenes, Office)
- ✅ Búsqueda avanzada por múltiples criterios
- ✅ Filtros por estado, fecha, tipo, remitente
- ✅ Visualización de historial completo de movimientos
- ✅ Edición y actualización de documentos

#### **Estados del documento:**
1. **Asignado:** Documento registrado y asignado a un usuario
2. **Recibido:** Usuario confirma recepción
3. **En_Proceso:** Documento en revisión/procesamiento
4. **Observado:** Documento con observaciones que requieren corrección
5. **Finalizado:** Procesamiento completo con informe final
6. **Salida:** Documento ha salido del sistema

#### **Endpoints principales:**
```
POST   /api/documentos                    # Crear documento
GET    /api/documentos                    # Listar documentos
GET    /api/documentos/{id}               # Detalle de documento
PUT    /api/documentos/{id}               # Actualizar documento
DELETE /api/documentos/{id}               # Eliminar documento
GET    /api/documentos/buscar/{codigo}    # Buscar por código
POST   /api/documentos/{id}/upload        # Subir archivo adjunto
```

### 5.3 Módulo de Hojas de Trámite

#### **Características:**
- ✅ Generación automática de números de HT
- ✅ Formato: AñoNúmeros (ej: 20250235689)
- ✅ Vinculación 1:1 con documento de entrada
- ✅ Autocarga de HT en registro de salida
- ✅ Campo editable manualmente
- ✅ Validación de formato (solo números)

#### **Flujo de trabajo:**
```
1. Usuario registra documento de entrada
2. Sistema genera HT automáticamente (formato: año+secuencia)
3. Al registrar salida, HT se carga automáticamente
4. Usuario puede editar HT si es necesario
5. HT se guarda en registro de salida
```

#### **Endpoints principales:**
```
POST /api/documentos/hojas-tramite          # Crear HT
GET  /api/documentos/hojas-tramite/documento/{id}  # Obtener HT por documento
```

### 5.4 Módulo de Derivaciones

#### **Características:**
- ✅ Derivación de documentos entre áreas/usuarios
- ✅ Sistema de prioridades (BAJA, MEDIA, ALTA, URGENTE)
- ✅ Campos de instrucciones y observaciones
- ✅ Seguimiento de estado de derivación
- ✅ Notificaciones automáticas al destinatario
- ✅ Historial completo de derivaciones

#### **Estados de derivación:**
- **Pendiente:** Derivación creada, esperando aceptación
- **Aceptada:** Destinatario acepta la derivación
- **Rechazada:** Destinatario rechaza con motivo
- **En_Proceso:** Documento en procesamiento
- **Finalizada:** Derivación completada

#### **Endpoints principales:**
```
POST /api/derivaciones                     # Crear derivación
GET  /api/derivaciones/usuario/{id}        # Derivaciones de usuario
GET  /api/derivaciones/pendientes          # Derivaciones pendientes
PUT  /api/derivaciones/{id}/aceptar        # Aceptar derivación
PUT  /api/derivaciones/{id}/rechazar       # Rechazar derivación
```

### 5.5 Módulo de Salida de Documentos

#### **Características:**
- ✅ Registro de salida con destino externo
- ✅ Generación automática de código de salida (SALIDA-XXXXXX)
- ✅ Campos: destinatario, cargo, dependencia, motivo
- ✅ Autocarga de información del documento
- ✅ Autocarga de HT si existe
- ✅ Registro de usuario y fecha de salida

#### **Endpoints principales:**
```
POST /api/salidas-documento              # Registrar salida
GET  /api/salidas-documento              # Listar salidas
GET  /api/salidas-documento/{id}         # Detalle de salida
```

### 5.6 Módulo de Reportes y Estadísticas

#### **Características:**
- ✅ Dashboard con métricas en tiempo real
- ✅ Gráficos de documentos por estado
- ✅ Gráficos de documentos por tipo
- ✅ Estadísticas de derivaciones
- ✅ Reporte de productividad por usuario
- ✅ Exportación a PDF y Excel
- ✅ Filtros por rango de fechas

#### **Métricas del dashboard:**
- Total de documentos en el sistema
- Documentos por estado (con porcentajes)
- Derivaciones pendientes
- Documentos procesados hoy
- Tiempo promedio de procesamiento
- Top usuarios más activos

#### **Endpoints principales:**
```
GET /api/reportes/dashboard                # Datos del dashboard
GET /api/reportes/estadisticas             # Estadísticas generales
GET /api/reportes/documentos-por-estado    # Reporte por estado
GET /api/reportes/documentos-por-tipo      # Reporte por tipo
GET /api/reportes/exportar/pdf             # Exportar reporte PDF
GET /api/reportes/exportar/excel           # Exportar reporte Excel
```

### 5.7 Módulo de Bitácora y Auditoría

#### **Características:**
- ✅ Registro automático de TODAS las operaciones
- ✅ Captura de IP de usuario
- ✅ Timestamp con milisegundos
- ✅ Detalles completos de cada acción
- ✅ Búsqueda y filtrado avanzado
- ✅ Exportación a PDF y Excel
- ✅ Visualización en tabla paginada

#### **Tipos de acciones registradas:**
- LOGIN / LOGOUT
- REGISTRO_DOCUMENTO / EDICION_DOCUMENTO / ELIMINACION_DOCUMENTO
- DERIVACION / ACEPTACION_DERIVACION / RECHAZO_DERIVACION
- CAMBIO_ESTADO_DOCUMENTO
- REGISTRO_SALIDA
- CREACION_USUARIO / EDICION_USUARIO
- BACKUP_REALIZADO

#### **Endpoints principales:**
```
GET  /api/bitacora                      # Listar bitácora (paginada)
GET  /api/bitacora/exportar/pdf         # Exportar bitácora PDF
GET  /api/bitacora/exportar/excel       # Exportar bitácora Excel
```

### 5.8 Módulo de Notificaciones

#### **Características:**
- ✅ Notificaciones en tiempo real
- ✅ Tipos: DERIVACION, ASIGNACION, CAMBIO_ESTADO, ALERTA
- ✅ Badge con contador de notificaciones no leídas
- ✅ Marcar como leída
- ✅ Marcar todas como leídas
- ✅ Redirección automática al documento relacionado

#### **Endpoints principales:**
```
GET  /api/notificaciones/usuario/{id}          # Notificaciones del usuario
GET  /api/notificaciones/no-leidas/{id}        # Notificaciones no leídas
PUT  /api/notificaciones/{id}/marcar-leida     # Marcar como leída
PUT  /api/notificaciones/marcar-todas-leidas   # Marcar todas como leídas
```

### 5.9 Módulo de Gestión de Usuarios (Admin)

#### **Características:**
- ✅ CRUD completo de usuarios
- ✅ Asignación de roles
- ✅ Activación/desactivación de cuentas
- ✅ Cambio de contraseñas
- ✅ Búsqueda y filtrado de usuarios
- ✅ Visualización de usuarios por área

#### **Endpoints principales:**
```
GET    /api/usuarios                 # Listar usuarios
POST   /api/usuarios                 # Crear usuario
PUT    /api/usuarios/{id}            # Actualizar usuario
DELETE /api/usuarios/{id}            # Eliminar usuario
PUT    /api/usuarios/{id}/roles      # Asignar roles
```

### 5.10 Módulo de Backups Automáticos

#### **Características:**
- ✅ Backups automáticos programados (2:00 AM diario)
- ✅ Backup manual bajo demanda
- ✅ Retención configurable (30 días por defecto)
- ✅ Eliminación automática de backups antiguos
- ✅ Compresión de archivos
- ✅ Logs de respaldo

#### **Configuración:**
```properties
backup.enabled=true
backup.directory=../backups
backup.schedule=0 0 2 * * ?          # CRON: 2:00 AM diario
backup.retention.days=30
```

#### **Endpoints principales:**
```
POST /api/backup/manual              # Ejecutar backup manual
GET  /api/backup/listar              # Listar backups disponibles
POST /api/backup/restaurar/{archivo} # Restaurar backup
```

---

## 6. SEGURIDAD

### 6.1 Autenticación y Autorización

#### **JWT (JSON Web Tokens):**
- Token firmado con algoritmo HMAC-SHA512
- Clave secreta configurada en `application.properties`
- Expiración: 8 horas (28800000 ms)
- Claims: username, roles, fecha de emisión

#### **Control de Acceso Basado en Roles:**
```java
@PreAuthorize("hasRole('Administrador')")        // Solo Admin
@PreAuthorize("hasAnyRole('Administrador', 'Jefatura')")  // Admin o Jefatura
@PreAuthorize("isAuthenticated()")               // Cualquier usuario autenticado
```

#### **Permisos por rol:**

| Funcionalidad | Administrador | Jefatura | Digitador | Usuario |
|---------------|---------------|----------|-----------|---------|
| Gestión de usuarios | ✅ | ❌ | ❌ | ❌ |
| Ver bitácora | ✅ | ✅ | ❌ | ❌ |
| Exportar reportes | ✅ | ✅ | ✅ | ❌ |
| Registrar documentos | ✅ | ✅ | ✅ | ❌ |
| Derivar documentos | ✅ | ✅ | ✅ | ✅ |
| Ver documentos asignados | ✅ | ✅ | ✅ | ✅ |
| Cambiar estado documentos | ✅ | ✅ | ✅ | ✅ |

### 6.2 Protección de Datos

#### **Hash de contraseñas:**
- Algoritmo: BCrypt con salt aleatorio
- Factor de costo: 10 rondas
- Nunca se almacenan contraseñas en texto plano

#### **Validaciones:**
- Validación de entrada en backend con Bean Validation
- Sanitización de datos para prevenir SQL Injection
- Validación de tipos de archivo permitidos
- Límite de tamaño de archivos (10 MB)

#### **CORS (Cross-Origin Resource Sharing):**
```properties
mesadepartes.app.allowedOrigins=http://localhost:5500,http://127.0.0.1:5500
```

### 6.3 Auditoría

- **Bitácora completa:** Registro de todas las operaciones
- **Captura de IP:** Identificación del origen de cada acción
- **Timestamps precisos:** Fecha y hora con milisegundos
- **No editable:** Bitácora de solo lectura para integridad

---

## 7. API REST

### 7.1 Especificaciones

- **Protocolo:** HTTP/HTTPS
- **Formato:** JSON
- **Autenticación:** Bearer Token (JWT)
- **Base URL:** `http://localhost:8080/api`
- **Documentación:** Swagger UI en `/swagger-ui.html`

### 7.2 Convenciones

#### **Estructura de respuesta exitosa:**
```json
{
  "idDocumento": 1,
  "codigo": "DOC-000001",
  "titulo": "Documento de ejemplo",
  "estado": "Asignado"
}
```

#### **Estructura de respuesta con error:**
```json
{
  "timestamp": "2025-12-03T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El campo 'titulo' es obligatorio",
  "path": "/api/documentos"
}
```

#### **Códigos HTTP utilizados:**
- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado exitosamente
- `400 Bad Request` - Error en los datos enviados
- `401 Unauthorized` - No autenticado o token inválido
- `403 Forbidden` - Sin permisos para la operación
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error interno del servidor

### 7.3 Autenticación de Requests

Todas las peticiones (excepto `/api/auth/login` y `/api/auth/registro`) requieren token JWT:

```http
GET /api/documentos HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYWt1c3UiLCJyb2xlcyI6WyJBZG1pbmlzdHJhZG9yIl0sImlhdCI6MTczMzIzMjAwMCwiZXhwIjoxNzMzMjYwODAwfQ...
Content-Type: application/json
```

---

## 8. FLUJOS DE TRABAJO PRINCIPALES

### 8.1 Flujo: Registro de Documento de Entrada

```
┌──────────────┐
│  1. Usuario  │
│   Digitador  │
│  inicia      │
│  sesión      │
└──────┬───────┘
       │
       ↓
┌──────────────────────────────────┐
│  2. Accede a "Registrar         │
│     Documento"                   │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  3. Llena formulario:            │
│     - Tipo de documento          │
│     - Título                     │
│     - Remitente                  │
│     - Descripción                │
│     - Archivo adjunto (opcional) │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  4. Sistema genera:              │
│     - Código único (DOC-XXXXXX)  │
│     - Hoja de Trámite (HT)       │
│     - Fecha de ingreso           │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  5. Documento queda en estado    │
│     "Asignado"                   │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  6. Sistema registra en bitácora │
│     Acción: REGISTRO_DOCUMENTO   │
└──────────────────────────────────┘
```

### 8.2 Flujo: Derivación de Documento

```
┌──────────────┐
│  1. Usuario  │
│  con doc     │
│  asignado    │
└──────┬───────┘
       │
       ↓
┌──────────────────────────────────┐
│  2. Selecciona "Derivar"         │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  3. Llena formulario derivación: │
│     - Usuario destino            │
│     - Prioridad                  │
│     - Instrucciones              │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  4. Sistema crea derivación      │
│     Estado: "Pendiente"          │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  5. Sistema envía notificación   │
│     al usuario destino           │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  6. Usuario destino recibe       │
│     notificación                 │
└──────┬───────────────────────────┘
       │
       ├──────────┬──────────┐
       │          │          │
       ↓          ↓          ↓
┌──────────┐ ┌──────────┐ ┌──────────┐
│ Acepta   │ │ Rechaza  │ │ Ignora   │
└────┬─────┘ └────┬─────┘ └────┬─────┘
     │            │            │
     ↓            ↓            ↓
Estado:      Estado:      Estado:
"Aceptada"   "Rechazada"  "Pendiente"
```

### 8.3 Flujo: Registro de Salida de Documento

```
┌──────────────┐
│  1. Usuario  │
│  busca doc   │
│  por código  │
└──────┬───────┘
       │
       ↓
┌──────────────────────────────────┐
│  2. Sistema carga:               │
│     - Información del documento  │
│     - HT automáticamente (si hay)│
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  3. Usuario completa:            │
│     - Destinatario externo       │
│     - Cargo                      │
│     - Dependencia                │
│     - Motivo de salida           │
│     - HT (editable)              │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  4. Sistema genera código:       │
│     SALIDA-XXXXXX                │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  5. Documento cambia a estado    │
│     "Salida"                     │
└──────┬───────────────────────────┘
       │
       ↓
┌──────────────────────────────────┐
│  6. Registro en bitácora         │
│     Acción: REGISTRO_SALIDA      │
└──────────────────────────────────┘
```

---

## 9. DESPLIEGUE Y CONFIGURACIÓN

### 9.1 Requisitos del Sistema

#### **Servidor de Aplicación:**
- **Sistema Operativo:** Windows Server 2019+, Linux (Ubuntu 20.04+), macOS
- **Java Runtime:** OpenJDK 21 LTS o superior
- **RAM:** Mínimo 2 GB, recomendado 4 GB
- **Almacenamiento:** Mínimo 10 GB (para aplicación y uploads)
- **Conectividad:** Puerto 8080 abierto para HTTP

#### **Base de Datos:**
- **MySQL:** 8.0 o superior
- **RAM:** Mínimo 2 GB dedicados
- **Almacenamiento:** Mínimo 5 GB (escalable según volumen)

#### **Cliente (Navegador):**
- Google Chrome 90+
- Mozilla Firefox 88+
- Microsoft Edge 90+
- Safari 14+

### 9.2 Instalación Local

#### **Paso 1: Base de Datos**
```sql
-- Ejecutar script de base de datos
mysql -u root -p < SQL/mesa_partes_db_completa_actualizada.sql
```

#### **Paso 2: Configuración Backend**
```properties
# Editar: backend/src/main/resources/application.properties

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/mesa_partes_db
spring.datasource.username=root
spring.datasource.password=tu_contraseña

# Puerto del servidor
server.port=8080

# JWT Secret (cambiar en producción)
mesadepartes.app.jwtSecret=TuClaveSecretaMuySegura
```

#### **Paso 3: Compilar y Ejecutar Backend**
```bash
cd backend
mvnw clean package
java -jar target/mesadepartes-0.0.1-SNAPSHOT.jar
```

O usar el batch de Windows:
```cmd
cd backend
start-app.bat
```

#### **Paso 4: Configurar Frontend**
```javascript
// Editar: frontend/assets/js/core/config.js

const API_URL = 'http://localhost:8080/api';
```

#### **Paso 5: Servir Frontend**
```bash
# Opción 1: Live Server (VS Code)
# Click derecho en index.html -> Open with Live Server

# Opción 2: Python
cd frontend
python -m http.server 5500

# Opción 3: Node.js
npx http-server -p 5500
```

#### **Paso 6: Acceso al Sistema**
```
URL: http://localhost:5500/pages/auth/login.html
Usuario por defecto: nakusu
Contraseña: 123456
```

### 9.3 Despliegue en Producción

#### **Railway (Recomendado para Demo):**
1. Crear cuenta en Railway.app
2. Conectar repositorio de GitHub
3. Configurar variables de entorno
4. Desplegar automáticamente

**Variables de entorno necesarias:**
```env
DB_HOST=containers-us-west-xxx.railway.app
DB_PORT=3306
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=xxxxx
JWT_SECRET=ClaveSecretaProductiva
ALLOWED_ORIGINS=https://tupagina.com
```

#### **Configuración SSL/HTTPS:**
```properties
# application-production.properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

### 9.4 Scripts de Utilidad

#### **Windows:**
- `iniciar-backend.bat` - Inicia el servidor backend
- `preparar-railway.bat` - Prepara el proyecto para Railway
- `verificar-railway.bat` - Verifica la configuración de Railway
- `scripts/backup_windows.bat` - Ejecuta backup manual de BD
- `scripts/restaurar_backup_windows.bat` - Restaura un backup

#### **Linux:**
- `scripts/backup_linux.sh` - Backup manual de BD en Linux

---

## 10. MONITOREO Y MANTENIMIENTO

### 10.1 Spring Boot Actuator

Endpoints de monitoreo disponibles en `/actuator`:

```
GET /actuator/health          # Estado de salud de la aplicación
GET /actuator/info            # Información de la aplicación
GET /actuator/metrics         # Métricas de rendimiento
```

### 10.2 Logs del Sistema

#### **Configuración de logging:**
```properties
# Nivel de logs
logging.level.root=INFO
logging.level.com.pnp.mesadepartes=DEBUG

# Archivo de logs
logging.file.name=logs/mesa-partes.log

# Rotación de logs
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
```

#### **Ubicación de logs:**
- Aplicación: `logs/mesa-partes.log`
- Errores: Incluidos en el log principal con nivel ERROR
- Backups: `backups/backup-YYYYMMDD-HHMMSS.log`

### 10.3 Backups

#### **Backups automáticos:**
- Frecuencia: Diaria a las 2:00 AM
- Retención: 30 días
- Ubicación: `../backups/`
- Formato: `backup-mesa-partes-YYYYMMDD-HHMMSS.sql`

#### **Backup manual:**
```bash
# Windows
cd scripts
backup_windows.bat

# Linux
cd scripts
./backup_linux.sh
```

#### **Restaurar backup:**
```bash
# Windows
cd scripts
restaurar_backup_windows.bat nombre_archivo.sql

# Linux
mysql -u root -p mesa_partes_db < ../backups/nombre_archivo.sql
```

---

## 11. RENDIMIENTO Y ESCALABILIDAD

### 11.1 Optimizaciones Implementadas

#### **Base de Datos:**
- ✅ Índices en campos de búsqueda frecuente (username, email, codigo)
- ✅ Motor InnoDB para transacciones ACID
- ✅ Charset utf8mb4 para soporte Unicode completo
- ✅ Queries optimizadas con JPQL y @Query

#### **Backend:**
- ✅ Connection pooling de HikariCP (por defecto en Spring Boot)
- ✅ Lazy loading de relaciones JPA
- ✅ DTOs para evitar serialización de entidades completas
- ✅ Cache de configuración en memoria

#### **Frontend:**
- ✅ Carga asíncrona con Fetch API
- ✅ Minimización de requests con batching
- ✅ Cache de datos estáticos en localStorage
- ✅ Event delegation para reducir listeners

### 11.2 Métricas de Rendimiento

#### **Tiempos de respuesta estimados (red local):**
- Login: < 200 ms
- Listar documentos (página de 20): < 300 ms
- Buscar documento por código: < 150 ms
- Crear documento: < 250 ms
- Generar reporte PDF: < 2 segundos
- Generar reporte Excel: < 3 segundos

#### **Capacidad estimada:**
- Usuarios concurrentes: 50-100 (con 4 GB RAM)
- Documentos en BD: 100,000+ sin degradación
- Uploads simultáneos: 10-20 (según ancho de banda)

### 11.3 Escalabilidad

#### **Escalamiento vertical (mejorar hardware):**
- Aumentar RAM: Mejora cache y conexiones concurrentes
- CPU más rápida: Reduce tiempo de procesamiento de reportes
- SSD: Mejora tiempos de lectura/escritura de archivos

#### **Escalamiento horizontal (preparación futura):**
- Load balancer con múltiples instancias del backend
- Base de datos en clúster o réplicas
- Almacenamiento de archivos en S3 o similar
- Cache distribuido con Redis

---

## 12. RESOLUCIÓN DE PROBLEMAS

### 12.1 Problemas Comunes

#### **Error: "Cannot connect to database"**
- **Causa:** MySQL no está corriendo o credenciales incorrectas
- **Solución:** 
  ```bash
  # Verificar que MySQL esté corriendo
  mysql -u root -p
  
  # Revisar application.properties
  spring.datasource.url=jdbc:mysql://localhost:3306/mesa_partes_db
  spring.datasource.username=root
  spring.datasource.password=tu_contraseña
  ```

#### **Error: "401 Unauthorized" en todas las peticiones**
- **Causa:** Token JWT expirado o inválido
- **Solución:**
  ```javascript
  // Hacer logout y login nuevamente
  localStorage.removeItem('token');
  window.location.href = '/pages/auth/login.html';
  ```

#### **Error: "CORS policy blocked"**
- **Causa:** Origen no permitido en configuración
- **Solución:**
  ```properties
  # Agregar origen en application.properties
  mesadepartes.app.allowedOrigins=http://localhost:5500,http://127.0.0.1:5500
  ```

#### **Error: "FileNotFoundException" al subir archivo**
- **Causa:** Directorio `uploads/` no existe
- **Solución:**
  ```bash
  mkdir -p backend/uploads/documentos
  mkdir -p backend/uploads/cargos
  ```

#### **Error 400 en endpoint de HT**
- **Causa:** Entidad HojaTramite con FetchType.LAZY causa error de serialización
- **Solución:** Ya implementada con `@JsonIgnoreProperties`

### 12.2 Logs de Depuración

#### **Activar logs detallados:**
```properties
# application.properties
logging.level.com.pnp.mesadepartes=TRACE
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

#### **Revisar logs en tiempo real:**
```bash
# Windows
type logs\mesa-partes.log

# Linux
tail -f logs/mesa-partes.log
```

---

## 13. MEJORAS FUTURAS

### 13.1 Corto Plazo (1-3 meses)

- [ ] **Notificaciones push en tiempo real** con WebSockets
- [ ] **Firma digital de documentos** con certificados digitales
- [ ] **Escáner de documentos integrado** desde navegador
- [ ] **Búsqueda full-text** con Elasticsearch
- [ ] **App móvil nativa** (Android/iOS)

### 13.2 Mediano Plazo (3-6 meses)

- [ ] **Integración con RENIEC** para validación de identidad
- [ ] **Reconocimiento OCR** de documentos escaneados
- [ ] **Flujos de aprobación multinivel** configurables
- [ ] **Dashboard ejecutivo** con BI integrado
- [ ] **API pública** para integraciones externas

### 13.3 Largo Plazo (6-12 meses)

- [ ] **Inteligencia artificial** para clasificación automática de documentos
- [ ] **Blockchain** para trazabilidad inmutable
- [ ] **Multitenancy** para uso en múltiples dependencias PNP
- [ ] **Integración con Sistema de Gestión de Expedientes**
- [ ] **Módulo de correspondencia electrónica** con otras instituciones

---

## 14. CONCLUSIONES

### 14.1 Logros del Proyecto

El Sistema de Mesa de Partes Digital representa un avance significativo en la modernización de procesos administrativos de la PNP, logrando:

✅ **Digitalización completa** del flujo documental  
✅ **Trazabilidad total** de documentos desde ingreso hasta salida  
✅ **Reducción de tiempos** de procesamiento y búsqueda  
✅ **Auditoría exhaustiva** de todas las operaciones  
✅ **Acceso remoto** desde cualquier ubicación  
✅ **Reportes en tiempo real** para toma de decisiones  
✅ **Seguridad robusta** con autenticación y control de acceso  

### 14.2 Impacto Esperado

- **Eficiencia operativa:** Reducción del 70% en tiempos de registro y búsqueda
- **Transparencia:** Visibilidad completa del estado de documentos
- **Ahorro de recursos:** Reducción de papel, impresiones y archivos físicos
- **Mejora en servicio:** Atención más rápida a consultas y derivaciones
- **Cumplimiento normativo:** Registro auditable de todas las operaciones

### 14.3 Recomendaciones

1. **Capacitación continua** del personal en el uso del sistema
2. **Monitoreo regular** de logs y métricas de rendimiento
3. **Backups frecuentes** y pruebas de restauración
4. **Actualizaciones periódicas** de dependencias y parches de seguridad
5. **Feedback constante** de usuarios para mejoras iterativas

---

## 15. ANEXOS

### 15.1 Glosario de Términos

- **JWT:** JSON Web Token - Token de autenticación basado en JSON
- **REST:** Representational State Transfer - Arquitectura de servicios web
- **CORS:** Cross-Origin Resource Sharing - Compartir recursos entre orígenes
- **JPA:** Java Persistence API - API de persistencia de Java
- **DTO:** Data Transfer Object - Objeto de transferencia de datos
- **CRUD:** Create, Read, Update, Delete - Operaciones básicas de BD
- **BCrypt:** Algoritmo de hash de contraseñas
- **ORM:** Object-Relational Mapping - Mapeo objeto-relacional
- **API:** Application Programming Interface - Interfaz de programación

### 15.2 Referencias

- [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación MySQL](https://dev.mysql.com/doc/)
- [JWT.io](https://jwt.io/) - Información sobre JSON Web Tokens
- [MDN Web Docs](https://developer.mozilla.org/) - Referencia de JavaScript/HTML/CSS

### 15.3 Información de Contacto

**Equipo de Desarrollo:**  
Proyecto Mesa de Partes PNP  
Email: soporte@mesadepartes.pnp.gob.pe  
GitHub: https://github.com/Nakusuo/ProyectoMesaDePartes

---

**DOCUMENTO GENERADO AUTOMÁTICAMENTE**  
**Fecha:** 3 de Diciembre de 2025  
**Versión:** 1.0  
**Estado:** COMPLETO
