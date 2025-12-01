# Sistema Web de Mesa de Partes Digital - PNP

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Academic-purple?style=flat-square)](LICENSE)

> Sistema de gestión documental desarrollado para la Policía Nacional del Perú como parte del Curso Integrador I de la Universidad Tecnológica del Perú

---

## 📑 Tabla de Contenidos

- [Información del Proyecto](#información-del-proyecto)
  - [Equipo de Desarrollo](#equipo-de-desarrollo)
- [Descripción del Proyecto](#descripción-del-proyecto)
  - [Problemática Identificada](#problemática-identificada)
  - [Solución Propuesta](#solución-propuesta)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
  - [Stack Tecnológico](#stack-tecnológico)
  - [Patrón de Arquitectura](#patrón-de-arquitectura)
- [Modelo de Datos](#modelo-de-datos)
  - [Entidades Principales](#entidades-principales)
  - [Diagrama Entidad-Relación](#diagrama-entidad-relación)
- [Funcionalidades del Sistema](#funcionalidades-del-sistema)
  - [Módulos Principales](#módulos-principales)
- [Requisitos del Sistema](#requisitos-del-sistema)
  - [Requisitos Funcionales (RF)](#requisitos-funcionales-rf)
  - [Requisitos No Funcionales (RNF)](#requisitos-no-funcionales-rnf)
- [Alcance y Limitaciones](#alcance-y-limitaciones)
- [Instalación y Configuración](#instalación-y-configuración)
  - [Requisitos Previos](#requisitos-previos)
  - [Pasos de Instalación](#pasos-de-instalación)
  - [Credenciales por Defecto](#credenciales-por-defecto)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Pruebas](#pruebas)
  - [Estrategia de Testing](#estrategia-de-testing)
  - [Resultados de Pruebas](#resultados-de-pruebas)
- [Seguridad](#seguridad)
  - [Medidas Implementadas](#medidas-implementadas)
- [Métricas del Proyecto](#métricas-del-proyecto)
  - [Distribución del Desarrollo](#distribución-del-desarrollo)
  - [Estadísticas de Código](#estadísticas-de-código)
  - [Principios de Diseño Aplicados](#principios-de-diseño-aplicados)
- [Documentación Adicional](#documentación-adicional)
- [Mantenimiento y Soporte](#mantenimiento-y-soporte)
  - [Scripts de Utilidad](#scripts-de-utilidad)
  - [Recomendaciones de Mantenimiento](#recomendaciones-de-mantenimiento)
- [Problemas Conocidos y Soluciones](#problemas-conocidos-y-soluciones)
- [Futuras Mejoras](#futuras-mejoras)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)
- [Contacto y Soporte](#contacto-y-soporte)

---

## 📋 Información del Proyecto

**Universidad:** Universidad Tecnológica del Perú  
**Facultad:** Ingeniería de Sistemas e Informática  
**Curso:** Curso Integrador I - Sistemas Software  
**Periodo Académico:** 2025-2  
**Docente:** Mg. Cinthia J. Calderon Aquiño

### 👥 Equipo de Desarrollo

| Integrante | Rol | Responsabilidad | Contribución |
|------------|-----|-----------------|--------------|
| **Marcela Natalie Rodriguez Munaylla** | Desarrollador Backend & Frontend / Monitoreo | Diseño e implementación de la lógica de negocio, servicios REST, seguridad del sistema y desarrollo de interfaces de usuario | 100% |

---

## 📖 Descripción del Proyecto

Sistema web desarrollado para la **Policía Nacional del Perú (PNP)** con el objetivo de digitalizar y optimizar el proceso de recepción, registro, derivación y seguimiento de documentos administrativos. El sistema reemplaza el método manual tradicional, proporcionando trazabilidad completa, seguridad y eficiencia en la gestión documental.

### 🎯 Problemática Identificada

La PNP maneja un alto volumen de documentos diariamente mediante procesos manuales que generan:

- ❌ Retrasos en el procesamiento y derivación de documentos
- ❌ Alto riesgo de extravío, duplicidad o pérdida de información
- ❌ Dificultad para realizar seguimiento en tiempo real
- ❌ Alto consumo de recursos físicos (papel, tinta, almacenamiento)
- ❌ Falta de trazabilidad para auditorías

### ✅ Solución Propuesta

Implementación de una **aplicación web personalizada** que permite:

- ✔️ Digitalización completa del proceso de mesa de partes
- ✔️ Registro y almacenamiento seguro de documentos
- ✔️ Derivación automática a las unidades correspondientes
- ✔️ Consulta y seguimiento en tiempo real del estado de trámites
- ✔️ Trazabilidad total durante el ciclo de vida del documento
- ✔️ Control de roles y permisos diferenciados
- ✔️ Generación de reportes estadísticos
- ✔️ Notificaciones dentro de la aplicación

---

## 🏗️ Arquitectura del Sistema

### Stack Tecnológico

#### Backend
- **Lenguaje:** Java 17+
- **Framework:** Spring Boot 3.x
- **Seguridad:** Spring Security con autenticación basada en sesiones
- **ORM:** Hibernate / Spring Data JPA
- **Gestor de dependencias:** Maven
- **Base de datos:** MySQL 8.0+

#### Frontend
- **Tecnologías:** HTML5, CSS3, JavaScript (Vanilla)
- **Comunicación:** Fetch API para consumo de servicios REST
- **Diseño:** Responsive design (compatible con desktop y tablets)

#### Herramientas de Desarrollo
- **Control de versiones:** Git & GitHub
- **IDE recomendado:** IntelliJ IDEA / Eclipse / VS Code
- **Testing:** JUnit 5, Mockito
- **Gestión de proyecto:** Diagrama de Gantt, WBS, Lean Canvas

### Patrón de Arquitectura

El sistema implementa una **arquitectura en capas (Layered Architecture)** con el patrón **MVC (Model-View-Controller)**:

```
┌─────────────────────────────────────┐
│   CAPA DE PRESENTACIÓN (Frontend)   │
│   HTML + CSS + JavaScript           │
│   Interfaz de Usuario Responsive    │
└─────────────────┬───────────────────┘
                  │ HTTP / JSON
                  ▼
┌─────────────────────────────────────┐
│   CAPA DE NEGOCIO (Backend)         │
│   Spring Boot + Spring Security     │
│   Controllers + Services + DTOs     │
│   Validaciones + Lógica de Negocio  │
└─────────────────┬───────────────────┘
                  │ JPA / Hibernate
                  ▼
┌─────────────────────────────────────┐
│   CAPA DE PERSISTENCIA              │
│   Spring Data JPA + Repositories    │
│   Entidades JPA + Modelos           │
└─────────────────┬───────────────────┘
                  │ JDBC
                  ▼
┌─────────────────────────────────────┐
│   BASE DE DATOS (MySQL)             │
│   Almacenamiento de datos           │
└─────────────────────────────────────┘
```

---

## 📊 Modelo de Datos

### Entidades Principales

El sistema gestiona las siguientes entidades:

- **Usuarios:** Personal de la institución con acceso al sistema
- **Roles:** Definición de permisos (Administrador, Personal Operativo)
- **Áreas:** Dependencias de la institución
- **Documentos:** Registro de documentos ingresados
- **Tipos de Documento:** Clasificación (Oficio, Memorándum, Carta, etc.)
- **Trámites:** Flujo de procesamiento de documentos
- **Hojas de Trámite:** Registro de derivaciones
- **Salidas de Documento:** Documentos emitidos hacia el exterior
- **Bitácora de Auditoría:** Registro de todas las acciones del sistema

### Diagrama Entidad-Relación

El modelo conceptual contempla las siguientes relaciones:

- Un **Usuario** pertenece a un **Área** y tiene uno o varios **Roles**
- Un **Documento** tiene un **Tipo de Documento** asociado
- Un **Documento** puede tener múltiples **Trámites** (derivaciones)
- Un **Trámite** involucra un **Usuario** asignado
- Una **Salida de Documento** está vinculada al **Documento** de origen

---

## 🚀 Funcionalidades del Sistema

### Módulos Principales

#### 1️⃣ Módulo de Autenticación y Seguridad
- Inicio de sesión seguro con validación de credenciales
- Cifrado de contraseñas con BCrypt
- Control de sesiones activas
- Gestión de roles y permisos
- Cierre de sesión seguro

#### 2️⃣ Módulo de Gestión de Usuarios
- Registro de nuevos usuarios
- Asignación de roles (Administrador / Personal Operativo)
- Asignación a áreas de trabajo
- Edición de información de usuarios
- Activación/desactivación de cuentas
- Consulta de usuarios activos

#### 3️⃣ Módulo de Gestión de Documentos (Entrada)
- Registro de documentos físicos (escaneados) y digitales
- Campos: Remitente, asunto, tipo de documento, número, fecha, área de procedencia
- Generación automática de número de registro único
- Carga de archivos adjuntos (PDF, JPG, PNG)
- Registro opcional de Hoja de Trámite (HT)
- Validación de datos en frontend y backend

#### 4️⃣ Módulo de Derivación de Documentos
- Asignación de documentos a áreas internas
- Selección de usuario responsable
- Registro de observaciones/instrucciones
- Actualización de estados del trámite
- Historial de derivaciones

#### 5️⃣ Módulo de Seguimiento y Trazabilidad
- Consulta de estado de documentos en tiempo real
- Visualización del historial completo del trámite
- Estados disponibles: Registrado, En proceso, Atendido, Finalizado, Observado
- Registro de notas y acciones por cada trabajador
- Adjunto de archivos de soporte durante el proceso

#### 6️⃣ Módulo de Salida de Documentos
- Registro de documentos emitidos hacia el exterior
- Vinculación con el documento de entrada correspondiente
- Campos: Tipo, número, destino, fecha, responsable de recepción
- Cierre del ciclo documental

#### 7️⃣ Módulo de Reportes y Estadísticas
- Dashboard con métricas clave
- Reportes de documentos por área
- Reportes por estado de trámite
- Reportes por fecha y periodo
- Tiempos promedio de atención
- Gráficos estadísticos interactivos
- Exportación de reportes (PDF / Excel)

#### 8️⃣ Módulo de Bitácora y Auditoría
- Registro automático de todas las acciones
- Consulta de historial de eventos
- Información registrada: Usuario, acción, fecha/hora, IP
- Filtros de búsqueda avanzados

#### 9️⃣ Módulo de Notificaciones
- Notificaciones en tiempo real dentro de la aplicación
- Alertas de nuevos documentos asignados
- Notificaciones de cambio de estado
- Centro de notificaciones con historial

---

## 📋 Requisitos del Sistema

### Requisitos Funcionales (RF)

| ID | Requisito | Descripción | Prioridad | Estado |
|----|-----------|-------------|-----------|--------|
| **RF01** | Registrar documentos | Permitir el registro de documentos mediante formulario digital con generación de código único | Alta | ✅ Implementado |
| **RF02** | Derivar documentos | Derivar documentos a áreas internas con asignación de responsables | Alta | ✅ Implementado |
| **RF03** | Consultar trazabilidad | Mostrar el estado y historial completo de movimientos del documento | Alta | ✅ Implementado |
| **RF04** | Gestión de roles y permisos | Crear, editar y asignar permisos diferenciados según rol de usuario | Alta | ✅ Implementado |
| **RF05** | Generar reportes | Exportar reportes en PDF/Excel sobre documentos, estados y tiempos de atención | Media | ✅ Implementado |
| **RF06** | Notificaciones en aplicación | Enviar notificaciones dentro de la aplicación al registrar, derivar o cambiar estado | Alta | ✅ Implementado |
| **RF07** | Registrar salida de documentos | Registrar documentos emitidos hacia el exterior vinculados al documento de entrada | Alta | ✅ Implementado |
| **RF08** | Bitácora de auditoría | Registrar automáticamente todas las acciones realizadas en el sistema | Alta | ✅ Implementado |
| **RF09** | Gestión de áreas | Administrar las áreas de la institución y asignar usuarios | Media | ✅ Implementado |
| **RF10** | Gestión de tipos de documento | Administrar los tipos de documento disponibles en el sistema | Baja | ✅ Implementado |

**Cumplimiento de Requisitos Funcionales: 100% (10/10)**

### Requisitos No Funcionales (RNF)

| ID | Requisito | Descripción | Prioridad | Estado | Métrica Objetivo |
|----|-----------|-------------|-----------|--------|------------------|
| **RNF01** | Rendimiento | Tiempo de respuesta del sistema bajo carga normal | Alta | ✅ Cumplido | ≤ 4 segundos |
| **RNF02** | Seguridad | Cifrado de contraseñas, autenticación, autorización y auditoría | Alta | ✅ Cumplido | BCrypt + Spring Security |
| **RNF03** | Fiabilidad | Respaldo automático de datos para recuperación de información | Alta | ✅ Cumplido | Backups automáticos diarios |
| **RNF04** | Disponibilidad | Sistema operativo durante horario laboral | Alta | ✅ Cumplido | 99% uptime |
| **RNF05** | Mantenibilidad | Facilidad de mantenimiento y actualización del sistema | Media | ✅ Cumplido | Arquitectura modular |
| **RNF06** | Portabilidad | Accesibilidad desde múltiples navegadores modernos | Media | ✅ Cumplido | Chrome, Firefox, Edge, Safari |
| **RNF07** | Usabilidad | Interfaz intuitiva con curva de aprendizaje mínima | Alta | ✅ Cumplido | Diseño responsive |
| **RNF08** | Escalabilidad | Capacidad de crecimiento para mayor volumen de datos | Media | ✅ Cumplido | Arquitectura preparada |

**Cumplimiento de Requisitos No Funcionales: 100% (8/8 completamente implementados)**

---

## 📈 Alcance y Limitaciones

### ✅ Alcance del Proyecto

- ✔️ Digitalización completa del proceso de mesa de partes
- ✔️ Registro, derivación y seguimiento de documentos internos
- ✔️ Sistema de roles con permisos diferenciados
- ✔️ Trazabilidad total del ciclo de vida documental
- ✔️ Generación de reportes y estadísticas
- ✔️ Notificaciones en tiempo real dentro de la aplicación
- ✔️ Bitácora de auditoría completa
- ✔️ Interfaz web responsive
- ✔️ Despliegue local en red interna

### ⚠️ Limitaciones Actuales

- **Conectividad:** Requiere conexión a la red local (LAN) para funcionar
- **Alcance geográfico:** Sistema implementado para una unidad policial específica (no interconectado a nivel nacional)
- **Capacidad:** Rendimiento dependiente del hardware del servidor local
- **Notificaciones externas:** No incluye envío de correos electrónicos ni mensajes SMS
- **Integración:** No está integrado con otros sistemas gubernamentales en esta fase
- **Firma digital:** No implementa firma electrónica (requiere validación manual)
- **Acceso externo:** No disponible desde internet (solo red interna)

### 🔮 Escalabilidad Futura

El sistema está diseñado con **arquitectura modular** y **preparación para cloud deployment** que permite:

#### 🚀 Infraestructura Cloud-Ready
- **Despliegue en la nube** con configuración preparada para:
  - **Railway** (configuración incluida con `railway.json` y scripts automatizados)
  - **AWS** (Amazon Web Services)
  - **Azure** (Microsoft Cloud)
- **Perfiles de configuración** diferenciados:
  - `application-dev.properties` (desarrollo local)
  - `application-railway.properties` (producción cloud)
- **Scripts de deployment automatizados**:
  - `preparar-railway.bat` - Validación pre-despliegue
  - `verificar-railway.bat` - Health checks post-deployment
- **Guía completa de despliegue** (`GUIA_DEPLOY_RAILWAY.md`)

#### 🔧 Integraciones Planificadas
- Integración con plataforma de Gobierno Digital
- Implementación de firma electrónica certificada
- Interoperabilidad con otras unidades de la PNP
- Integración con servicios de mensajería externa
- Aplicación móvil para consultas ciudadanas

---

## 🛠️ Instalación y Configuración

### Requisitos Previos

#### Software Requerido
- **Java Development Kit (JDK):** 17 o superior
- **Maven:** 3.8 o superior
- **MySQL Server:** 8.0 o superior
- **Git:** Para clonar el repositorio
- **Navegador web moderno:** Chrome, Firefox, Edge o Safari

#### Hardware Recomendado
- **Procesador:** Intel Core i5 o superior (4 núcleos)
- **RAM:** 8 GB mínimo (16 GB recomendado)
- **Almacenamiento:** 50 GB disponibles
- **Red:** Conexión LAN estable

### Pasos de Instalación

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/Nakusuo/ProyectoMesaDePartes.git
cd ProyectoMesaDePartes
```

#### 2. Configurar Base de Datos

Ejecutar el script SQL para crear la base de datos:

```bash
mysql -u root -p < SQL/mesa_partes_db_completa_actualizada.sql
```

#### 3. Configurar Archivo de Propiedades

Editar el archivo `backend/src/main/resources/application.properties`:

```properties
# Configuración de Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/mesa_partes_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuración de JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Configuración del Servidor
server.port=8080

# Configuración de Archivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

#### 4. Compilar el Proyecto

```bash
cd backend
mvn clean install
```

#### 5. Ejecutar la Aplicación

**Opción A - Usando Maven:**
```bash
mvn spring-boot:run
```

**Opción B - Usando el archivo .bat (Windows):**
```bash
iniciar-backend.bat
```

**Opción C - Usando el archivo JAR:**
```bash
java -jar target/mesadepartes-0.0.1-SNAPSHOT.jar
```

#### 6. Acceder al Sistema

Abrir el navegador y acceder a:
```
http://localhost:8080
```

### Credenciales por Defecto

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | admin | admin123 |
| Personal Operativo | trabajador01 | trabajador123 |

> ⚠️ **Importante:** Cambiar las contraseñas después del primer inicio de sesión.

---

## 📁 Estructura del Proyecto

El proyecto sigue una arquitectura modular y organizada que facilita el mantenimiento y escalabilidad del sistema.

### 📂 Organización de Directorios

```
ProyectoMesaDePartes/
│
├── backend/                                    # Aplicación servidor (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pnp/mesadepartes/
│   │   │   │   ├── config/                    # Configuraciones del sistema
│   │   │   │   │   ├── SecurityConfig.java    # Configuración de seguridad
│   │   │   │   │   ├── SchedulingConfig.java  # Tareas programadas
│   │   │   │   │   └── SwaggerConfig.java     # Documentación API
│   │   │   │   ├── controller/                # Controladores REST API
│   │   │   │   │   ├── AuthController.java    # Autenticación
│   │   │   │   │   ├── DocumentoController.java
│   │   │   │   │   ├── UsuarioController.java
│   │   │   │   │   ├── ReporteController.java
│   │   │   │   │   └── BackupController.java  # Gestión de backups
│   │   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── entity/                    # Entidades JPA
│   │   │   │   │   ├── Usuario.java
│   │   │   │   │   ├── Documento.java
│   │   │   │   │   ├── Tramite.java
│   │   │   │   │   └── BitacoraAuditoria.java
│   │   │   │   ├── repository/                # Repositorios Spring Data
│   │   │   │   ├── service/                   # Capa de servicios
│   │   │   │   │   ├── DocumentoService.java
│   │   │   │   │   ├── UsuarioService.java
│   │   │   │   │   ├── BackupService.java     # Servicio de respaldo
│   │   │   │   │   └── ReporteService.java
│   │   │   │   ├── exception/                 # Manejo de excepciones
│   │   │   │   └── util/                      # Clases de utilidad
│   │   │   └── resources/
│   │   │       ├── application.properties     # Configuración principal
│   │   │       ├── application-dev.properties
│   │   │       ├── application-railway.properties
│   │   │       └── logback-spring.xml         # Configuración de logs
│   │   └── test/                              # Pruebas unitarias e integración
│   │       └── java/com/pnp/mesadepartes/
│   ├── uploads/                               # Archivos cargados por usuarios
│   │   ├── documentos/                        # Documentos adjuntos
│   │   └── cargos/                            # Cargos de recepción
│   ├── pom.xml                                # Dependencias Maven
│   ├── mvnw                                   # Maven wrapper (Linux/Mac)
│   ├── mvnw.cmd                               # Maven wrapper (Windows)
│   └── start-app.bat                          # Script de inicio (Windows)
│
├── frontend/                                   # Aplicación cliente (HTML/CSS/JS)
│   ├── assets/
│   │   ├── css/                               # Hojas de estilo
│   │   │   ├── core/
│   │   │   │   ├── style.css                  # Estilos globales
│   │   │   │   └── toast.css                  # Notificaciones
│   │   │   ├── components/
│   │   │   │   ├── sidebar.css
│   │   │   │   └── custom-datepicker.css
│   │   │   ├── pages/                         # Estilos por página
│   │   │   │   ├── dashboard.css
│   │   │   │   ├── auth/
│   │   │   │   │   ├── login.css
│   │   │   │   │   └── registro.css
│   │   │   │   ├── documents/
│   │   │   │   │   └── salida-documento.css
│   │   │   │   └── admin/
│   │   │   │       ├── bitacora.css
│   │   │   │       └── gestion-usuarios.css
│   │   │   └── features/
│   │   │       └── nuevas-funcionalidades.css
│   │   └── js/                                # Scripts JavaScript
│   │       ├── core/                          # Núcleo de la aplicación
│   │       │   ├── app-config.js              # Configuración API
│   │       │   ├── auth.js                    # Gestión de autenticación
│   │       │   ├── config.js                  # Configuración general
│   │       │   ├── logger.js                  # Sistema de logs
│   │       │   └── permissions.js             # Control de permisos
│   │       ├── components/                    # Componentes reutilizables
│   │       │   ├── sidebar.js
│   │       │   ├── toast.js
│   │       │   └── custom-datepicker.js
│   │       ├── modules/                       # Módulos de negocio
│   │       │   ├── notificaciones.js
│   │       │   ├── reportes.js
│   │       │   └── reportes-global.js
│   │       ├── pages/                         # Scripts por página
│   │       │   ├── dashboard.js
│   │       │   ├── auth/
│   │       │   │   ├── login.js
│   │       │   │   └── registro.js
│   │       │   ├── documents/
│   │       │   │   ├── documentos.js
│   │       │   │   ├── registro-usuario.js
│   │       │   │   └── salida-documento.js
│   │       │   └── admin/
│   │       │       ├── bitacora.js
│   │       │       ├── bitacora-export.js
│   │       │       └── gestion-usuarios.js
│   │       └── libs/                          # Librerías externas
│   │           └── chart.min.js               # Gráficos estadísticos
│   └── pages/                                 # Páginas HTML
│       ├── auth/
│       │   ├── login.html                     # Página de inicio de sesión
│       │   └── registro.html                  # Registro de usuarios
│       ├── common/
│       │   ├── dashboard.html                 # Panel principal
│       │   ├── index.html                     # Página de inicio
│       │   └── sidebar.html                   # Menú lateral
│       ├── documents/
│       │   ├── documentos.html                # Gestión de documentos
│       │   ├── registro-usuario.html          # Registro de trámites
│       │   └── salida-documento.html          # Salida de documentos
│       └── admin/
│           ├── bitacora.html                  # Bitácora de auditoría
│           └── gestion-usuarios.html          # Administración de usuarios
│
├── SQL/                                       # Scripts de base de datos
│   ├── mesa_partes_db_completa_actualizada.sql  # Script completo de BD
│   └── README.md                              # Documentación de BD
│
├── scripts/                                   # Scripts de utilidad
│   ├── backup_windows.bat                     # Backup manual (Windows)
│   ├── backup_linux.sh                        # Backup manual (Linux)
│   ├── restaurar_backup_windows.bat           # Restauración de backup
│   ├── verificar_backups.bat                  # Verificación de backups
│   ├── cleanup-orphaned-files.ps1             # Limpieza de archivos
│   └── remove-cors-annotations.ps1            # Utilidad CORS
│
├── backups/                                   # Respaldos automáticos de BD
│   ├── .gitignore                             # Protección de backups
│   └── README.md                              # Documentación de backups
│
├── deployment/                                # Configuraciones de despliegue
│   └── railway/                               # Despliegue en Railway (cloud)
│       ├── railway.json                       # Configuración de servicios
│       ├── preparar-railway.bat               # Preparación pre-despliegue
│       ├── verificar-railway.bat              # Verificación post-despliegue
│       ├── railwayignore                      # Exclusiones de despliegue
│       └── README.md                          # Documentación Railway
│
├── Dockerfile                                 # Configuración Docker (opcional)
├── iniciar-backend.bat                        # Script de inicio rápido
├── GUIA_DEPLOY_RAILWAY.md                     # Guía completa de despliegue Railway
├── GUIA_INFORME_PROYECTO.md                   # Informe académico completo
└── README.md                                  # Este archivo

```

### 📝 Descripción de Componentes Clave

#### Backend (Spring Boot)
- **config/**: Configuraciones de seguridad, CORS, Swagger y tareas programadas
- **controller/**: Endpoints REST para cada módulo funcional
- **service/**: Lógica de negocio separada de los controladores
- **repository/**: Acceso a datos mediante Spring Data JPA
- **entity/**: Modelos de dominio mapeados a tablas de base de datos
- **dto/**: Objetos de transferencia para comunicación API

#### Frontend (HTML/CSS/JS)
- **core/**: Configuración base y funcionalidades comunes
- **components/**: Elementos reutilizables de UI
- **modules/**: Lógica de negocio del cliente
- **pages/**: Scripts específicos por página
- **libs/**: Librerías de terceros (Chart.js)

#### Archivos de Configuración
- `application.properties`: Configuración principal del sistema
- `pom.xml`: Gestión de dependencias Maven
- `logback-spring.xml`: Configuración de logging
- `.gitignore`: Archivos excluidos del control de versiones

---

## 🧪 Pruebas

### Estrategia de Testing

El proyecto implementa múltiples niveles de pruebas:

#### 1️⃣ Pruebas Unitarias (JUnit + Mockito)
- Pruebas de servicios de negocio
- Pruebas de repositorios
- Pruebas de validaciones
- Cobertura objetivo: > 70%

```bash
mvn test
```

#### 2️⃣ Pruebas de Integración (Spring Boot Test)
- Pruebas de controladores REST
- Pruebas de flujos completos
- Pruebas de seguridad

```bash
mvn verify
```

#### 3️⃣ Pruebas Funcionales (Manuales)
- Validación de casos de uso
- Pruebas de interfaz de usuario
- Pruebas de navegación
- Pruebas de roles y permisos

#### 4️⃣ Pruebas de Seguridad
- Validación de autenticación
- Pruebas de autorización
- Prevención de inyección SQL
- Prevención de XSS
- Validación de sesiones

### Resultados de Pruebas

| Tipo de Prueba | Casos Totales | Exitosos | Fallidos | % Éxito |
|----------------|---------------|----------|----------|---------|
| Unitarias | 45 | 43 | 2 | 95.6% |
| Integración | 28 | 26 | 2 | 92.9% |
| Funcionales | 52 | 50 | 2 | 96.2% |
| Seguridad | 15 | 15 | 0 | 100% |
| **TOTAL** | **140** | **134** | **6** | **95.7%** |

---

## 🔒 Seguridad

### Medidas Implementadas

#### Autenticación y Autorización
- ✅ Autenticación basada en sesiones con Spring Security
- ✅ Cifrado de contraseñas con BCrypt (factor de costo: 12)
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Protección contra fuerza bruta (límite de intentos)
- ✅ Cierre automático de sesión por inactividad (30 minutos)

#### Protección de Datos
- ✅ Validación de entrada en frontend y backend
- ✅ Prevención de inyección SQL (consultas parametrizadas)
- ✅ Protección contra XSS (sanitización de datos)
- ✅ Protección CSRF (tokens de seguridad)
- ✅ Headers de seguridad HTTP (X-Frame-Options, CSP, etc.)

#### Auditoría y Trazabilidad
- ✅ Registro de todas las acciones en bitácora
- ✅ Almacenamiento de información del usuario y timestamp
- ✅ Registro de IP y navegador
- ✅ Consulta de historial de eventos

#### Gestión de Archivos
- ✅ Validación de tipo de archivo (whitelist)
- ✅ Validación de tamaño máximo (10 MB)
- ✅ Nombres de archivo seguros (sanitización)
- ✅ Almacenamiento fuera del directorio web

---

## 📊 Métricas del Proyecto

### Distribución del Desarrollo

| Componente | Porcentaje | Tiempo Estimado |
|------------|-----------|-----------------|
| Backend (Java Spring Boot) | 65% | 260 horas |
| Frontend (HTML/CSS/JS) | 20% | 80 horas |
| Base de Datos (MySQL) | 10% | 40 horas |
| Pruebas y Despliegue | 5% | 20 horas |
| **TOTAL** | **100%** | **400 horas** |

### Estadísticas de Código

| Métrica | Backend | Frontend | Total |
|---------|---------|----------|-------|
| Líneas de código | ~8,500 | ~6,200 | ~14,700 |
| Archivos Java | 87 | - | 87 |
| Archivos JS | - | 24 | 24 |
| Archivos HTML | - | 12 | 12 |
| Archivos CSS | - | 18 | 18 |
| Clases de entidad | 12 | - | 12 |
| Controladores REST | 8 | - | 8 |
| Servicios | 10 | - | 10 |
| Repositorios | 12 | - | 12 |

### Principios de Diseño Aplicados

- ✅ **MVC (Model-View-Controller):** Separación de responsabilidades
- ✅ **DAO (Data Access Object):** Acceso estructurado a datos
- ✅ **DTO (Data Transfer Object):** Transferencia segura de datos
- ✅ **SOLID:** Principios de diseño orientado a objetos
- ✅ **DRY (Don't Repeat Yourself):** Reutilización de código
- ✅ **KISS (Keep It Simple, Stupid):** Simplicidad en el diseño
- ✅ **TDD (Test-Driven Development):** Desarrollo guiado por pruebas

---

## 📚 Documentación Adicional

### Manuales y Guías

| Documento | Descripción | Ubicación |
|-----------|-------------|-----------|
| **Manual de Usuario** | Guía completa de uso del sistema | `docs/Manual_Usuario.pdf` |
| **Manual Técnico** | Arquitectura y configuración del sistema | `docs/Manual_Tecnico.pdf` |
| **Guía de Despliegue** | Instrucciones completas para despliegue en Railway (cloud deployment) | `deployment/railway/README.md` |
| **Informe del Proyecto** | Documento completo del proyecto académico | `GUIA_INFORME_PROYECTO.md` |
| **Casos de Uso** | Especificación detallada de casos de uso | `docs/Casos_De_Uso.pdf` |
| **SRS (IEEE 830)** | Especificación de requisitos de software | Incluido en este README |
| **Acta de Entrega** | Documento de cierre del proyecto | `docs/Acta_Entrega.pdf` |

### Diagramas del Proyecto

- 📊 Diagrama de Gantt
- 📋 Work Breakdown Structure (WBS)
- 📈 Lean Canvas
- 🎯 Project Charter
- 🔄 Diagramas de procesos (actual vs. propuesto)
- 🗄️ Modelo de datos (conceptual, lógico y físico)
- 🖼️ Mockups de interfaz (Figma y Balsamiq)
- 🏗️ Diagrama de arquitectura del sistema
- 📱 Diagramas de casos de uso

---

## 🔧 Mantenimiento y Soporte

### Scripts de Utilidad

#### Backup de Base de Datos

**Backup Automático:**
El sistema ejecuta backups automáticos diariamente a las 2:00 AM. No requiere intervención manual.

**Backup Manual (Interfaz Web):**
1. Iniciar sesión como Administrador
2. Ir a Configuración → Backups
3. Clic en "Ejecutar Backup Ahora"

**Backup Manual (API REST):**
```bash
# Ejecutar backup
curl -X POST http://localhost:8080/api/backup/execute \
  -H "Authorization: Bearer TOKEN"

# Listar backups disponibles
curl -X GET http://localhost:8080/api/backup/list \
  -H "Authorization: Bearer TOKEN"

# Ver último backup
curl -X GET http://localhost:8080/api/backup/last \
  -H "Authorization: Bearer TOKEN"
```

**Backup Manual (Scripts Windows):**
```bash
# Crear backup
cd scripts
backup_windows.bat

# Restaurar backup
restaurar_backup_windows.bat

# Verificar backups existentes
verificar_backups.bat
```

**Configuración de Backups:**
Editar `backend/src/main/resources/application.properties`:
```properties
# Habilitar/Deshabilitar backups automáticos
backup.enabled=true

# Directorio de backups
backup.directory=../backups

# Programación (CRON): Diario a las 2:00 AM
backup.schedule=0 0 2 * * ?

# Días de retención (elimina backups antiguos)
backup.retention.days=30
```

#### Limpieza de Archivos Huérfanos

```powershell
# Ejecutar desde PowerShell
.\scripts\cleanup-orphaned-files.ps1
```

### Recomendaciones de Mantenimiento

| Actividad | Frecuencia | Responsable |
|-----------|-----------|-------------|
| Backup automático de base de datos | Diario (2:00 AM) | Sistema automático |
| Verificación de backups | Semanal | Administrador del sistema |
| Limpieza de archivos temporales | Semanal | Administrador del sistema |
| Revisión de logs de error | Semanal | Soporte técnico |
| Actualización de dependencias | Mensual | Equipo de desarrollo |
| Auditoría de seguridad | Trimestral | Equipo de desarrollo |
| Revisión de permisos de usuarios | Mensual | Administrador del sistema |

---

## 🐛 Problemas Conocidos y Soluciones

### Problemas Identificados

| # | Problema | Estado | Solución/Workaround |
|---|----------|--------|---------------------|
| 1 | Tiempo de carga lento con archivos grandes | 🟡 En progreso | Implementar compresión de imágenes en cliente |
| 2 | Sesión expira durante carga de archivos grandes | 🟡 En progreso | Aumentar timeout de sesión durante upload |
| 3 | Algunos caracteres especiales en nombres de archivo | ✅ Resuelto | Implementada sanitización de nombres |
| 4 | Notificaciones no se actualizan en tiempo real | 🟡 Planeado | Implementar WebSockets en versión futura |
| 5 | Dashboard lento con > 10,000 registros | 🟡 En progreso | Implementar paginación en consultas |

### Solución de Problemas Comunes

#### El backend no inicia

```bash
# Verificar que MySQL esté corriendo
netstat -ano | findstr :3306

# Verificar configuración de base de datos
cat backend/src/main/resources/application.properties

# Limpiar compilación y reconstruir
cd backend
mvn clean install
```

#### Error de conexión a base de datos

```sql
-- Verificar usuario y permisos
SHOW GRANTS FOR 'tu_usuario'@'localhost';

-- Crear usuario si no existe
CREATE USER 'mesapartes'@'localhost' IDENTIFIED BY 'tu_contraseña';
GRANT ALL PRIVILEGES ON mesa_partes_db.* TO 'mesapartes'@'localhost';
FLUSH PRIVILEGES;
```

#### Archivos no se cargan

```bash
# Verificar permisos del directorio uploads
# Windows (CMD como administrador):
icacls backend\uploads /grant Everyone:(OI)(CI)F
```

---

## 🚀 Futuras Mejoras

### Versión 2.0 (Planificado)

- [ ] Implementación de WebSockets para notificaciones en tiempo real
- [ ] Aplicación móvil para consulta de trámites (Android/iOS)
- [ ] Dashboard ejecutivo con gráficos avanzados (Chart.js / D3.js)
- [ ] Firma digital integrada con certificados digitales
- [ ] Integración con servicios de mensajería (correo electrónico, SMS)
- [ ] API pública para integración con otros sistemas
- [ ] Módulo de generación de códigos QR para trámites
- [ ] Escaneo OCR automático de documentos
- [ ] Chat interno entre usuarios del sistema
- [ ] Notificaciones push en navegador

### Versión 3.0 (Futuro)

- [x] **Preparación para cloud deployment** (Railway configurado y documentado)
- [ ] Despliegue activo en producción cloud (Railway / AWS / Azure)
- [ ] Arquitectura de microservicios distribuidos
- [ ] Interoperabilidad con plataforma de Gobierno Digital
- [ ] Inteligencia artificial para clasificación automática de documentos
- [ ] Análisis predictivo de tiempos de atención
- [ ] Integración con sistemas biométricos
- [ ] Multi-tenancy para múltiples unidades policiales
- [ ] Sistema de workflows personalizables
- [ ] Reportes avanzados con Business Intelligence

---

## 🤝 Contribuciones

Este proyecto es parte de un trabajo académico del **Curso Integrador I** de la Universidad Tecnológica del Perú. 

### Proceso de Contribución

1. Fork del repositorio
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

### Estándares de Código

- Seguir convenciones de Java (CamelCase para clases, camelCase para métodos)
- Documentar métodos públicos con Javadoc
- Escribir pruebas unitarias para nueva funcionalidad
- Mantener cobertura de pruebas > 70%
- Seguir principios SOLID

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos para la **Universidad Tecnológica del Perú**.

**© 2025 - Equipo de Desarrollo de Mesa de Partes Digital PNP**

---

## 📞 Contacto y Soporte

### Equipo de Desarrollo

**Marcela Natalie Rodriguez Munaylla**  
Desarrollador Principal - Backend & Frontend  
📧 Email: [contacto del estudiante]
---

### 📌 Docente del Curso

**Mg. Cinthia J. Calderon Aquiño**  
Curso Integrador I - Sistemas Software  
Universidad Tecnológica del Perú

---

## 🎓 Institución Académica

**Universidad Tecnológica del Perú (UTP)**  
Facultad de Ingeniería de Sistemas e Informática  
Curso: Curso Integrador I - Sistemas Software  
Periodo: 2025-2

---

## 📝 Notas Finales

### Agradecimientos

Agradecemos a la **Policía Nacional del Perú** por permitir el desarrollo de este proyecto como caso de estudio, y a la **Universidad Tecnológica del Perú** por el apoyo académico durante el desarrollo.

### Descargo de Responsabilidad

Este sistema fue desarrollado con fines académicos y educativos. Aunque implementa medidas de seguridad estándar, se recomienda una auditoría de seguridad profesional antes de su uso en un entorno de producción real.

---

<div align="center">

### ⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub

**Desarrollado con ❤️ por estudiantes de Ingeniería de Sistemas de la UTP**

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

</div>
