CHANGELOG minimizado el 12 de noviembre de 2025.

Motivo: las entradas relevantes fueron consolidadas en `README.md` y `AVANCE_PROYECTO.md`. Este archivo se mantiene vacío para evitar duplicación.

Recuperar versión completa: revisar historial de Git si se requiere.# Changelog - Sistema Mesa de Partes Digital PNP

## [2.0.0] - Octubre 2025

### Añadido

#### Sistema de Notificaciones Toast
- Implementación completa de notificaciones animadas con CSS3
- 5 tipos de notificaciones: Success, Error, Warning, Info, Loading
- Animaciones con cubic-bezier easing para efectos suaves
- Barra de progreso animada
- Auto-dismiss configurable
- Reemplazo automático de alert() nativo
- Responsive design para móviles
- Archivos: `toast.css` (268 líneas), `toast.js` (115 líneas)

#### Página "Mis Documentos"
- Nueva vista dedicada para trabajadores
- Filtrado automático de documentos asignados al usuario
- Modal para actualizar estados con validaciones
- Filtros por estado, área y tipo de documento
- Descarga de archivos adjuntos
- Botones de acción con verificación de permisos
- Archivo: `documentos.html`, `documentos.js`

#### Workflow de Estados
- Sistema completo de 6 estados:
  1. Asignado (Estado inicial)
  2. Recibido (Trabajador confirmó recepción)
  3. En_Proceso (Documento en proceso)
  4. Observado (Con observaciones)
  5. Finalizado (Completo con informe)
  6. Salida (Salió del sistema)
- Badges visuales con emojis para cada estado
- Validaciones de transición de estados

#### Base de Datos
- Script SQL completo actualizado: `mesa_partes_db_completa_actualizada.sql`
- ENUM de estados actualizado en tabla documentos
- 10 documentos de ejemplo con estados variados
- 34 Departamentos PNP completos
- 7 usuarios precargados (contraseña: 123456)

### Modificado

#### Estados de Documentos
- ENUM anterior: Registrado, En Proceso, Observado, Finalizado, Salida
- ENUM nuevo: Asignado, Recibido, En_Proceso, Observado, Finalizado, Salida
- Columna estado con nuevo default: 'Asignado'
- Archivos modificados:
  - `EstadoDocumento.java` - ENUM actualizado
  - `Documento.java` - Columna con nuevo ENUM
  - `DocumentoController.java` - Manejo de nuevos estados
  - `documentos.js` - Badges actualizados

#### UI/UX
- Selects personalizados con arrows con gradiente PNP
- File inputs modernos con botón estilizado
- Modales con header en gradiente verde PNP
- Animaciones de entrada/salida en modales
- Hover effects con transiciones suaves
- Focus states con borders animados

#### Integración de Notificaciones
- `login.html` - Incluye sistema toast
- `registro-usuario.html` - Incluye sistema toast
- `documentos.html` - Incluye sistema toast
- `registro.html` - Incluye sistema toast

### Corregido

#### Error 500 en Dashboard de Trabajadores
- **Problema**: Serialización circular de Jackson con relaciones bidireccionales
- **Solución**: Conversión de entidades a Map<String, Object> en controller
- **Archivo**: `DocumentoController.java`

#### Botón "Actualizar" no funcional
- **Problema**: Funciones JavaScript no en scope global
- **Solución**: Uso de `window.functionName` para acceso global
- **Archivo**: `documentos.js`

#### Estado "En Proceso" no aceptado
- **Problema**: ENUM con guión bajo (En_Proceso) vs espacio en frontend
- **Solución**: Normalización en frontend y backend
- **Archivos**: `documentos.js`, `DocumentoController.java`

#### Error de compilación en registro de documentos
- **Problema**: Referencia a `EstadoDocumento.Registrado` (eliminado)
- **Solución**: Cambio a `EstadoDocumento.Asignado`
- **Archivo**: `DocumentoController.java` línea 56

### Documentación

#### Archivos nuevos
- `NOTIFICACIONES_TOAST.md` - Guía completa del sistema de notificaciones
- `CHANGELOG.md` - Este archivo
- README.md actualizado con todas las novedades

#### Secciones actualizadas en README
- Características principales
- Estructura de archivos
- Estado del proyecto (55% → 68%)
- Novedades v2.0
- Changelog completo

---

## [1.0.0] - Septiembre 2025

### Añadido
- Arquitectura base Spring Boot 3.5.6 con Java 21
- Sistema de autenticación JWT
- CRUD de usuarios con BCrypt
- Registro de documentos con códigos secuenciales
- Sistema de áreas dual (DEPARTAMENTO_PNP / AREA_TRABAJO)
- Bitácora de documentos
- Dashboard con métricas básicas
- Base de datos MySQL con 10 tablas
- Frontend con HTML5, CSS3 y JavaScript vanilla

### Características Iniciales
- 4 roles: Administrador, Mesa de Partes, Trabajador, Jefatura
- 34 Departamentos PNP precargados
- 5 Áreas de trabajo precargadas
- 7 usuarios de prueba
- 10 tipos de documento
- Carga de archivos PDF (máx. 10MB)
- Control de acceso basado en roles (RBAC)

---

## Formato
El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Categorías
- **Añadido** para nuevas características
- **Modificado** para cambios en funcionalidad existente
- **Deprecado** para características que serán eliminadas
- **Eliminado** para características eliminadas
- **Corregido** para corrección de bugs
- **Seguridad** para vulnerabilidades corregidas
