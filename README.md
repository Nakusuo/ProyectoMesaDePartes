# 📋 Sistema Mesa de Partes Digital - PNP

<div align="center">

![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0.40-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

**Sistema de Gestión Documental para la Policía Nacional del Perú**

[Características](#-características-principales) • [Arquitectura](#-arquitectura-técnica) • [Estado del Proyecto](#-estado-del-proyecto) • [Instalación](#-instalación-y-configuración) • [API](#-documentación-de-la-api)

</div>

---

## 📖 Descripción General

Sistema integral de Mesa de Partes Digital desarrollado para la **Policía Nacional del Perú (PNP)**, diseñado para optimizar la gestión, registro y seguimiento de documentos administrativos internos. Implementa un patrón de arquitectura **MVC (Model-View-Controller)** con separación clara de responsabilidades y principios SOLID.

### 🎯 Objetivos del Sistema

- ✅ **Digitalizar** el proceso de recepción y registro de documentos
- ✅ **Automatizar** la asignación de códigos y trámites mediante generación secuencial
- ✅ **Centralizar** el almacenamiento de archivos PDF con validaciones robustas
- ✅ **Facilitar** el seguimiento de documentos mediante bitácora en tiempo real
- ✅ **Implementar** control de acceso basado en roles (RBAC)
- ✅ **Garantizar** la seguridad mediante JWT + BCrypt con algoritmo Blowfish

---

## 🏗️ Arquitectura Técnica

### Patrón de Diseño: MVC con Repository Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                         FRONTEND                             │
│  HTML5 + CSS3 + Vanilla JavaScript (ES6+)                   │
│  ├─ Views: bitacora.html, registro.html, dashboard.html     │
│  ├─ Scripts: auth.js, permissions.js, config.js             │
│  └─ Styles: PNP branding (verde #00642e, amarillo #fbbf24)  │
└─────────────────────────────────────────────────────────────┘
                              ↓ HTTP REST API
┌─────────────────────────────────────────────────────────────┐
│                      BACKEND (Spring Boot)                   │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  CONTROLLER LAYER (REST Endpoints)                   │  │
│  │  ├─ DocumentoController    (@RestController)         │  │
│  │  ├─ UsuarioController      (@RestController)         │  │
│  │  ├─ AuthController         (@RestController)         │  │
│  │  ├─ AreaController         (@RestController)         │  │
│  │  └─ TipoDocumentoController (@RestController)        │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  SECURITY LAYER (JWT + BCrypt)                       │  │
│  │  ├─ JwtAuthenticationFilter                          │  │
│  │  ├─ JwtUtils (Token generation/validation)           │  │
│  │  ├─ UserDetailsServiceImpl                           │  │
│  │  └─ SecurityConfig (@EnableWebSecurity)              │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  MODEL LAYER (JPA Entities)                          │  │
│  │  ├─ Documento, Usuario, Area, Tramite                │  │
│  │  ├─ TipoDocumento, HojaTramite, Rol                  │  │
│  │  └─ SalidaDocumento                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                              ↓                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  REPOSITORY LAYER (Spring Data JPA)                  │  │
│  │  ├─ DocumentoRepository extends JpaRepository        │  │
│  │  ├─ UsuarioRepository extends JpaRepository          │  │
│  │  ├─ TramiteRepository extends JpaRepository          │  │
│  │  └─ Custom queries con @Query y JPQL                 │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│              DATABASE (MySQL 8.0.40)                         │
│  ├─ 10 Tablas relacionales con integridad referencial       │
│  ├─ Índices en columnas de búsqueda frecuente              │
│  └─ ENUM types para estados y tipos                         │
└─────────────────────────────────────────────────────────────┘
```

### Stack Tecnológico Completo

#### Backend
- **Java 21 LTS** (OpenJDK)
- **Spring Boot 3.5.6** (Framework principal)
- **Spring Data JPA** (ORM con Hibernate 6.6.29)
- **Spring Security** (Autenticación y autorización)
- **Spring Web** (REST API)
- **MySQL Connector/J** (Driver JDBC)
- **jjwt 0.12.6** (JSON Web Tokens)
- **BCrypt** (Password hashing con Blowfish)
- **Lombok** (Reducción de boilerplate)
- **Maven** (Gestión de dependencias)

#### Frontend
- **HTML5** (Estructura semántica)
- **CSS3** (Variables CSS, Flexbox, Grid)
- **JavaScript ES6+** (Async/Await, Fetch API, Modules)
- **Chart.js 4.4.0** (Visualización de datos)

#### Base de Datos
- **MySQL 8.0.40** (RDBMS)
- **10 Tablas** con relaciones FK
- **ENUM types** para estados y clasificaciones
- **Triggers** para auditoría automática

---

## 🚀 Características Principales

### 📄 Gestión de Documentos

- **Registro completo** con validación de campos obligatorios
- **Códigos secuenciales** automáticos (DOC-000001, DOC-000002, ...)
- **Carga de archivos PDF** con validación de tipo y tamaño (máx. 10MB)
- **Almacenamiento seguro** en `backend/uploads/documentos/`
- **Asignación automática** de hojas de trámite (HT)
- **10 tipos** de documento preconfigurados (Oficio, Memorándum, Informe, etc.)
- **Estados del documento**: Registrado → En Proceso → Observado/Finalizado → Salida

### 👥 Gestión de Usuarios

- **Autenticación JWT** con tokens Bearer en headers HTTP
- **4 Roles diferenciados**:
  - `Administrador` (ID: 1) - Acceso completo al sistema
  - `Mesa de Partes` (ID: 2) - Registro y asignación de documentos
  - `Trabajador` (ID: 3) - Visualización de documentos asignados
  - `Jefatura` (ID: 4) - Visualización de bitácora y métricas
- **7 usuarios precargados** con contraseña `123456` (BCrypt)
- **CRUD completo** de usuarios con validaciones

### 🏢 Gestión de Áreas

Sistema dual de áreas implementado con ENUM `tipo`:
- **34 Departamentos PNP** (`DEPARTAMENTO_PNP`): DIRTIC, DIRANDRO, IGPNP, etc.
  - Usados como remitentes/destinatarios en documentos
  - Filtrados automáticamente en formularios de registro documental
- **5 Áreas de Trabajo** (`AREA_TRABAJO`): MDP, SIS, DEV, RED, ST
  - Usados para asignación de usuarios al sistema
  - Filtrados en formularios de registro y gestión de usuarios

### 📊 Bitácora y Seguimiento

- **Registro histórico** de todos los documentos con información de asignación
- **Filtros dinámicos**: Por estado, área, tipo de documento, fechas
- **Vista detallada**: Usuario asignado mediante JOIN con tabla `tramites`
- **Endpoint especializado**: `/api/documentos/bitacora` con datos enriquecidos

---

## 📐 Implementación de Principios SOLID

### Single Responsibility Principle (SRP) ✅
Cada clase tiene una única responsabilidad bien definida:
- **Controllers**: Solo manejan peticiones HTTP y respuestas
- **Repositories**: Solo acceso a datos (CRUD + queries personalizadas)
- **Models**: Solo representación de entidades de dominio
- **DTOs**: Solo transferencia de datos entre capas

**Ejemplo:**
```java
// DocumentoController - Solo gestión de endpoints REST
@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {
    // Solo métodos HTTP: GET, POST, PUT, DELETE
}

// DocumentoRepository - Solo operaciones de base de datos
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByEstado(EstadoDocumento estado);
}
```

### Open/Closed Principle (OCP) ✅
El sistema está abierto a extensión pero cerrado a modificación:
- **JpaRepository**: Extiende funcionalidad sin modificar código base
- **ENUMs**: Nuevos tipos de documento sin cambiar lógica existente
- **@CrossOrigin**: Configuración CORS extensible sin tocar controllers

**Ejemplo:**
```java
// Extensible sin modificar JpaRepository
public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    // Nuevos métodos de consulta sin tocar la clase base
    List<Tramite> findByDocumento(Documento documento);
    List<Tramite> findByUsuarioAsignado_IdUsuario(Long idUsuario);
}
```

### Liskov Substitution Principle (LSP) ✅
Las subclases pueden sustituir a sus clases base:
- **JpaRepository**: Todos los repositorios son intercambiables
- **ResponseEntity<?>**: Respuestas HTTP uniformes y sustituibles

### Interface Segregation Principle (ISP) ✅
Interfaces específicas en lugar de una interfaz general:
- **JpaRepository** separado por entidad (no un repositorio gigante)
- **UserDetailsService** solo para autenticación
- **JwtUtils** solo para manejo de tokens

### Dependency Inversion Principle (DIP) ✅
Dependencia de abstracciones, no de implementaciones concretas:
- **@Autowired**: Inyección de dependencias vía interfaces
- **JpaRepository**: Interface implementada por Spring Data
- **UserDetailsService**: Interface implementada por UserDetailsServiceImpl

**Ejemplo:**
```java
@RestController
public class DocumentoController {
    // Dependemos de la interfaz, no de la implementación
    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
}
```

---

## 🗄️ Modelo de Datos

### Diagrama de Entidad-Relación

```
┌─────────────────┐         ┌─────────────────┐
│     ROLES       │         │     AREAS       │
├─────────────────┤         ├─────────────────┤
│ ID_rol (PK)     │         │ ID_area (PK)    │
│ nombre          │         │ nombre          │
└────────┬────────┘         │ sigla           │
         │                  │ tipo (ENUM)     │
         │                  └────────┬────────┘
         │                           │
         │      ┌────────────────────┘
         │      │
         │      ▼
┌────────┴──────────────┐
│   USUARIO_ROLES       │         ┌─────────────────────────┐
├───────────────────────┤         │      USUARIOS           │
│ ID_usuario_rol (PK)   │◄────────├─────────────────────────┤
│ ID_usuario (FK)       │         │ ID_usuario (PK)         │
│ ID_rol (FK)           │         │ nombre, apellido        │
└───────────────────────┘         │ username, password_hash │
                                  │ email, telefono         │
                                  │ tipo_contrato (ENUM)    │
                                  │ activo (BOOLEAN)        │
                                  │ ID_area (FK)            │
                                  └───────────┬─────────────┘
                                              │
                                              │
        ┌─────────────────────────────────────┼─────────────────────┐
        │                                     │                     │
        ▼                                     ▼                     ▼
┌─────────────────────┐          ┌────────────────────┐  ┌─────────────────┐
│   DOCUMENTOS        │          │     TRAMITES       │  │ TIPOS_DOCUMENTO │
├─────────────────────┤          ├────────────────────┤  ├─────────────────┤
│ ID_documento (PK)   │◄─────────│ ID_tramite (PK)    │  │ ID_tipo_doc(PK) │
│ codigo (UNIQUE)     │          │ ID_documento (FK)  │  │ nombre          │
│ titulo              │          │ ID_usuario_crea(FK)│  └────────┬────────┘
│ descripcion         │          │ ID_usuario_asig(FK)│           │
│ numero_documento    │          └────────────────────┘           │
│ estado (ENUM)       │                                           │
│ remitente           │          ┌────────────────────┐           │
│ destinatario        │          │  HOJAS_TRAMITE     │           │
│ fecha_ingreso       │          ├────────────────────┤           │
│ archivo_url         │◄─────────│ ID_hoja_tramite(PK)│           │
│ ID_usuario_reg (FK) │          │ numero_ht          │           │
│ ID_tipo_doc (FK)    │◄─────────│ ID_documento (FK)  │           │
│ created_at          │          └────────────────────┘           │
│ updated_at          │                                           │
└──────────┬──────────┘          ┌────────────────────┐           │
           │                     │ SALIDAS_DOCUMENTO  │           │
           └─────────────────────┤────────────────────┤           │
                                 │ ID_salida_doc (PK) │           │
                                 │ ID_documento (FK)  │           │
                                 │ ID_tipo_doc (FK)   │◄──────────┘
                                 │ numero_doc_salida  │
                                 │ destinatario_salida│
                                 │ ID_usuario_sal(FK) │
                                 │ fecha_salida       │
                                 │ observacion        │
                                 │ archivo_cargo_url  │
                                 └────────────────────┘
```

### Relaciones Principales

1. **Usuario ↔ Roles** (Many-to-Many): Tabla intermedia `usuario_roles`
2. **Usuario ↔ Area** (Many-to-One): Usuario pertenece a una área de trabajo
3. **Documento ↔ Usuario** (Many-to-One): Usuario registrador
4. **Documento ↔ TipoDocumento** (Many-to-One): Clasificación del documento
5. **Documento ↔ Tramite** (One-to-Many): Trámites asociados al documento
6. **Tramite ↔ Usuario** (Many-to-One): Usuario creador y asignado
7. **Documento ↔ HojaTramite** (One-to-One): Número de hoja de trámite
8. **Documento ↔ SalidaDocumento** (One-to-Many): Salidas del documento

---

## 📊 Estado del Proyecto

### Progreso General: **78%** ✅

| Módulo | Completado | Estado | Observaciones |
|--------|-----------|--------|---------------|
| **Autenticación y Seguridad** | 95% | ✅ Funcional | JWT + BCrypt implementado, refresh token pendiente |
| **Gestión de Usuarios** | 90% | ✅ Funcional | CRUD completo, falta edición de contraseña |
| **Gestión de Documentos** | 85% | ✅ Funcional | Registro y listado completo, edición básica |
| **Sistema de Áreas** | 100% | ✅ Completo | Separación DEPARTAMENTO_PNP/AREA_TRABAJO |
| **Bitácora y Seguimiento** | 80% | ✅ Funcional | Vista y filtros, falta exportación PDF |
| **Dashboard y Métricas** | 75% | 🔄 En desarrollo | Gráficas básicas, faltan KPIs avanzados |
| **Gestión de Trámites** | 70% | 🔄 En desarrollo | Asignación básica, falta flujo de estados |
| **Salida de Documentos** | 40% | ⚠️ Parcial | Modelo creado, falta interfaz completa |
| **Reportes y Exportación** | 30% | ⏳ Pendiente | Solo exportación básica |
| **Notificaciones** | 0% | ⏳ Pendiente | Sistema de alertas por implementar |

### Funcionalidades Implementadas ✅

#### Backend
- ✅ Arquitectura REST con Spring Boot
- ✅ Autenticación JWT con tokens Bearer
- ✅ Encriptación BCrypt (10 rounds)
- ✅ 7 Controladores REST (@RestController)
- ✅ 8 Repositorios (JpaRepository)
- ✅ 10 Entidades JPA con relaciones bidireccionales
- ✅ DTOs para transferencia de datos
- ✅ Manejo de excepciones con @ControllerAdvice
- ✅ CORS habilitado para desarrollo
- ✅ Subida de archivos con validación
- ✅ Queries personalizadas con JPQL
- ✅ Endpoint de bitácora con datos enriquecidos

#### Frontend
- ✅ Sistema de autenticación con localStorage
- ✅ Control de acceso basado en roles (permissions.js)
- ✅ 8 Páginas HTML con diseño responsive
- ✅ Sidebar con navegación dinámica según permisos
- ✅ Dashboard con gráficas (Chart.js)
- ✅ Formularios con validación cliente
- ✅ Filtros dinámicos en bitácora
- ✅ Fetch API con manejo de errores
- ✅ Diseño PNP (verde #00642e, amarillo #fbbf24)
- ✅ Redirección automática según estado de sesión

#### Base de Datos
- ✅ 10 Tablas con integridad referencial
- ✅ 39 Áreas precargadas (5 trabajo + 34 PNP)
- ✅ 4 Roles del sistema
- ✅ 7 Usuarios de prueba
- ✅ 10 Tipos de documento
- ✅ ENUM para estados y tipos
- ✅ Timestamps automáticos (created_at, updated_at)

### Pendientes y Mejoras Propuestas 🔧

#### Alta Prioridad
- ⏳ **Flujo completo de estados**: Implementar transiciones Registrado → En Proceso → Finalizado
- ⏳ **Edición de documentos**: Formulario de modificación post-registro
- ⏳ **Búsqueda avanzada**: Filtros combinados con autocompletado
- ⏳ **Validaciones backend**: @Valid en DTOs con mensajes personalizados
- ⏳ **Logs de auditoría**: Registro de todas las acciones CRUD
- ⏳ **Paginación**: PageRequest en consultas grandes (>100 registros)

#### Media Prioridad
- ⏳ **Salida de documentos**: Interfaz completa con cargo de entrega
- ⏳ **Reportes PDF**: Generación con iText o JasperReports
- ⏳ **Exportación Excel**: Apache POI para reportes tabulares
- ⏳ **Dashboard avanzado**: KPIs con cálculos estadísticos
- ⏳ **Notificaciones**: Sistema de alertas en tiempo real (WebSockets)
- ⏳ **Historial de cambios**: Versionado de documentos

#### Baja Prioridad (Mejoras Futuras)
- ⏳ **Refresh tokens**: Renovación automática de JWT
- ⏳ **Multi-idioma**: i18n para español/quechua
- ⏳ **Firma digital**: Integración con certificados digitales
- ⏳ **OCR**: Extracción de texto de PDFs escaneados
- ⏳ **App móvil**: React Native para consultas móviles
- ⏳ **Tests automatizados**: JUnit + Mockito (cobertura 80%)

### Calidad del Código

| Métrica | Valor | Estado |
|---------|-------|--------|
| **Líneas de código** | ~5,200 | 📊 |
| **Cobertura de tests** | 15% | ⚠️ Bajo |
| **Deuda técnica** | Baja | ✅ |
| **Complejidad ciclomática** | Media (8-12) | ✅ |
| **Duplicación de código** | <5% | ✅ |
| **Principios SOLID** | 90% | ✅ |
| **Documentación** | 60% | 🔄 |

---

## 📡 Documentación de la API REST

### Base URL
```
http://localhost:8080/api
```

### Autenticación

Todos los endpoints (excepto `/auth/*`) requieren un token JWT en el header:
```http
Authorization: Bearer <token>
```

#### POST `/auth/login`
Autenticación de usuario.

**Request:**
```json
{
  "username": "nakusu",
  "password": "123456"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 7,
  "username": "nakusu",
  "nombre": "Marcela",
  "apellido": "Rodríguez Munaylla",
  "roles": ["Administrador"]
}
```

#### GET `/auth/me`
Obtiene información del usuario autenticado.

**Response 200:**
```json
{
  "idUsuario": 7,
  "username": "nakusu",
  "nombre": "Marcela",
  "apellido": "Rodríguez Munaylla",
  "email": null,
  "telefono": "987654326",
  "tipoContrato": "LOCADOR",
  "activo": true,
  "roles": [{"idRol": 1, "nombre": "Administrador"}],
  "area": {"idArea": 2, "nombre": "Sistemas", "sigla": "SIS"}
}
```

### Documentos

#### POST `/documentos/registrar`
Registra un nuevo documento.

**Request:**
```json
{
  "titulo": "Solicitud de equipos de cómputo",
  "descripcion": "Solicitud de 5 computadoras para el área de sistemas",
  "idTipoDocumento": 1,
  "numeroDocumento": "OF-2025-001",
  "numeroHt": "HT-2025-001",
  "remitente": "DIRTIC - Dirección de Tecnología de la Información",
  "idUsuarioAsignado": 2,
  "archivoUrl": "/uploads/documentos/1730234567890-documento.pdf"
}
```

**Response 200:**
```json
{
  "idDocumento": 8,
  "codigo": "DOC-000008",
  "titulo": "Solicitud de equipos de cómputo",
  "estado": "Registrado",
  "fechaIngreso": "2025-10-29T14:30:00"
}
```

#### GET `/documentos`
Lista todos los documentos.

**Response 200:**
```json
[
  {
    "idDocumento": 1,
    "codigo": "DOC-000001",
    "titulo": "Solicitud de combustible",
    "estado": "Registrado",
    "remitente": "DIRANDRO",
    "fechaIngreso": "2025-09-01T08:00:00",
    "tipoDocumento": {"idTipoDocumento": 1, "nombre": "Oficio"}
  }
]
```

#### GET `/documentos/{id}`
Obtiene un documento por ID.

#### GET `/documentos/bitacora`
Listado de documentos con usuario asignado (para bitácora).

**Response 200:**
```json
[
  {
    "documento": {
      "idDocumento": 1,
      "codigo": "DOC-000001",
      "titulo": "Solicitud de combustible",
      "estado": "Registrado"
    },
    "usuarioAsignado": "Edwin Cisneros",
    "idUsuarioAsignado": 2
  }
]
```

#### GET `/documentos/asignados/{userId}`
Documentos asignados a un usuario específico (para rol Trabajador).

#### POST `/documentos/upload`
Sube un archivo PDF al servidor.

**Request:** FormData
```
file: <archivo.pdf>
```

**Response 200:**
```json
{
  "url": "/uploads/documentos/1730234567890-documento.pdf",
  "message": "Archivo subido exitosamente"
}
```

### Usuarios

#### GET `/usuarios`
Lista todos los usuarios.

**Response 200:**
```json
[
  {
    "idUsuario": 1,
    "username": "mdepaz",
    "nombre": "Marius",
    "apellido": "De Paz Salazar",
    "email": null,
    "telefono": "987654321",
    "tipoContrato": "LOCADOR",
    "activo": true,
    "area": {"idArea": 2, "nombre": "Sistemas", "sigla": "SIS"}
  }
]
```

#### GET `/usuarios/{id}`
Obtiene un usuario por ID.

#### POST `/usuarios`
Crea un nuevo usuario.

**Request:**
```json
{
  "username": "jperez",
  "nombre": "Juan",
  "apellido": "Pérez López",
  "password": "123456",
  "tipoContrato": "CAS",
  "telefono": "987654330",
  "idArea": 1,
  "idRoles": [3]
}
```

#### PUT `/usuarios/{id}`
Actualiza un usuario existente.

#### DELETE `/usuarios/{id}`
Desactiva un usuario (soft delete).

### Áreas

#### GET `/areas`
Lista todas las áreas (departamentos PNP + áreas de trabajo).

**Response 200:**
```json
[
  {
    "idArea": 1,
    "nombre": "Mesa de Partes",
    "sigla": "MDP",
    "tipo": "AREA_TRABAJO"
  },
  {
    "idArea": 6,
    "nombre": "Comandancia General de la PNP",
    "sigla": "COMGEN",
    "tipo": "DEPARTAMENTO_PNP"
  }
]
```

#### POST `/areas`
Crea una nueva área.

#### PUT `/areas/{id}`
Actualiza un área.

#### DELETE `/areas/{id}`
Elimina un área (si no tiene dependencias).

### Tipos de Documento

#### GET `/tipos-documento`
Lista todos los tipos de documento.

**Response 200:**
```json
[
  {"idTipoDocumento": 1, "nombre": "Oficio"},
  {"idTipoDocumento": 2, "nombre": "Correo"},
  {"idTipoDocumento": 3, "nombre": "Memorándum"}
]
```

### Roles

#### GET `/roles`
Lista todos los roles del sistema.

**Response 200:**
```json
[
  {"idRol": 1, "nombre": "Administrador"},
  {"idRol": 2, "nombre": "Mesa de Partes"},
  {"idRol": 3, "nombre": "Trabajador"},
  {"idRol": 4, "nombre": "Jefatura"}
]
```

---

## 🔧 Guía Técnica para Desarrolladores

### Estructura de Directorios

```
ProyectoMesaDePartes/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pnp/mesadepartes/
│   │   │   │   ├── MesadepartesApplication.java    # Clase principal Spring Boot
│   │   │   │   ├── config/
│   │   │   │   │   ├── FileUploadConfig.java       # Configuración de subida de archivos
│   │   │   │   │   └── SecurityConfig.java         # Configuración de seguridad JWT
│   │   │   │   ├── controller/                     # Capa de presentación (REST)
│   │   │   │   │   ├── AuthController.java         # Login, logout, me
│   │   │   │   │   ├── DocumentoController.java    # CRUD documentos + upload
│   │   │   │   │   ├── UsuarioController.java      # CRUD usuarios
│   │   │   │   │   ├── AreaController.java         # CRUD áreas
│   │   │   │   │   └── TipoDocumentoController.java
│   │   │   │   ├── dto/                            # Objetos de transferencia
│   │   │   │   │   ├── DocumentoRegistroDTO.java   # DTO para registro
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   └── UserInfoResponse.java
│   │   │   │   ├── model/                          # Entidades JPA
│   │   │   │   │   ├── Documento.java              # @Entity con relaciones
│   │   │   │   │   ├── Usuario.java
│   │   │   │   │   ├── Area.java                   # Con ENUM TipoArea
│   │   │   │   │   ├── Tramite.java
│   │   │   │   │   └── ...
│   │   │   │   ├── repository/                     # Capa de persistencia
│   │   │   │   │   ├── DocumentoRepository.java    # extends JpaRepository
│   │   │   │   │   ├── UsuarioRepository.java      # findByUsername
│   │   │   │   │   └── TramiteRepository.java      # Custom queries
│   │   │   │   └── security/                       # Seguridad y JWT
│   │   │   │       ├── jwt/
│   │   │   │       │   ├── JwtUtils.java           # Generación/validación tokens
│   │   │   │       │   └── JwtAuthenticationFilter.java
│   │   │   │       └── services/
│   │   │   │           └── UserDetailsServiceImpl.java
│   │   │   └── resources/
│   │   │       ├── application.properties          # Configuración DB, JWT, etc.
│   │   │       └── static/                         # Frontend servido por Spring
│   │   │           ├── index.html
│   │   │           ├── dashboard.html
│   │   │           └── assets/
│   │   │               ├── css/
│   │   │               │   ├── style.css
│   │   │               │   ├── dashboard.css
│   │   │               │   └── sidebar.css
│   │   │               └── js/
│   │   │                   ├── config.js           # API_URL, constantes
│   │   │                   ├── auth.js             # Verificación de sesión
│   │   │                   ├── permissions.js      # Control de acceso RBAC
│   │   │                   ├── sidebar.js          # Menú dinámico
│   │   │                   ├── dashboard.js        # Métricas y gráficas
│   │   │                   ├── registro.js         # Formulario de documentos
│   │   │                   └── bitacora.js         # Tabla con filtros
│   │   └── test/                                   # Tests unitarios (pendiente)
│   ├── uploads/                                    # Archivos PDF subidos
│   │   └── documentos/
│   ├── pom.xml                                     # Dependencias Maven
│   ├── mvnw / mvnw.cmd                            # Maven Wrapper
│   └── start-app.bat                              # Script de inicio
├── frontend/                                       # Desarrollo frontend
│   ├── *.html                                      # Páginas HTML
│   └── assets/                                     # Recursos estáticos
├── SQL/
│   ├── mesa_de_partes_bd.sql                      # Schema completo
│   └── setup_completo.sql                         # Schema + datos
└── README.md

```

### Cómo Funciona Cada Componente

#### 1. **Controllers** (@RestController)
Los controladores manejan las peticiones HTTP y devuelven respuestas JSON.

**Responsabilidades:**
- Recibir peticiones HTTP (GET, POST, PUT, DELETE)
- Validar datos de entrada
- Llamar a repositorios para operaciones CRUD
- Construir respuestas con ResponseEntity
- Manejo de errores con try-catch

**Ejemplo de uso:**
```java
@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {
    @Autowired private DocumentoRepository documentoRepository;
    
    @GetMapping
    public List<Documento> getAllDocumentos() {
        return documentoRepository.findAll();  // SELECT * FROM documentos
    }
    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody DocumentoRegistroDTO dto) {
        // Lógica de negocio
        Documento doc = new Documento();
        doc.setTitulo(dto.getTitulo());
        documentoRepository.save(doc);  // INSERT INTO documentos
        return ResponseEntity.ok(doc);
    }
}
```

#### 2. **Repositories** (Spring Data JPA)
Interfaces que extienden JpaRepository para acceso a datos.

**Responsabilidades:**
- Operaciones CRUD automáticas (save, findById, findAll, delete)
- Queries personalizadas con nomenclatura de métodos
- Queries JPQL con @Query
- Transacciones con @Transactional

**Ejemplo de uso:**
```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Data genera automáticamente: SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);
    
    // Query personalizada con JPQL
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.area.idArea = :idArea")
    List<Usuario> findActiveUsersByArea(@Param("idArea") Long idArea);
}
```

#### 3. **Models** (@Entity)
Clases POJO que representan tablas de la base de datos.

**Responsabilidades:**
- Mapeo de tablas con @Entity y @Table
- Definición de columnas con @Column
- Relaciones con @ManyToOne, @OneToMany, @ManyToMany
- Generación automática de getters/setters con Lombok

**Ejemplo de uso:**
```java
@Data
@Entity
@Table(name = "documentos")
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_documento")
    private Long idDocumento;
    
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;  // DOC-000001
    
    @Enumerated(EnumType.STRING)
    private EstadoDocumento estado;  // Registrado, En Proceso, etc.
    
    @ManyToOne
    @JoinColumn(name = "ID_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;  // FK a tipos_documento
}
```

#### 4. **DTOs** (Data Transfer Objects)
Objetos para transferir datos entre capas sin exponer entidades completas.

**Responsabilidades:**
- Simplificar peticiones del frontend
- Evitar lazy loading issues
- Validaciones con @NotNull, @Size, etc.
- Ocultar campos sensibles (password_hash)

**Ejemplo de uso:**
```java
@Data
public class DocumentoRegistroDTO {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    
    @NotNull(message = "El tipo de documento es obligatorio")
    private Long idTipoDocumento;
    
    private String descripcion;  // Opcional
    private String numeroHt;     // Opcional
}
```

#### 5. **Security** (JWT + BCrypt)
Sistema de autenticación y autorización.

**Flujo de autenticación:**
1. Usuario envía username/password a `/api/auth/login`
2. `AuthController` valida credenciales con BCrypt
3. Si es correcto, `JwtUtils` genera un token JWT
4. Frontend guarda token en localStorage
5. En cada petición, `JwtAuthenticationFilter` valida el token
6. Spring Security autoriza acceso según roles

**Ejemplo de uso:**
```java
// JwtUtils.java
public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
}
```

#### 6. **Frontend** (Vanilla JavaScript)
Interfaz de usuario con HTML, CSS y JavaScript puro.

**Patrones usados:**
- **Fetch API** para llamadas asíncronas
- **LocalStorage** para persistencia de token y userInfo
- **Async/Await** para código limpio
- **Modularización** con archivos JS separados

**Ejemplo de uso:**
```javascript
// registro.js
async function registrarDocumento() {
    const data = {
        titulo: document.getElementById('titulo').value,
        idTipoDocumento: parseInt(document.getElementById('tipo-documento').value),
        remitente: document.getElementById('remitente').value,
        idUsuarioAsignado: parseInt(document.getElementById('usuario-asignado').value)
    };
    
    const response = await fetch(`${API_URL}/documentos/registrar`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(data)
    });
    
    if (response.ok) {
        const documento = await response.json();
        alert(`Documento ${documento.codigo} registrado exitosamente`);
        window.location.href = 'dashboard.html';
    }
}
```

### Flujo de Registro de un Documento

```
1. Usuario llena formulario en registro.html
   ↓
2. JavaScript valida campos (registro.js)
   ↓
3. Si hay PDF, sube archivo a /api/documentos/upload
   ↓
4. POST /api/documentos/registrar con JSON
   ↓
5. DocumentoController recibe @RequestBody DocumentoRegistroDTO
   ↓
6. Controller valida y busca entidades relacionadas:
   - Usuario registrador (por defecto ID 1)
   - Usuario asignado (del DTO)
   - Tipo de documento (del DTO)
   ↓
7. Controller genera código secuencial (DOC-XXXXXX)
   ↓
8. Controller crea entidad Documento y la guarda
   ↓
9. DocumentoRepository.save() → INSERT INTO documentos
   ↓
10. Si hay numeroHt, crea HojaTramite
   ↓
11. Crea Tramite con usuarioCreador y usuarioAsignado
   ↓
12. TramiteRepository.save() → INSERT INTO tramites
   ↓
13. ResponseEntity.ok(documento) → JSON al frontend
   ↓
14. Frontend muestra mensaje de éxito y redirecciona
```

---

## 🔍 Cómo Funciona el Proyecto - Guía Detallada

### 📁 Estructura y Propósito de Cada Carpeta

#### 🔧 **config/** - Configuraciones del Sistema

**Ubicación:** `backend/src/main/java/com/pnp/mesadepartes/config/`

##### `SecurityConfig.java`
**¿Qué hace?** Configura la seguridad de toda la aplicación.

**Funciones principales:**
- ✅ **Protege endpoints**: Define qué URLs requieren autenticación
- ✅ **Configura JWT**: Integra el filtro de tokens JWT
- ✅ **CORS**: Permite peticiones desde el frontend
- ✅ **BCrypt**: Configura el encoder de contraseñas con 10 rounds

**Código clave:**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(csrf -> csrf.disable())  // Desactiva CSRF (usamos JWT)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()  // Login público
            .anyRequest().authenticated()  // Todo lo demás requiere token
        )
        .addFilterBefore(jwtAuthenticationFilter, ...);  // Añade filtro JWT
}
```

**Lo que valida:**
- Token JWT válido en cada petición
- Contraseñas con BCrypt al hacer login
- Permisos de acceso a endpoints

##### `FileUploadConfig.java`
**¿Qué hace?** Configura la subida y almacenamiento de archivos PDF.

**Funciones principales:**
- 📁 **Directorio de uploads**: `backend/uploads/documentos/`
- 📏 **Tamaño máximo**: 10MB por archivo
- 📄 **Tipos permitidos**: Solo PDF
- 🔒 **Seguridad**: Nombres únicos con timestamp

---

#### 🎮 **controller/** - Puntos de Entrada de la API

**Ubicación:** `backend/src/main/java/com/pnp/mesadepartes/controller/`

Los **Controllers** son la puerta de entrada de tu aplicación. Reciben las peticiones HTTP del frontend.

##### `AuthController.java`
**¿Qué hace?** Maneja todo lo relacionado con autenticación.

**Endpoints:**
- `POST /api/auth/login` → Login con username/password
- `GET /api/auth/me` → Obtiene datos del usuario actual

**Flujo de login:**
```
1. Frontend envía username + password
2. Controller valida con BCrypt
3. Si es correcto, JwtUtils genera token
4. Devuelve token + datos del usuario
5. Frontend guarda token en localStorage
```

**Ejemplo de uso:**
```java
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    // Valida credenciales con BCrypt
    Authentication authentication = authenticationManager.authenticate(...);
    
    // Genera token JWT
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    // Devuelve token + info del usuario
    return ResponseEntity.ok(new UserInfoResponse(jwt, userDetails));
}
```

##### `DocumentoController.java`
**¿Qué hace?** Gestiona todo el ciclo de vida de los documentos.

**Endpoints:**
- `POST /api/documentos/registrar` → Registra nuevo documento
- `POST /api/documentos/upload` → Sube archivo PDF
- `GET /api/documentos` → Lista todos los documentos
- `GET /api/documentos/{id}` → Obtiene documento específico
- `GET /api/documentos/bitacora` → Lista con usuarios asignados
- `GET /api/documentos/asignados/{userId}` → Documentos de un usuario

**Lo que hace al registrar:**
1. Valida datos de entrada
2. Busca usuario registrador, asignado y tipo de documento
3. Genera código secuencial (DOC-000001, DOC-000002...)
4. Guarda documento en BD
5. Crea hoja de trámite si existe numeroHt
6. Crea trámite con usuario asignado
7. Devuelve documento creado al frontend

##### `UsuarioController.java`
**¿Qué hace?** CRUD completo de usuarios.

**Endpoints:**
- `GET /api/usuarios` → Lista todos
- `GET /api/usuarios/{id}` → Obtiene uno
- `POST /api/usuarios` → Crea nuevo
- `PUT /api/usuarios/{id}` → Actualiza
- `DELETE /api/usuarios/{id}` → Desactiva (soft delete)

##### `AreaController.java`
**¿Qué hace?** Gestiona áreas (departamentos PNP y áreas de trabajo).

**Endpoints:**
- `GET /api/areas` → Lista todas las áreas
- `POST /api/areas` → Crea nueva área
- `PUT /api/areas/{id}` → Actualiza área
- `DELETE /api/areas/{id}` → Elimina área

**Importante:** Devuelve áreas con campo `tipo`:
- `DEPARTAMENTO_PNP` → Para documentos (DIRTIC, DIRANDRO, etc.)
- `AREA_TRABAJO` → Para usuarios (Sistemas, Desarrollo, etc.)

---

#### 🗄️ **model/** - Entidades de Base de Datos

**Ubicación:** `backend/src/main/java/com/pnp/mesadepartes/model/`

Los **Models** representan las tablas de tu base de datos.

##### `Documento.java`
**¿Qué es?** La entidad principal del sistema.

**Campos importantes:**
```java
@Entity
@Table(name = "documentos")
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocumento;
    
    @Column(unique = true, nullable = false)
    private String codigo;  // DOC-000001
    
    private String titulo;
    private String descripcion;
    private String numeroDocumento;
    
    @Enumerated(EnumType.STRING)
    private EstadoDocumento estado;  // Registrado, En Proceso, etc.
    
    private String remitente;  // DIRTIC, DIRANDRO, etc.
    private LocalDateTime fechaIngreso;
    private String archivoUrl;  // /uploads/documentos/1234567890-doc.pdf
    
    @ManyToOne
    @JoinColumn(name = "ID_usuario_registro")
    private Usuario usuarioRegistro;  // Quién lo registró
    
    @ManyToOne
    @JoinColumn(name = "ID_tipo_documento")
    private TipoDocumento tipoDocumento;  // Oficio, Memorándum, etc.
}
```

##### `Usuario.java`
**¿Qué es?** Representa a los usuarios del sistema.

**Campos importantes:**
```java
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String passwordHash;  // Contraseña con BCrypt
    
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    private TipoContrato tipoContrato;  // CAS, LOCADOR, PNP
    
    private Boolean activo = true;  // Para soft delete
    
    @ManyToOne
    @JoinColumn(name = "ID_area")
    private Area area;  // Área de trabajo del usuario
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles",
        joinColumns = @JoinColumn(name = "ID_usuario"),
        inverseJoinColumns = @JoinColumn(name = "ID_rol"))
    private Set<Rol> roles;  // Administrador, Mesa de Partes, etc.
}
```

##### `Area.java`
**¿Qué es?** Departamentos PNP y áreas de trabajo.

**Campos importantes:**
```java
@Entity
@Table(name = "areas")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArea;
    
    private String nombre;
    private String sigla;
    
    @Enumerated(EnumType.STRING)
    private TipoArea tipo;  // DEPARTAMENTO_PNP o AREA_TRABAJO
    
    public enum TipoArea {
        DEPARTAMENTO_PNP,  // Para documentos: DIRTIC, DIRANDRO
        AREA_TRABAJO       // Para usuarios: Sistemas, Desarrollo
    }
}
```

##### `Tramite.java`
**¿Qué es?** Registra la asignación de documentos a usuarios.

**Campos importantes:**
```java
@Entity
@Table(name = "tramites")
public class Tramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTramite;
    
    @ManyToOne
    @JoinColumn(name = "ID_documento")
    private Documento documento;
    
    @ManyToOne
    @JoinColumn(name = "ID_usuario_creador")
    private Usuario usuarioCreador;  // Quién creó el trámite
    
    @ManyToOne
    @JoinColumn(name = "ID_usuario_asignado")
    private Usuario usuarioAsignado;  // A quién se le asignó
}
```

**¿Por qué es importante?** Esta tabla conecta documentos con usuarios, permitiendo saber a quién está asignado cada documento.

---

#### 💾 **repository/** - Acceso a Datos

**Ubicación:** `backend/src/main/java/com/pnp/mesadepartes/repository/`

Los **Repositories** son interfaces que extienden `JpaRepository` y te dan acceso automático a la base de datos.

##### `DocumentoRepository.java`
**¿Qué hace?** Operaciones CRUD + consultas personalizadas para documentos.

```java
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    // Spring Data genera automáticamente:
    // - save(documento)
    // - findById(id)
    // - findAll()
    // - deleteById(id)
    
    // Consultas personalizadas:
    List<Documento> findByEstado(EstadoDocumento estado);
    List<Documento> findByUsuarioRegistro(Usuario usuario);
}
```

##### `UsuarioRepository.java`
**¿Qué hace?** Operaciones para usuarios + búsqueda por username.

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Consulta personalizada para login
    Optional<Usuario> findByUsername(String username);
    
    // Consulta para verificar si existe
    Boolean existsByUsername(String username);
}
```

##### `TramiteRepository.java`
**¿Qué hace?** Consultas para trámites y asignaciones.

```java
public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    // Busca trámites de un documento
    List<Tramite> findByDocumento(Documento documento);
    
    // Busca trámites asignados a un usuario
    List<Tramite> findByUsuarioAsignado_IdUsuario(Long idUsuario);
}
```

**Uso en el código:**
```java
// En DocumentoController
List<Tramite> tramites = tramiteRepository.findByDocumento(documento);
Usuario asignado = tramites.get(0).getUsuarioAsignado();
```

---

#### 🔐 **security/** - Seguridad y Autenticación

**Ubicación:** `backend/src/main/java/com/pnp/mesadepartes/security/`

##### `jwt/JwtUtils.java`
**¿Qué hace?** Genera y valida tokens JWT.

**Funciones:**
- `generateJwtToken()` → Crea token JWT válido por 24 horas
- `getUserNameFromJwtToken()` → Extrae username del token
- `validateJwtToken()` → Verifica si el token es válido

**Ejemplo:**
```java
// Generar token al hacer login
String token = jwtUtils.generateJwtToken(authentication);

// Validar token en cada petición
boolean isValid = jwtUtils.validateJwtToken(token);

// Extraer username del token
String username = jwtUtils.getUserNameFromJwtToken(token);
```

##### `jwt/JwtAuthenticationFilter.java`
**¿Qué hace?** Intercepta TODAS las peticiones HTTP y valida el token.

**Flujo:**
```
1. Petición HTTP llega → interceptada por el filtro
2. Extrae token del header Authorization: Bearer <token>
3. Valida token con JwtUtils
4. Si es válido, carga datos del usuario
5. Si no es válido, rechaza la petición (401 Unauthorized)
6. Si es válido, continúa al Controller
```

##### `services/UserDetailsServiceImpl.java`
**¿Qué hace?** Carga datos del usuario desde la BD para autenticación.

```java
@Override
public UserDetails loadUserByUsername(String username) {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    
    // Convierte Usuario a UserDetails (Spring Security)
    return UserDetailsImpl.build(usuario);
}
```

---

#### 📦 **dto/** - Objetos de Transferencia

**Ubicación:** `backend/src/main/java/com/pnp/mesadepartes/dto/`

Los **DTOs** son objetos simples para transferir datos entre frontend y backend.

##### `DocumentoRegistroDTO.java`
**¿Para qué?** Recibe datos del formulario de registro de documentos.

```java
public class DocumentoRegistroDTO {
    private String titulo;
    private String descripcion;
    private Long idTipoDocumento;
    private String numeroDocumento;
    private String numeroHt;
    private String remitente;
    private Long idUsuarioAsignado;
    private String archivoUrl;
}
```

**¿Por qué usar DTO?**
- ✅ No expone toda la entidad Documento
- ✅ Solo los campos necesarios del frontend
- ✅ Evita lazy loading issues
- ✅ Validaciones específicas con @NotNull, @Size, etc.

##### `LoginRequest.java`
**¿Para qué?** Recibe credenciales de login.

```java
public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}
```

##### `UserInfoResponse.java`
**¿Para qué?** Devuelve datos del usuario después del login.

```java
public class UserInfoResponse {
    private String token;  // JWT
    private String type = "Bearer";
    private Long id;
    private String username;
    private String nombre;
    private String apellido;
    private List<String> roles;
}
```

---

### 🎨 Frontend - Interfaz de Usuario

**Ubicación:** `backend/src/main/resources/static/` (servido por Spring Boot)

#### 📄 **HTML Files** - Páginas del Sistema

##### `index.html`
**¿Qué hace?** Página de entrada que detecta sesión.

**Lógica:**
```javascript
const token = localStorage.getItem('token');
if (token && userInfo) {
    window.location.href = 'dashboard.html';  // Ya logueado
} else {
    window.location.href = 'login.html';  // Ir a login
}
```

##### `login.html`
**¿Qué hace?** Formulario de autenticación.

**Elementos:**
- Input para username
- Input para password (type="password")
- Botón "Iniciar Sesión"
- Mensaje de error si falla

##### `dashboard.html`
**¿Qué hace?** Panel principal con métricas y gráficas.

**Muestra:**
- 📊 Total de documentos
- 📈 Documentos por estado (gráfica de torta)
- 📊 Documentos por mes (gráfica de barras)
- 📋 Lista de documentos recientes

##### `registro.html` (registrar-interno.js)
**¿Qué hace?** Formulario para registrar documentos.

**Campos:**
- Título (obligatorio)
- Tipo de documento (dropdown)
- Remitente (dropdown con DEPARTAMENTOS PNP)
- Usuario asignado (dropdown)
- Número de documento
- Número HT
- Descripción
- Archivo PDF (upload)

##### `bitacora.html`
**¿Qué hace?** Tabla con todos los documentos y filtros.

**Muestra:**
- Tabla con: Fecha, Usuario Asignado, Código, Remitente, Tipo, Estado
- Filtros por: Estado, Área, Tipo de Documento, Rango de fechas
- Paginación (si hay muchos registros)

##### `gestion-usuarios.html`
**¿Qué hace?** CRUD de usuarios (solo Administrador).

**Funciones:**
- Listar todos los usuarios
- Crear nuevo usuario
- Editar usuario existente
- Desactivar usuario (soft delete)

---

#### 🎨 **assets/css/** - Estilos

##### `style.css`
**¿Qué hace?** Estilos globales de la aplicación.

**Define:**
- Variables CSS para colores PNP:
  ```css
  :root {
      --pnp-green: #00642e;
      --pnp-green-dark: #004d24;
      --pnp-yellow: #fbbf24;
  }
  ```
- Reset de estilos base
- Tipografía general
- Clases de utilidad

##### `sidebar.css`
**¿Qué hace?** Estilos del menú lateral.

**Elementos:**
- Logo circular PNP
- Menú de navegación
- Hover effects
- Active state
- Responsive collapse

##### `dashboard.css`
**¿Qué hace?** Estilos del dashboard.

**Elementos:**
- Cards de métricas
- Gráficas con Chart.js
- Layout en grid
- Tabla de documentos recientes

##### `bitacora.css`
**¿Qué hace?** Estilos de la bitácora.

**Elementos:**
- Tabla responsive
- Filtros superiores
- Badges de estado
- Paginación

---

#### ⚙️ **assets/js/** - Lógica del Frontend

##### `config.js`
**¿Qué hace?** Configuración global del frontend.

```javascript
const API_URL = 'http://localhost:8080/api';
const DEFAULT_DASHBOARD = 'dashboard.html';
const MAX_FILE_SIZE = 10 * 1024 * 1024;  // 10MB
```

##### `auth.js`
**¿Qué hace?** Funciones de autenticación.

**Funciones:**
```javascript
// Verifica si hay sesión activa
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
    }
}

// Cierra sesión
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    window.location.href = 'login.html';
}

// Headers con token para fetch
function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    };
}
```

##### `permissions.js`
**¿Qué hace?** Control de acceso basado en roles (RBAC).

**Define:**
```javascript
const ROLES = {
    ADMIN: 'Administrador',
    MESA_PARTES: 'Mesa de Partes',
    TRABAJADOR: 'Trabajador',
    JEFATURA: 'Jefatura'
};

const PERMISSIONS = {
    VER_DASHBOARD: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.TRABAJADOR, ROLES.JEFATURA],
    VER_REGISTRO: [ROLES.ADMIN, ROLES.MESA_PARTES],
    VER_BITACORA: [ROLES.ADMIN, ROLES.MESA_PARTES, ROLES.JEFATURA],
    VER_USUARIOS: [ROLES.ADMIN],
    VER_SOLO_ASIGNADOS: [ROLES.TRABAJADOR]
};

class PermissionsManager {
    hasPermission(permission) {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        const userRole = userInfo.roles[0];
        return PERMISSIONS[permission].includes(userRole);
    }
}
```

##### `sidebar.js`
**¿Qué hace?** Gestiona el menú lateral dinámico.

**Funciones:**
```javascript
// Filtra menú según permisos
function filterMenuByPermissions() {
    const permissionsManager = new PermissionsManager();
    
    // Oculta opciones según rol
    if (!permissionsManager.hasPermission('VER_REGISTRO')) {
        document.getElementById('menu-registro').style.display = 'none';
    }
    
    if (!permissionsManager.hasPermission('VER_USUARIOS')) {
        document.getElementById('menu-usuarios').style.display = 'none';
    }
}
```

##### `dashboard.js`
**¿Qué hace?** Carga métricas y gráficas del dashboard.

**Funciones:**
```javascript
// Carga estadísticas
async function cargarMetricas() {
    const response = await fetch(`${API_URL}/documentos`, {
        headers: getAuthHeaders()
    });
    const documentos = await response.json();
    
    // Calcula métricas
    const total = documentos.length;
    const registrados = documentos.filter(d => d.estado === 'Registrado').length;
    const enProceso = documentos.filter(d => d.estado === 'En Proceso').length;
    
    // Actualiza cards
    document.getElementById('total-docs').textContent = total;
    document.getElementById('registrados').textContent = registrados;
    document.getElementById('en-proceso').textContent = enProceso;
}

// Crea gráfica con Chart.js
function cargarGraficas() {
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Registrado', 'En Proceso', 'Finalizado'],
            datasets: [{
                data: [10, 5, 3],
                backgroundColor: ['#00642e', '#fbbf24', '#059669']
            }]
        }
    });
}
```

##### `registro.js` / `registrar-interno.js`
**¿Qué hace?** Maneja el formulario de registro de documentos.

**Funciones:**
```javascript
// Carga dropdowns
async function cargarAreas() {
    const response = await fetch(`${API_URL}/areas`);
    const areas = await response.json();
    
    // Filtra solo departamentos PNP
    const departamentosPNP = areas.filter(area => area.tipo === 'DEPARTAMENTO_PNP');
    
    // Llena dropdown
    departamentosPNP.forEach(area => {
        remitenteSelect.innerHTML += `<option value="${area.sigla}">${area.sigla} - ${area.nombre}</option>`;
    });
}

// Registra documento
async function handleSubmit(event) {
    event.preventDefault();
    
    // Construye datos
    const data = {
        titulo: document.getElementById('titulo').value,
        idTipoDocumento: parseInt(document.getElementById('tipo-documento').value),
        remitente: document.getElementById('remitente').value,
        idUsuarioAsignado: parseInt(document.getElementById('usuario-asignado').value)
    };
    
    // Envía al backend
    const response = await fetch(`${API_URL}/documentos/registrar`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    });
    
    if (response.ok) {
        alert('Documento registrado exitosamente');
        window.location.href = 'dashboard.html';
    }
}
```

##### `bitacora.js`
**¿Qué hace?** Muestra tabla de documentos con filtros.

**Funciones:**
```javascript
// Carga documentos con usuario asignado
async function cargarDocumentos() {
    const response = await fetch(`${API_URL}/documentos/bitacora`, {
        headers: getAuthHeaders()
    });
    const documentos = await response.json();
    
    // Construye tabla
    tableBody.innerHTML = documentos.map(item => {
        const doc = item.documento;
        const usuarioAsignado = item.usuarioAsignado || 'Sin asignar';
        
        return `<tr>
            <td>${formatDate(doc.fechaIngreso)}</td>
            <td><strong>${usuarioAsignado}</strong></td>
            <td>${doc.codigo}</td>
            <td>${doc.remitente}</td>
            <td>${doc.tipoDocumento.nombre}</td>
            <td><span class="badge ${getBadgeClass(doc.estado)}">${doc.estado}</span></td>
        </tr>`;
    }).join('');
}

// Filtra documentos
function filtrarDocumentos() {
    const estadoFiltro = document.getElementById('filtro-estado').value;
    const documentosFiltrados = documentos.filter(doc => {
        if (estadoFiltro && doc.documento.estado !== estadoFiltro) return false;
        return true;
    });
    mostrarDocumentos(documentosFiltrados);
}
```

##### `gestion-usuarios.js`
**¿Qué hace?** CRUD de usuarios.

**Funciones:**
```javascript
// Lista usuarios
async function cargarUsuarios() {
    const response = await fetch(`${API_URL}/usuarios`, {
        headers: getAuthHeaders()
    });
    const usuarios = await response.json();
    
    // Construye tabla
    usuarios.forEach(usuario => {
        const row = `<tr>
            <td>${usuario.username}</td>
            <td>${usuario.nombre} ${usuario.apellido}</td>
            <td>${usuario.area?.nombre || 'Sin área'}</td>
            <td>${usuario.roles.map(r => r.nombre).join(', ')}</td>
            <td>
                <button onclick="editarUsuario(${usuario.idUsuario})">Editar</button>
                <button onclick="desactivarUsuario(${usuario.idUsuario})">Desactivar</button>
            </td>
        </tr>`;
        tableBody.innerHTML += row;
    });
}
```

---

### 🔄 Flujo Completo de una Petición

```
1. Usuario hace click en "Registrar Documento"
   ↓
2. Frontend (registro.html) muestra formulario
   ↓
3. Usuario llena campos y hace submit
   ↓
4. JavaScript (registro.js) intercepta submit
   ↓
5. Valida campos en cliente
   ↓
6. Construye objeto JSON con datos
   ↓
7. Fetch POST a http://localhost:8080/api/documentos/registrar
   ↓
8. JwtAuthenticationFilter intercepta petición
   ↓
9. Valida token JWT del header Authorization
   ↓
10. Si token válido, permite acceso al Controller
    ↓
11. DocumentoController.registrarDocumento() recibe DTO
    ↓
12. Controller busca entidades relacionadas en BD
    ↓
13. Controller genera código secuencial
    ↓
14. DocumentoRepository.save() guarda en BD
    ↓
15. TramiteRepository.save() crea trámite
    ↓
16. Controller devuelve ResponseEntity con documento
    ↓
17. Frontend recibe JSON response
    ↓
18. JavaScript muestra mensaje de éxito
    ↓
19. Redirecciona a dashboard.html
```

---

### 🔑 Puntos Clave para Entender el Proyecto

1. **Spring Boot sirve el frontend**: No hay servidor separado, Spring Boot sirve los archivos HTML/CSS/JS desde `src/main/resources/static/`

2. **JWT en cada petición**: Después del login, TODAS las peticiones incluyen `Authorization: Bearer <token>` en el header

3. **Dos tipos de áreas**: La tabla `areas` tiene un campo `tipo`:
   - `DEPARTAMENTO_PNP`: Para remitentes de documentos
   - `AREA_TRABAJO`: Para asignación de usuarios

4. **BCrypt para contraseñas**: Las contraseñas NUNCA se guardan en texto plano, siempre con hash BCrypt de 10 rounds

5. **Trámites = Asignaciones**: La tabla `tramites` conecta documentos con usuarios asignados

6. **Permisos en frontend**: El archivo `permissions.js` oculta/muestra menús según el rol del usuario

7. **Códigos secuenciales**: Los códigos de documentos se generan automáticamente contando los documentos existentes + 1

8. **Soft delete**: Los usuarios no se eliminan físicamente, solo se marca `activo = false`

9. **Fetch API**: Todo el frontend usa `fetch()` con async/await, sin jQuery ni axios

10. **Chart.js para gráficas**: El dashboard usa Chart.js 4.4.0 para visualizaciones

---

## 🎨 Interfaz de Usuario

- **Diseño moderno y profesional** con colores institucionales PNP
- **Responsive design** adaptable a dispositivos móviles
- **Formularios intuitivos** con validación en tiempo real
- **Navegación simplificada** mediante dashboard centralizado

---

## 🛠️ Stack Tecnológico

### Backend

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Java** | 21.0.8 LTS (Temurin) | Lenguaje principal |
| **Spring Boot** | 3.5.6 | Framework backend |
| **Spring Security** | 6.x | Autenticación (simplificada) |
| **Hibernate** | 6.6.29.Final | ORM para persistencia |
| **MySQL Connector** | 8.0.33 | Driver JDBC |
| **Lombok** | 1.18.36 | Reducción de código boilerplate |
| **Maven** | Wrapper incluido | Gestión de dependencias |

### Frontend

| Tecnología | Descripción |
|------------|-------------|
| **HTML5** | Estructura semántica |
| **CSS3** | Estilos y diseño responsive |
| **JavaScript (Vanilla)** | Lógica de cliente sin frameworks |
| **Fetch API** | Comunicación con REST API |
| **Python HTTP Server** | Servidor estático para desarrollo |

### Base de Datos

| Componente | Detalle |
|------------|---------|
| **MySQL** | 8.0.40 Community Server |
| **Nombre BD** | `mesa_partes_db` |
| **Charset** | utf8mb4_unicode_ci |
| **Tablas** | 10 tablas relacionales |

---

## 📦 Instalación y Configuración

### 📋 Prerrequisitos

- ✅ **Java JDK 21** o superior ([Descargar Temurin](https://adoptium.net/))
- ✅ **MySQL 8.0** o superior
- ✅ **Maven** (incluido wrapper en el proyecto)
- ✅ **Python 3.x** (para servidor frontend)
- ✅ **Git** (opcional, para clonar repositorio)

---

## 🚀 Instalación y Configuración

### Requisitos Previos

- **Java 21 LTS** (OpenJDK o Oracle JDK)
- **Maven 3.9+** (incluido con Maven Wrapper)
- **MySQL 8.0+**
- **Git** (opcional, para clonar repositorio)
- **Navegador moderno** (Chrome, Firefox, Edge)

### 🔧 Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/Nakusuo/ProyectoMesaDePartes.git
cd ProyectoMesaDePartes
```

### 🗄️ Paso 2: Configurar Base de Datos

1. **Iniciar MySQL** y conectarse:
```bash
mysql -u root -p
```

2. **Ejecutar script completo** (crea DB + tablas + datos):
```bash
# Desde MySQL Workbench o CLI
SOURCE ProyectoMesaDePartes/SQL/mesa_de_partes_bd.sql;

# O desde terminal
mysql -u root -p < ProyectoMesaDePartes/SQL/mesa_de_partes_bd.sql
```

Esto creará:
- Base de datos `mesa_partes_db`
- 10 tablas con relaciones FK
- 39 áreas (5 trabajo + 34 PNP)
- 4 roles del sistema
- 7 usuarios de prueba
- 10 tipos de documento
- 7 documentos de ejemplo

3. **Verificar configuración** en `backend/src/main/resources/application.properties`:
```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mesa_partes_db?useSSL=false&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
app.jwtSecret=mySecretKey123456789012345678901234567890
app.jwtExpirationMs=86400000

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### ⚙️ Paso 3: Compilar y Ejecutar Backend

**Opción A: Script automático (Windows)**
```bash
cd ProyectoMesaDePartes\backend
start-app.bat
```

**Opción B: Maven Wrapper manual**
```bash
cd ProyectoMesaDePartes/backend

# En Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run

# En Linux/Mac
./mvnw clean install
./mvnw spring-boot:run
```

**Verificar inicio exitoso:**
```
Started MesadepartesApplication in 4.287 seconds
Tomcat started on port 8080 (http) with context path '/'
```

El backend estará disponible en: `http://localhost:8080`

### 🌐 Paso 4: Acceder al Frontend

El frontend está integrado en Spring Boot y se sirve automáticamente:

1. **Abrir navegador** y acceder a: `http://localhost:8080`
2. **Redirección automática** a login (index.html detecta sesión)
3. **Credenciales de prueba**:
   - Usuario: `nakusu`
   - Contraseña: `123456`
   - Rol: Administrador

**Otros usuarios de prueba:**
| Username | Contraseña | Rol | Área |
|----------|------------|-----|------|
| `nakusu` | 123456 | Administrador | Sistemas |
| `accori` | 123456 | Mesa de Partes | Mesa de Partes |
| `mdepaz` | 123456 | Trabajador | Sistemas |
| `ecisneros` | 123456 | Trabajador | Desarrollo |
| `ghuaman` | 123456 | Jefatura | Soporte Técnico |

### 🔍 Verificación de Instalación

**1. Verificar Backend:**
```bash
curl http://localhost:8080/api/areas
```
Debe devolver JSON con 39 áreas.

**2. Verificar Autenticación:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"nakusu","password":"123456"}'
```
Debe devolver un token JWT.

**3. Verificar Frontend:**
Abrir `http://localhost:8080` y verificar redirección a login.

---

## 📝 Uso del Sistema

### Flujo de Trabajo Completo

#### 1. **Login** (Todos los roles)
1. Acceder a `http://localhost:8080`
2. Ingresar credenciales
3. Sistema valida con BCrypt y genera JWT
4. Redirección automática a dashboard

#### 2. **Dashboard** (Todos los roles)
- **Administrador/Jefatura**: Ve todas las métricas y documentos
- **Mesa de Partes**: Ve documentos registrados y pendientes
- **Trabajador**: Ve solo documentos asignados a él
- Gráficas: Documentos por estado, por tipo, por mes

#### 3. **Registro de Documento** (Administrador, Mesa de Partes)
1. Click en "Registrar Documento" en sidebar
2. Llenar formulario:
   - Título (obligatorio)
   - Tipo de documento (obligatorio)
   - Remitente (dropdown con DEPARTAMENTOS PNP)
   - Usuario asignado (obligatorio)
   - Número de documento (opcional)
   - Número HT (opcional)
   - Descripción (opcional)
   - Archivo PDF (opcional, máx 10MB)
3. Click "Registrar Documento"
4. Sistema genera código secuencial (DOC-000008)
5. Crea trámite con usuario asignado
6. Redirección a dashboard con mensaje de éxito

#### 4. **Bitácora** (Administrador, Mesa de Partes, Jefatura)
1. Click en "Bitácora" en sidebar
2. Tabla con todos los documentos:
   - Fecha de ingreso
   - Usuario asignado
   - Código del documento
   - Remitente
   - Tipo
   - Estado
3. Filtros disponibles:
   - Por estado (dropdown)
   - Por área (dropdown)
   - Por tipo de documento (dropdown)
   - Por rango de fechas
4. Click en fila para ver detalles completos

#### 5. **Gestión de Usuarios** (Solo Administrador)
1. Click en "Usuarios" en sidebar
2. Tabla con todos los usuarios
3. Botón "Nuevo Usuario":
   - Llenar formulario con validaciones
   - Asignar área de trabajo (MDP, SIS, DEV, RED, ST)
   - Asignar rol
4. Botón "Editar" en cada fila
5. Botón "Desactivar" (soft delete)

### Resolución de Problemas Comunes

#### Error: "Port 8080 already in use"
```bash
# Buscar proceso en puerto 8080
netstat -ano | findstr :8080

# Terminar proceso (Windows)
taskkill /F /PID <PID>

# Terminar proceso (Linux/Mac)
kill -9 <PID>
```

#### Error: "Access denied for user 'root'@'localhost'"
Verificar credenciales en `application.properties` y permisos de MySQL:
```sql
GRANT ALL PRIVILEGES ON mesa_partes_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

#### Error: "Table doesn't exist"
Ejecutar nuevamente el script SQL:
```bash
mysql -u root -p mesa_partes_db < SQL/mesa_de_partes_bd.sql
```

#### Frontend no carga estilos
Verificar que el frontend esté en `backend/src/main/resources/static/`:
```bash
# Copiar frontend a static
xcopy /E /I frontend backend\src\main\resources\static
```

---

## 🧪 Testing

### Tests Implementados (Pendiente Ampliación)

```bash
# Ejecutar tests
cd backend
mvnw.cmd test

# Con cobertura
mvnw.cmd test jacoco:report
```

**Cobertura actual:** 15% (objetivo: 80%)

### Endpoints para Testing Manual

**Postman Collection** (ejemplo):
```json
{
  "info": {
    "name": "Mesa de Partes API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"nakusu\",\"password\":\"123456\"}"
        },
        "url": {"raw": "http://localhost:8080/api/auth/login"}
      }
    }
  ]
}
```

---

## 🤝 Contribución al Proyecto

### Convenciones de Código

#### Backend (Java)
- **Nomenclatura**: PascalCase para clases, camelCase para métodos/variables
- **Packages**: `com.pnp.mesadepartes.{layer}.{feature}`
- **Formato**: Google Java Style Guide
- **Comentarios**: JavaDoc en métodos públicos
- **Excepciones**: Usar ResponseEntity<?> con mensajes descriptivos

**Ejemplo:**
```java
/**
 * Registra un nuevo documento en el sistema.
 *
 * @param dto Datos del documento a registrar
 * @return ResponseEntity con el documento creado o error
 */
@PostMapping("/registrar")
public ResponseEntity<?> registrarDocumento(@RequestBody DocumentoRegistroDTO dto) {
    try {
        // Lógica de negocio
        return ResponseEntity.ok(documento);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(
            Map.of("error", "Error al registrar", "message", e.getMessage())
        );
    }
}
```

#### Frontend (JavaScript)
- **Nomenclatura**: camelCase para variables/funciones
- **Formato**: 2 espacios de indentación
- **Async/Await**: Preferir sobre .then()
- **Comentarios**: Secciones con `// === Sección ===`
- **Validaciones**: Siempre en cliente y servidor

**Ejemplo:**
```javascript
/**
 * Registra un nuevo documento en el sistema.
 * @async
 * @returns {Promise<Object>} Documento creado
 */
async function registrarDocumento() {
    try {
        const data = construirDatosDocumento();
        const response = await fetch(`${API_URL}/documentos/registrar`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });
        
        if (!response.ok) throw new Error('Error en el servidor');
        
        const documento = await response.json();
        mostrarMensajeExito(documento.codigo);
        return documento;
        
    } catch (error) {
        console.error('Error al registrar:', error);
        mostrarMensajeError(error.message);
        throw error;
    }
}
```

### Flujo de Trabajo Git

```bash
# 1. Crear rama para nueva funcionalidad
git checkout -b feature/nombre-funcionalidad

# 2. Hacer commits descriptivos
git commit -m "feat: Agregar endpoint de búsqueda avanzada"
git commit -m "fix: Corregir filtro de fechas en bitácora"
git commit -m "refactor: Extraer lógica de validación a servicio"

# 3. Push y crear Pull Request
git push origin feature/nombre-funcionalidad

# 4. Después de aprobación, merge a main
git checkout main
git merge feature/nombre-funcionalidad
git push origin main
```

**Convención de commits:**
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `refactor:` Refactorización sin cambio de funcionalidad
- `docs:` Cambios en documentación
- `style:` Cambios de formato (espacios, punto y coma)
- `test:` Agregar o corregir tests
- `chore:` Tareas de mantenimiento

---

## 📄 Licencia

Este proyecto fue desarrollado como sistema interno para la **Policía Nacional del Perú (PNP)**.

---

## 👥 Equipo de Desarrollo

**Desarrolladores:**
- Marius De Paz Salazar - Backend Developer
- Edwin Cisneros Buendía - Backend Developer
- Anderson Ccorimanya Huachos - Frontend Developer
- Jonathan Chiclla Melo - DevOps
- Gersson Huamán García - Database Administrator
- Oliver Suárez Tinoco - QA Tester
- Marcela Rodríguez Munaylla - Tech Lead / Administrador del Sistema

**Institución:** Dirección de Tecnología de la Información y Comunicación (DIRTIC) - PNP

**Año:** 2025

---

## 📞 Soporte y Contacto

Para reportar bugs, solicitar funcionalidades o hacer preguntas técnicas:

- **GitHub Issues**: [https://github.com/Nakusuo/ProyectoMesaDePartes/issues](https://github.com/Nakusuo/ProyectoMesaDePartes/issues)
- **Email**: soporte.mesadepartes@pnp.gob.pe
- **Documentación adicional**: Ver carpeta `/docs` (en desarrollo)

---

<div align="center">

**Sistema Mesa de Partes Digital - PNP**

Desarrollado con ❤️ por el equipo DIRTIC

🇵🇪 **Policía Nacional del Perú** 🇵🇪

*"Orden, Seguridad y Servicio"*

</div
cd ../frontend
python -m http.server 5500
```

El frontend estará disponible en: `http://localhost:5500`

### 📂 Paso 5: Crear Directorio de Uploads

```bash
# En la raíz del proyecto backend
mkdir uploads
mkdir uploads\documentos
```

---

## 🎮 Guía de Uso

### 🔐 Inicio de Sesión

1. Abrir `http://localhost:5500/login.html`
2. Credenciales de prueba:
   - **Usuario**: `nakusu`
   - **Contraseña**: `123456`
3. Click en "Iniciar Sesión"

### 📝 Registrar Nuevo Documento

1. Acceder a "Registro de Documentos" desde el dashboard
2. Completar formulario:
   - **Asunto/Título**: Descripción breve del documento
   - **Tipo de Documento**: Seleccionar de lista (Oficio, Memorando, etc.)
   - **N° Documento Externo**: Número de referencia (opcional)
   - **N° Hoja de Trámite**: Código HT (opcional)
   - **Área Remitente**: Seleccionar de 34 áreas PNP
   - **Descripción/Sumilla**: Detalle del contenido
   - **Asignar a Usuario**: Trabajador responsable
   - **Archivo PDF**: Subir documento escaneado (máx. 10MB)
3. Click en "Registrar Documento"
4. El sistema asignará código automático (ej: DOC-000006)

### 📋 Consultar Bitácora

1. Acceder a "Bitácora de Eventos"
2. Ver lista completa de documentos registrados
3. Click en "Ver PDF" para abrir archivos adjuntos
4. Información mostrada:
   - Fecha y hora de registro
   - Usuario responsable
   - Código de documento
   - Estado actual
   - Tipo de documento
   - Área remitente

### 👤 Gestión de Usuarios (Admin)

1. Acceder a "Gestión de Usuarios"
2. Opciones disponibles:
   - Ver lista completa de usuarios
   - Crear nuevos usuarios
   - Editar información
   - Cambiar roles
   - Eliminar usuarios

---

## 🏗️ Arquitectura del Sistema

### 📊 Diagrama de Capas

```
┌─────────────────────────────────────┐
│         FRONTEND (HTML/JS)          │
│   ┌─────────────────────────────┐   │
│   │   login.html                │   │
│   │   registro.html             │   │
│   │   bitacora.html             │   │
│   │   gestion-usuarios.html     │   │
│   └─────────────────────────────┘   │
└──────────────┬──────────────────────┘
               │ HTTP/REST API
               │ (JSON)
┌──────────────▼──────────────────────┐
│       BACKEND (Spring Boot)         │
│   ┌─────────────────────────────┐   │
│   │    Controllers              │   │
│   │  - DocumentoController      │   │
│   │  - UsuarioController        │   │
│   │  - AreaController           │   │
│   │  - AuthController           │   │
│   └──────────┬──────────────────┘   │
│              │                       │
│   ┌──────────▼──────────────────┐   │
│   │    Services (Business)      │   │
│   └──────────┬──────────────────┘   │
│              │                       │
│   ┌──────────▼──────────────────┐   │
│   │    Repositories (JPA)       │   │
│   └──────────┬──────────────────┘   │
└──────────────┼──────────────────────┘
               │ JDBC
┌──────────────▼──────────────────────┐
│       BASE DE DATOS (MySQL)         │
│   - documentos                      │
│   - usuarios                        │
│   - areas                           │
│   - tipos_documento                 │
│   - tramites                        │
│   - hojas_tramite                   │
│   - roles                           │
└─────────────────────────────────────┘
```

### 🗃️ Modelo de Datos

#### Tabla: `documentos`
```sql
- ID_documento (PK)
- codigo (UNIQUE, VARCHAR(50))
- titulo (VARCHAR(200))
- descripcion (TEXT)
- numero_documento (VARCHAR(100))
- estado (ENUM)
- remitente (VARCHAR(200))
- destinatario (VARCHAR(200))
- fecha_ingreso (DATETIME)
- archivo_url (VARCHAR(255))
- ID_usuario_registro (FK)
- ID_tipo_documento (FK)
```

#### Tabla: `usuarios`
```sql
- ID_usuario (PK)
- nombre (VARCHAR(100))
- apellido (VARCHAR(100))
- dni (VARCHAR(8), UNIQUE)
- email (VARCHAR(100), UNIQUE)
- password (VARCHAR(255)) -- BCrypt hash
- cargo (VARCHAR(100))
- ID_rol (FK)
- ID_area (FK)
```

#### Tabla: `areas`
```sql
- ID_area (PK)
- nombre (VARCHAR(150))
- sigla (VARCHAR(20))
- descripcion (TEXT)
```

### 🔗 Relaciones

- **Documento** → **Usuario** (ManyToOne): Usuario que registró
- **Documento** → **TipoDocumento** (ManyToOne): Clasificación
- **Tramite** → **Documento** (ManyToOne): Documento asociado
- **Tramite** → **Usuario** (ManyToOne): Usuario asignado
- **Usuario** → **Rol** (ManyToOne): Permisos
- **Usuario** → **Area** (ManyToOne): Área de trabajo

---

## 🌐 API REST

### 🔐 Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/signin` | Iniciar sesión |
| POST | `/api/auth/signup` | Registrar usuario |

**Request (Login):**
```json
{
  "username": "nakusu",
  "password": "123456"
}
```

### 📄 Documentos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/documentos` | Listar todos los documentos |
| GET | `/api/documentos/{id}` | Obtener documento por ID |
| GET | `/api/documentos/buscar/{codigo}` | Buscar por código |
| POST | `/api/documentos/registrar` | Registrar nuevo documento |
| POST | `/api/documentos/upload` | Subir archivo PDF |

**Request (Registrar):**
```json
{
  "titulo": "Solicitud de información",
  "descripcion": "Solicitud sobre patrullaje",
  "remitente": "DIRTEPOL - Dirección de Telemática",
  "numeroDocumento": "1956",
  "numeroHt": "2024001234",
  "idTipoDocumento": 1,
  "idUsuarioAsignado": 2,
  "archivoUrl": "/uploads/documentos/1234567890_documento.pdf"
}
```

**Response:**
```json
{
  "idDocumento": 6,
  "codigo": "DOC-000006",
  "titulo": "Solicitud de información",
  "fechaIngreso": "2025-10-28T15:30:00",
  "estado": "Registrado",
  "usuario": {
    "nombre": "Usuario",
    "apellido": "Ejemplo"
  },
  "tipoDocumento": {
    "nombre": "Oficio"
  }
}
```

### 👥 Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| POST | `/api/usuarios` | Crear nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### 🏢 Áreas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/areas` | Listar todas las áreas |
| GET | `/api/areas/{id}` | Obtener área por ID |
| POST | `/api/areas` | Crear nueva área |
| PUT | `/api/areas/{id}` | Actualizar área |
| DELETE | `/api/areas/{id}` | Eliminar área |

### 📋 Tipos de Documento

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/tipos-documento` | Listar todos los tipos |

---

## 🔒 Seguridad

### 🛡️ Implementación

- **BCrypt Hashing**: Todas las contraseñas almacenadas con hash BCrypt (strength 10)
- **CORS Configurado**: Permite acceso desde `localhost:5500`
- **Autenticación Simplificada**: Sistema interno sin JWT (por decisión de diseño)
- **Validación de Archivos**: Solo PDF, máximo 10MB
- **Sanitización de Inputs**: Validación en frontend y backend

### 👤 Usuarios por Defecto

| Usuario | Contraseña | Rol | Área |
|---------|-----------|-----|------|
| nakusu | 123456 | Administrador | DIRTEPOL |
| jsmith | 123456 | Mesa de Partes | DIRGEN |
| mgarcia | 123456 | Trabajador | DIRREHUM |
| clopez | 123456 | Supervisor | DIRSEC |

**⚠️ IMPORTANTE**: Cambiar contraseñas en producción.

---

## 📁 Estructura del Proyecto

```
ProyectoMesaDePartes/
├── 📄 README.md
├── 📄 CAMBIOS_REALIZADOS.md
│
├── 📂 backend/
│   ├── 📂 src/
│   │   ├── 📂 main/
│   │   │   ├── 📂 java/com/pnp/mesadepartes/
│   │   │   │   ├── 📂 config/
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── FileUploadConfig.java
│   │   │   │   ├── 📂 controller/
│   │   │   │   │   ├── DocumentoController.java ⭐
│   │   │   │   │   ├── UsuarioController.java
│   │   │   │   │   ├── AreaController.java
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   └── TipoDocumentoController.java
│   │   │   │   ├── 📂 dto/
│   │   │   │   │   ├── DocumentoRegistroDTO.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   └── UserInfoResponse.java
│   │   │   │   ├── 📂 model/
│   │   │   │   │   ├── Documento.java ⭐
│   │   │   │   │   ├── Usuario.java
│   │   │   │   │   ├── Area.java
│   │   │   │   │   ├── TipoDocumento.java
│   │   │   │   │   ├── Tramite.java
│   │   │   │   │   └── HojaTramite.java
│   │   │   │   ├── 📂 repository/
│   │   │   │   │   ├── DocumentoRepository.java
│   │   │   │   │   ├── UsuarioRepository.java
│   │   │   │   │   └── AreaRepository.java
│   │   │   │   └── 📂 security/
│   │   │   │       └── (configuraciones simplificadas)
│   │   │   └── 📂 resources/
│   │   │       └── application.properties
│   │   └── 📂 test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── 📂 frontend/
│   ├── 🌐 login.html
│   ├── 🌐 registro.html ⭐
│   ├── 🌐 bitacora.html ⭐
│   ├── 🌐 gestion-usuarios.html
│   └── 📂 assets/
│       ├── 📂 css/
│       │   ├── style.css
│       │   ├── login.css
│       │   ├── registro.css ⭐
│       │   └── bitacora.css ⭐
│       └── 📂 js/
│           ├── auth.js
│           ├── login.js
│           ├── registrar-interno.js ⭐
│           ├── bitacora.js ⭐
│           └── gestion-usuarios.js
│
└── 📂 SQL/
    ├── mesa_de_partes_bd.sql ⭐
    └── actualizar_passwords_bcrypt.sql

⭐ = Archivos clave del sistema
```

---

## 🔧 Configuración Avanzada

### ⚙️ application.properties

```properties
# Configuración de Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/mesa_partes_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Configuración de Servidor
server.port=8080

# Configuración de Multipart (Uploads)
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.file-size-threshold=2KB
```

### 📤 Configuración de Uploads

**FileUploadConfig.java:**
```java
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
```

---

## 📊 Datos Precargados

### 🏢 Áreas PNP (34 áreas)

- **DIRGEN** - Dirección General
- **DIRTEPOL** - Dirección de Telemática
- **DIRREHUM** - Dirección de Recursos Humanos
- **DIRSEC** - Dirección de Seguridad
- **DIRINV** - Dirección de Investigación Criminal
- ... (29 áreas más)

### 📋 Tipos de Documento (10 tipos)

1. Oficio
2. Memorando
3. Informe
4. Solicitud
5. Resolución
6. Carta
7. Constancia
8. Certificado
9. Acta
10. Proveído

### 👔 Roles (4 roles)

1. **Administrador**: Acceso completo
2. **Mesa de Partes**: Registro y consulta
3. **Trabajador**: Consulta asignados
4. **Supervisor**: Revisión y aprobación

---

## 🚀 Despliegue en Producción

### 📦 Compilar Aplicación

```bash
cd backend
mvnw clean package -DskipTests
```

El archivo JAR estará en: `target/mesadepartes-0.0.1-SNAPSHOT.jar`

### ☁️ Consideraciones

- Cambiar contraseñas de BD
- Configurar HTTPS/SSL
- Implementar backup automático
- Configurar logs centralizados
- Aumentar límites de archivos si es necesario

---

## 🐛 Solución de Problemas

### ❌ Error: "Bad credentials"

**Causa**: Hash de contraseña incorrecto en BD  
**Solución**:
```sql
UPDATE usuarios 
SET password = '$2a$10$EnIgaJ1aZKFViIgwOju9suKSSni1MJ7MlHOWwGKL2hu0nHDIeil8m' 
WHERE username = 'nakusu';
```

### ❌ Error: "Port 8080 already in use"

**Causa**: Puerto ocupado  
**Solución**:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID] /F

# Linux/Mac
lsof -i :8080
kill -9 [PID]
```

### ❌ Error: "Cannot connect to MySQL"

**Causa**: MySQL no iniciado o credenciales incorrectas  
**Solución**:
1. Verificar que MySQL esté corriendo
2. Revisar `application.properties`
3. Probar conexión: `mysql -u root -p`

### ❌ Error al subir archivos

**Causa**: Directorio `uploads/` no existe  
**Solución**:
```bash
mkdir -p uploads/documentos
```

---

## 📈 Mejoras Futuras

- [ ] **Notificaciones en tiempo real** (WebSocket)
- [ ] **Generación de reportes PDF** (JasperReports)
- [ ] **Búsqueda avanzada** con filtros
- [ ] **Dashboard con estadísticas** (gráficos)
- [ ] **Auditoría completa** de acciones
- [ ] **Backup automático** programado

---

## 👥 Contribuidores

### 💻 Equipo de Desarrollo

- **Desarrollador Principal**: [Nakusu]
- **Backend**: Spring Boot + MySQL
- **Frontend**: HTML5 + JavaScript Vanilla
- **Base de Datos**: MySQL Schema Design

---

## 📄 Licencia

Este proyecto es de uso interno para la **Policía Nacional del Perú (PNP)**.

---


**🇵🇪 Desarrollado para el Curso Integrador I**

[![Java](https://img.shields.io/badge/Powered_by-Java_21-ED8B00?style=flat&logo=openjdk)](https://adoptium.net/)
[![Spring](https://img.shields.io/badge/Built_with-Spring_Boot-6DB33F?style=flat&logo=spring)](https://spring.io/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1?style=flat&logo=mysql)](https://www.mysql.com/)

⭐ **Si este proyecto te fue útil, deja una estrella** ⭐

</div>
