# 📋 Sistema Mesa de Partes Digital - PNP

<div align="center">

![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0.40-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

**Sistema de Gestión Documental para la Policía Nacional del Perú**

[Características](#-características-principales) • [Instalación](#-instalación-y-configuración) • [Uso](#-guía-de-uso) • [Arquitectura](#-arquitectura-del-sistema) • [API](#-api-rest)

</div>

---

## 📖 Descripción General

Sistema integral de Mesa de Partes Digital desarrollado para la **Policía Nacional del Perú (PNP)**, diseñado para optimizar la gestión, registro y seguimiento de documentos administrativos internos. El sistema elimina procesos manuales, reduce tiempos de respuesta y proporciona trazabilidad completa de todos los documentos institucionales.

### 🎯 Objetivos del Sistema

- ✅ **Digitalizar** el proceso de recepción y registro de documentos
- ✅ **Automatizar** la asignación de códigos y trámites
- ✅ **Centralizar** el almacenamiento de archivos PDF
- ✅ **Facilitar** el seguimiento de documentos mediante bitácora
- ✅ **Simplificar** la gestión de usuarios y áreas administrativas
- ✅ **Garantizar** la seguridad mediante autenticación con BCrypt

---

## 🚀 Características Principales

### 📄 Gestión de Documentos

- **Registro completo** con campos personalizados (título, descripción, remitente, etc.)
- **Códigos secuenciales** automáticos (DOC-000001, DOC-000002, ...)
- **Carga de archivos PDF** con validación de tipo y tamaño (máx. 10MB)
- **Almacenamiento seguro** con nombres únicos basados en timestamp
- **Asignación automática** de hojas de trámite (HT)
- **Clasificación por tipos** de documento preconfigurados

### 👥 Gestión de Usuarios

- **Autenticación segura** con contraseñas hasheadas (BCrypt)
- **Roles diferenciados**: Administrador, Mesa de Partes, Trabajador, Supervisor
- **7 usuarios precargados** con credenciales: `nakusu/123456`
- **Gestión completa** CRUD de usuarios

### 🏢 Gestión de Áreas

- **34 áreas PNP** precargadas con siglas oficiales
- **Selector dinámico** en formularios de registro
- **Formato oficial**: "SIGLA - Nombre Completo"

### 📊 Bitácora y Seguimiento

- **Registro histórico** de todos los documentos
- **Visualización en tiempo real** ordenada por fecha
- **Estados con badges**: Registrado, En Revisión, Aprobado, Rechazado, Archivado
- **Acceso directo** a archivos PDF desde la bitácora
- **Información completa**: usuario, fecha, tipo, remitente

### 🎨 Interfaz de Usuario

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

### 🔧 Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/ProyectoMesaDePartes.git
cd ProyectoMesaDePartes
```

### 🗄️ Paso 2: Configurar Base de Datos

1. **Iniciar MySQL** y conectarse:
```bash
mysql -u root -p
```

2. **Crear base de datos**:
```sql
CREATE DATABASE mesa_partes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mesa_partes_db;
```

3. **Ejecutar script de inicialización**:
```bash
mysql -u root -p mesa_partes_db < ProyectoMesaDePartes/SQL/mesa_de_partes_bd.sql
```

4. **Verificar configuración** en `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mesa_partes_db
spring.datasource.username=root
spring.datasource.password=root
```

### ⚙️ Paso 3: Compilar y Ejecutar Backend

```bash
cd ProyectoMesaDePartes/backend

# En Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run

# En Linux/Mac
./mvnw clean install
./mvnw spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

### 🌐 Paso 4: Iniciar Frontend

```bash
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
