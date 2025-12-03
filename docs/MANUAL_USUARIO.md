# Manual de Usuario
## Sistema de Mesa de Partes Digital - Policía Nacional del Perú

**Versión:** 1.0  
**Fecha:** Diciembre 2025  
**Desarrollado por:** Equipo de Desarrollo - UTP

---

## Contenido

1. [Introducción](#introducción)
2. [Requisitos del Sistema](#requisitos-del-sistema)
3. [Acceso al Sistema](#acceso-al-sistema)
4. [Módulos del Sistema](#módulos-del-sistema)
5. [Guía de Uso por Rol](#guía-de-uso-por-rol)
6. [Preguntas Frecuentes](#preguntas-frecuentes)
7. [Soporte Técnico](#soporte-técnico)

---

## 1. Introducción

### 1.1 Propósito del Manual

Este manual tiene como objetivo guiar a los usuarios finales del Sistema de Mesa de Partes Digital de la Policía Nacional del Perú en el uso correcto y eficiente de la plataforma. El documento está dirigido a:

- **Administradores de Mesa de Partes:** Personal encargado del registro, derivación y control de documentos.
- **Personal Operativo:** Trabajadores de área que reciben, procesan y actualizan el estado de los documentos asignados.
- **Ciudadanos/Usuarios Externos:** Personas que consultan el estado de sus trámites.

### 1.2 Alcance del Sistema

El Sistema de Mesa de Partes Digital permite:

- Registrar documentos de entrada y salida de forma digital
- Derivar documentos a diferentes áreas de la institución
- Realizar seguimiento en tiempo real del estado de los trámites
- Generar reportes estadísticos y de gestión
- Mantener un registro de auditoría de todas las acciones realizadas
- Gestionar usuarios y sus respectivos roles y permisos

### 1.3 Beneficios del Sistema

- ✅ **Trazabilidad completa:** Seguimiento detallado de cada documento desde su ingreso hasta su cierre
- ✅ **Reducción de tiempos:** Eliminación de procesos manuales y físicos
- ✅ **Transparencia:** Acceso a información actualizada en tiempo real
- ✅ **Seguridad:** Cifrado de datos y control de accesos por rol
- ✅ **Eficiencia:** Automatización de notificaciones y reportes

---

## 2. Requisitos del Sistema

### 2.1 Requisitos de Hardware

**Para usuarios finales:**
- Computadora con procesador Intel Core i3 o superior
- Mínimo 4 GB de RAM
- Conexión a Internet estable (mínimo 2 Mbps)
- Resolución de pantalla mínima: 1366x768 píxeles

**Periféricos recomendados:**
- Escáner para digitalización de documentos físicos
- Impresora para constancias y reportes

### 2.2 Requisitos de Software

**Navegadores web compatibles:**
- Google Chrome (versión 90 o superior) ✅ Recomendado
- Mozilla Firefox (versión 88 o superior)
- Microsoft Edge (versión 90 o superior)
- Safari (versión 14 o superior)

**Otros requisitos:**
- Adobe Acrobat Reader o visor de PDF
- Microsoft Excel o LibreOffice Calc (para reportes)
- Conexión a Internet activa

### 2.3 Requisitos de Seguridad

- Credenciales de acceso (usuario y contraseña) proporcionadas por el Administrador
- Cambio de contraseña al primer inicio de sesión
- Contraseña con mínimo 8 caracteres (letras, números y símbolos)

---

## 3. Acceso al Sistema

### 3.1 Iniciar Sesión

**Paso 1:** Abrir el navegador web e ingresar a la URL del sistema:
```
http://localhost:8080/login
o
https://mesadepartes-pnp.railway.app/login
```

**Paso 2:** Se mostrará la pantalla de inicio de sesión con los siguientes campos:

![Pantalla de Login](../imagenes/login-screenshot.png)

- **Usuario:** Ingrese su nombre de usuario asignado
- **Contraseña:** Ingrese su contraseña personal

**Paso 3:** Hacer clic en el botón **"Iniciar Sesión"**

**Importante:**
- ⚠️ Las credenciales son personales e intransferibles
- ⚠️ No comparta su contraseña con terceros
- ⚠️ El sistema bloqueará su cuenta después de 3 intentos fallidos

### 3.2 Recuperación de Contraseña

Si olvidó su contraseña:

1. En la pantalla de login, hacer clic en **"¿Olvidó su contraseña?"**
2. Ingresar su correo electrónico institucional
3. Recibirá un enlace de recuperación en su correo
4. Hacer clic en el enlace y establecer una nueva contraseña
5. Iniciar sesión con la nueva contraseña

### 3.3 Primer Inicio de Sesión

Al iniciar sesión por primera vez:

1. El sistema solicitará cambiar la contraseña temporal
2. Ingrese la contraseña temporal proporcionada
3. Ingrese su nueva contraseña
4. Confirme la nueva contraseña
5. Acepte los términos y condiciones de uso
6. Haga clic en **"Actualizar Contraseña"**

### 3.4 Cerrar Sesión

Para cerrar sesión de forma segura:

1. Hacer clic en el icono de usuario en la esquina superior derecha
2. Seleccionar la opción **"Cerrar Sesión"**
3. El sistema cerrará la sesión y redirigirá a la pantalla de login

**Recomendación:** Siempre cierre sesión al finalizar su trabajo, especialmente en computadoras compartidas.

---

## 4. Módulos del Sistema

### 4.1 Dashboard (Panel Principal)

El Dashboard es la pantalla principal que se muestra después de iniciar sesión. Presenta información resumida y relevante según el rol del usuario.

**Elementos del Dashboard:**

#### 4.1.1 Para Administradores
- **Estadísticas generales:**
  - Total de documentos registrados
  - Documentos pendientes de derivación
  - Documentos en proceso
  - Documentos finalizados
  - Documentos observados

- **Gráficos estadísticos:**
  - Documentos por área (gráfico de barras)
  - Estado de documentos (gráfico circular)
  - Tendencia de registro mensual (gráfico de líneas)

- **Últimos registros:**
  - Lista de los 10 últimos documentos ingresados
  - Estado actual y área asignada

- **Accesos rápidos:**
  - Registrar nuevo documento
  - Ver bitácora
  - Generar reporte
  - Gestionar usuarios

#### 4.1.2 Para Personal Operativo
- **Mis documentos asignados:**
  - Documentos pendientes de atención
  - Documentos en proceso
  - Documentos finalizados

- **Notificaciones:**
  - Nuevas asignaciones
  - Documentos próximos a vencer
  - Alertas importantes

- **Acciones rápidas:**
  - Actualizar estado de documento
  - Ver documentos asignados
  - Consultar historial

### 4.2 Módulo de Registro de Documentos

Este módulo permite ingresar nuevos documentos al sistema.

#### 4.2.1 Registrar Documento de Entrada

**Ruta:** Dashboard → Documentos → Registrar Entrada

**Campos del formulario:**

**Información del Documento:**
- **Tipo de documento:** Seleccionar de lista desplegable
  - Oficio
  - Oficio Múltiple
  - Memorándum
  - Carta
  - Comunicación Web
  - Comunicación Telefónica
  - Otros

- **Número de documento:** Número del documento original
- **Fecha de emisión:** Fecha en que fue emitido el documento
- **Número de Hoja de Trámite (HT):** Campo opcional

**Información del Remitente:**
- **Unidad/Entidad de procedencia:** Origen del documento
- **Asunto:** Descripción breve del contenido (máximo 500 caracteres)

**Asignación:**
- **Área destino:** Seleccionar el área a la que se derivará el documento
- **Usuario responsable:** Seleccionar el usuario que atenderá el documento
- **Prioridad:** Normal / Urgente / Muy Urgente

**Archivo adjunto:**
- Formato permitido: PDF, JPG, PNG
- Tamaño máximo: 10 MB
- Hacer clic en **"Examinar"** y seleccionar el archivo

**Observaciones:**
- Campo opcional para notas adicionales

**Paso a paso:**

1. Completar todos los campos obligatorios (marcados con *)
2. Adjuntar el archivo digitalizado del documento
3. Verificar que toda la información sea correcta
4. Hacer clic en **"Registrar Documento"**
5. El sistema generará automáticamente un **Código Único de Registro**
6. Se mostrará un mensaje de confirmación con el código
7. El documento aparecerá en la lista de documentos registrados

**Importante:**
- ⚠️ El código de registro es único e irrepetible
- ⚠️ Guarde el código para futuras consultas
- ⚠️ El archivo adjunto es obligatorio

#### 4.2.2 Registrar Documento de Salida

**Ruta:** Dashboard → Documentos → Registrar Salida

Este módulo registra los documentos emitidos por la institución hacia el exterior.

**Campos del formulario:**

- **Tipo de documento de salida:** Generalmente Oficio
- **Número de documento:** Número correlativo asignado
- **Unidad/Entidad de destino:** A quién se dirige el documento
- **Fecha de envío:** Fecha de emisión
- **Responsable de entrega:** Persona que recibe el documento
- **Documento de entrada relacionado:** Vincular con el trámite que origina la respuesta (opcional)
- **Observaciones:** Notas adicionales
- **Archivo adjunto:** PDF del documento de salida

**Procedimiento:**

1. Completar todos los campos del formulario
2. Si es una respuesta a un documento de entrada, vincular ambos
3. Adjuntar el PDF del documento de salida
4. Hacer clic en **"Registrar Salida"**
5. El sistema actualizará el estado del trámite relacionado

### 4.3 Módulo de Derivación

Este módulo permite asignar o redirigir documentos a diferentes áreas.

**Ruta:** Dashboard → Documentos → Derivar

**Funcionalidades:**

#### 4.3.1 Derivar Documento

1. Buscar el documento por código o número
2. Seleccionar el documento de la lista
3. Hacer clic en **"Derivar"**
4. Seleccionar el área destino
5. Seleccionar el usuario responsable
6. Establecer prioridad
7. Agregar observaciones (opcional)
8. Hacer clic en **"Confirmar Derivación"**

#### 4.3.2 Historial de Derivaciones

Para cada documento se puede visualizar:
- Fecha y hora de cada derivación
- Usuario que realizó la derivación
- Área origen y área destino
- Estado en cada etapa
- Observaciones registradas

### 4.4 Módulo de Consulta de Trámites

Permite buscar y visualizar el estado de cualquier documento.

**Ruta:** Dashboard → Consultas → Buscar Trámite

**Métodos de búsqueda:**

1. **Por código único:** Ingresar el código de registro
2. **Por número de documento:** Buscar por número original
3. **Por fecha:** Filtrar documentos por rango de fechas
4. **Por área:** Ver documentos de un área específica
5. **Por estado:** Filtrar por pendiente, en proceso, finalizado, etc.
6. **Por remitente:** Buscar documentos de una entidad específica

**Información mostrada:**

- Código de registro
- Número de documento
- Tipo de documento
- Fecha de ingreso
- Remitente
- Asunto
- Estado actual
- Área responsable
- Usuario asignado
- Tiempo transcurrido
- Historial completo de movimientos

**Acciones disponibles:**

- 👁️ Ver detalles completos
- 📄 Descargar constancia
- 📊 Ver trazabilidad
- 🖨️ Imprimir información

### 4.5 Módulo de Gestión de Usuarios (Solo Administrador)

**Ruta:** Dashboard → Administración → Usuarios

#### 4.5.1 Crear Nuevo Usuario

**Campos obligatorios:**
- **Nombre completo:** Nombres y apellidos
- **DNI:** Documento de identidad
- **Correo electrónico:** Email institucional
- **Teléfono:** Número de contacto
- **Usuario:** Nombre de usuario para login
- **Área:** Unidad a la que pertenece
- **Rol:** Administrador / Personal Operativo
- **Estado:** Activo / Inactivo

**Procedimiento:**
1. Hacer clic en **"+ Nuevo Usuario"**
2. Completar el formulario
3. El sistema generará una contraseña temporal
4. Hacer clic en **"Guardar Usuario"**
5. El nuevo usuario recibirá sus credenciales por correo

#### 4.5.2 Editar Usuario

1. Buscar el usuario en la lista
2. Hacer clic en el icono de edición (✏️)
3. Modificar los campos necesarios
4. Hacer clic en **"Actualizar Usuario"**

#### 4.5.3 Desactivar Usuario

1. Seleccionar el usuario
2. Hacer clic en el icono de desactivación (🚫)
3. Confirmar la acción
4. El usuario no podrá acceder al sistema

**Nota:** No se eliminan usuarios, solo se desactivan para mantener la integridad de la auditoría.

### 4.6 Módulo de Reportes

**Ruta:** Dashboard → Reportes

#### 4.6.1 Tipos de Reportes Disponibles

**Reporte de Documentos Registrados:**
- Filtros: Fecha inicio, fecha fin, área, tipo de documento
- Formato: PDF / Excel
- Contenido: Lista completa de documentos con sus datos

**Reporte de Tiempos de Atención:**
- Muestra el tiempo promedio de atención por área
- Identifica cuellos de botella
- Formato: PDF / Excel

**Reporte de Documentos por Estado:**
- Pendientes
- En proceso
- Finalizados
- Observados
- Formato: PDF / Excel con gráficos

**Reporte de Productividad por Usuario:**
- Documentos atendidos por usuario
- Tiempo promedio de respuesta
- Formato: PDF / Excel

**Reporte de Documentos por Área:**
- Total de documentos recibidos por área
- Estado actual de cada uno
- Formato: PDF / Excel

#### 4.6.2 Generar Reporte

**Paso a paso:**

1. Seleccionar el tipo de reporte deseado
2. Establecer los filtros:
   - Rango de fechas
   - Área (si aplica)
   - Estado (si aplica)
   - Usuario (si aplica)
3. Seleccionar formato de salida: PDF o Excel
4. Hacer clic en **"Generar Reporte"**
5. El sistema procesará la solicitud
6. Se mostrará una vista previa del reporte
7. Hacer clic en **"Descargar"** para guardar el archivo

**Recomendaciones:**
- Para reportes extensos, seleccione formato Excel
- Los reportes PDF incluyen gráficos estadísticos
- Puede programar reportes automáticos (contactar al administrador)

### 4.7 Módulo de Bitácora (Solo Administrador)

**Ruta:** Dashboard → Administración → Bitácora

La bitácora registra todas las acciones realizadas en el sistema para fines de auditoría.

**Información registrada:**
- Fecha y hora de la acción
- Usuario que realizó la acción
- Tipo de acción: Crear, Editar, Eliminar, Consultar, Derivar
- Módulo afectado: Documentos, Usuarios, Reportes
- Dirección IP del usuario
- Detalles específicos de la acción

**Funcionalidades de consulta:**
- Filtrar por fecha
- Filtrar por usuario
- Filtrar por tipo de acción
- Filtrar por módulo
- Búsqueda por palabra clave
- Exportar a Excel

**Uso recomendado:**
- Revisar acciones sospechosas
- Verificar quién realizó una acción específica
- Generar reportes de auditoría
- Seguimiento de cambios críticos

---

## 5. Guía de Uso por Rol

### 5.1 Administrador de Mesa de Partes

El Administrador es el responsable principal de la gestión documental y administración del sistema.

#### 5.1.1 Flujo de Trabajo Diario

**Inicio del día:**
1. Iniciar sesión en el sistema
2. Revisar el dashboard para ver estadísticas actualizadas
3. Verificar notificaciones y alertas
4. Revisar documentos pendientes de registro

**Recepción de documentos:**
1. Recibir documento físico o digital
2. Verificar que esté completo y legible
3. Escanear el documento si es físico
4. Ingresar al módulo **Registrar Entrada**
5. Completar todos los campos del formulario
6. Adjuntar archivo digitalizado
7. Asignar al área y usuario correspondiente
8. Guardar el registro
9. Entregar constancia al remitente (si aplica)

**Derivación de documentos:**
1. Verificar documentos ingresados
2. Derivar a las áreas correspondientes según el asunto
3. Establecer prioridad
4. Agregar observaciones si es necesario
5. Confirmar derivación
6. El sistema notificará automáticamente al usuario asignado

**Seguimiento y control:**
1. Monitorear el estado de los documentos
2. Verificar documentos próximos a vencer
3. Contactar a áreas con documentos retrasados
4. Actualizar información si es necesario

**Registro de salidas:**
1. Recibir documentos de respuesta de las áreas
2. Verificar correspondencia con documento de entrada
3. Registrar salida en el sistema
4. Vincular con el documento de entrada
5. Actualizar estado del trámite

**Gestión administrativa:**
1. Crear nuevos usuarios cuando sea necesario
2. Asignar roles y permisos
3. Desactivar usuarios que ya no laboran
4. Configurar áreas y tipos de documento

**Generación de reportes:**
1. Generar reportes semanales de gestión
2. Reportes mensuales para la dirección
3. Reportes de auditoría cuando se requiera

**Cierre del día:**
1. Verificar que todos los documentos recibidos estén registrados
2. Revisar documentos pendientes de derivación
3. Cerrar sesión de forma segura

#### 5.1.2 Casos Especiales

**Documento con información incompleta:**
1. Registrar el documento con la información disponible
2. Marcar como "Observado" en el estado
3. Agregar observación detallada
4. Contactar al remitente para completar información

**Documento urgente o muy urgente:**
1. Establecer prioridad correspondiente
2. Derivar inmediatamente
3. Notificar directamente al usuario responsable
4. Hacer seguimiento personalizado

**Reclamo por documento extraviado:**
1. Buscar en el sistema por todos los criterios posibles
2. Revisar bitácora de acciones
3. Verificar con el área asignada
4. Generar reporte de trazabilidad
5. Si no se encuentra, iniciar protocolo de documento extraviado

### 5.2 Personal Operativo

El Personal Operativo recibe, procesa y actualiza documentos asignados a su área.

#### 5.2.1 Flujo de Trabajo

**Inicio del día:**
1. Iniciar sesión
2. Revisar dashboard personal
3. Ver notificaciones de nuevas asignaciones
4. Revisar lista de documentos pendientes

**Recepción de asignación:**
1. Recibir notificación de nuevo documento asignado
2. Acceder al documento desde el dashboard
3. Descargar y revisar el archivo adjunto
4. Actualizar estado a "Recibido"
5. Agregar nota de recepción con fecha y hora

**Procesamiento de documento:**
1. Analizar el contenido del documento
2. Realizar las acciones correspondientes según el trámite
3. Actualizar el estado a "En Proceso"
4. Agregar notas de avance periódicas
5. Si requiere información adicional:
   - Agregar observación
   - Contactar al remitente o al administrador
   - Actualizar estado a "Observado"

**Elaboración de respuesta:**
1. Preparar la documentación de respuesta
2. Elaborar informe, dictamen o documento correspondiente
3. Guardar en formato PDF
4. Adjuntar al trámite en el sistema

**Finalización:**
1. Una vez completado el trámite, actualizar estado a "Atendido"
2. Adjuntar todos los documentos generados
3. Agregar resumen de la gestión realizada
4. Notificar al administrador
5. El administrador registrará la salida oficial

**Consultas:**
1. Revisar historial de documentos atendidos
2. Verificar tiempo de atención
3. Consultar trazabilidad de documentos específicos

#### 5.2.2 Buenas Prácticas

- ✅ Actualizar el estado del documento regularmente
- ✅ Agregar notas descriptivas de cada acción
- ✅ Adjuntar todos los archivos generados
- ✅ Atender los documentos urgentes prioritariamente
- ✅ Comunicar demoras o problemas al administrador
- ✅ Mantener organizada la documentación digital

---

## 6. Preguntas Frecuentes

### 6.1 Preguntas Generales

**P: ¿Qué hago si olvidé mi contraseña?**  
R: En la pantalla de login, haga clic en "¿Olvidó su contraseña?" e ingrese su correo electrónico. Recibirá un enlace para restablecerla.

**P: ¿Puedo acceder al sistema desde mi celular?**  
R: Sí, el sistema es responsive y puede acceder desde cualquier dispositivo con navegador web moderno, aunque se recomienda usar una computadora para mejor experiencia.

**P: ¿Cuánto tiempo se guardan los documentos en el sistema?**  
R: Los documentos se mantienen permanentemente en el sistema. Se realizan respaldos automáticos diarios.

**P: ¿Puedo modificar un documento ya registrado?**  
R: Solo el Administrador puede modificar información de documentos registrados. Contacte al administrador si necesita realizar cambios.

**P: ¿Cómo sé si tengo documentos pendientes?**  
R: En su dashboard personal verá un contador de documentos pendientes. También recibirá notificaciones dentro del sistema.

### 6.2 Preguntas Técnicas

**P: ¿Qué formatos de archivo puedo adjuntar?**  
R: Se aceptan archivos PDF, JPG y PNG. El tamaño máximo es de 10 MB por archivo.

**P: ¿Qué hago si el sistema está lento?**  
R: Verifique su conexión a internet. Si el problema persiste, contacte al soporte técnico.

**P: ¿Puedo descargar los documentos adjuntos?**  
R: Sí, puede descargar los documentos haciendo clic en el ícono de descarga junto al nombre del archivo.

**P: ¿El sistema funciona sin internet?**  
R: No, el sistema requiere conexión a internet para funcionar correctamente.

**P: ¿Cómo puedo imprimir un reporte?**  
R: Genere el reporte en formato PDF y use la función de impresión de su navegador (Ctrl+P).

### 6.3 Preguntas sobre Seguridad

**P: ¿Es seguro guardar documentos en el sistema?**  
R: Sí, el sistema utiliza cifrado SSL/TLS para la transmisión de datos y todas las contraseñas están encriptadas. Se realizan respaldos automáticos diarios.

**P: ¿Quién puede ver mis documentos?**  
R: Solo los usuarios con los permisos adecuados pueden ver los documentos según su rol y área asignada.

**P: ¿Se registran todas las acciones que realizo?**  
R: Sí, el sistema mantiene una bitácora completa de auditoría con todas las acciones realizadas.

**P: ¿Cada cuánto debo cambiar mi contraseña?**  
R: Se recomienda cambiar la contraseña cada 90 días. El sistema le notificará cuando sea necesario.

---

## 7. Soporte Técnico

### 7.1 Canales de Soporte

**Soporte Técnico - Sistema Mesa de Partes Digital**

📧 **Correo electrónico:** soporte.mdp@pnp.gob.pe  
📞 **Teléfono:** (01) XXX-XXXX anexo XXX  
🕐 **Horario de atención:** Lunes a Viernes de 8:00 AM a 5:00 PM  
📍 **Ubicación:** Oficina de Tecnologías de la Información - PNP

### 7.2 Tipos de Solicitudes

**Soporte técnico inmediato:**
- Problemas de acceso al sistema
- Errores al registrar documentos
- Problemas con archivos adjuntos
- Sistema lento o no responde

**Solicitudes administrativas:**
- Creación de nuevos usuarios
- Cambio de contraseña
- Cambio de área o rol
- Desactivación de usuarios

**Capacitación:**
- Solicitud de capacitación para nuevos usuarios
- Capacitación de actualización
- Manuales adicionales

### 7.3 Procedimiento de Reporte de Incidencias

Cuando reporte un problema, proporcione la siguiente información:

1. **Datos del usuario:**
   - Nombre completo
   - Usuario de sistema
   - Área

2. **Descripción del problema:**
   - ¿Qué estaba intentando hacer?
   - ¿Qué sucedió exactamente?
   - ¿Hay algún mensaje de error? (captura de pantalla si es posible)

3. **Contexto:**
   - Fecha y hora del incidente
   - Navegador utilizado
   - ¿El problema es recurrente o fue la primera vez?

4. **Urgencia:**
   - Baja: No impide el trabajo
   - Media: Dificulta el trabajo pero hay alternativas
   - Alta: Impide completamente el trabajo

### 7.4 Tiempo de Respuesta

- **Urgencia Alta:** Respuesta en 2 horas / Solución en 4 horas
- **Urgencia Media:** Respuesta en 4 horas / Solución en 8 horas
- **Urgencia Baja:** Respuesta en 8 horas / Solución en 24 horas

---

## 8. Glosario de Términos

- **Código único:** Identificador automático generado por el sistema para cada documento registrado
- **Dashboard:** Panel principal con información resumida y estadísticas
- **Derivación:** Acción de asignar un documento a un área o usuario específico
- **Hoja de Trámite (HT):** Documento físico que acompaña a algunos trámites oficiales
- **Trazabilidad:** Historial completo de movimientos y acciones sobre un documento
- **Bitácora:** Registro de auditoría de todas las acciones realizadas en el sistema
- **Rol:** Perfil de usuario que determina los permisos y accesos en el sistema
- **Backup:** Copia de seguridad automática de la base de datos
- **PDF:** Formato de documento portable (Portable Document Format)

---

## 9. Anexos

### Anexo A: Atajos de Teclado

- `Ctrl + F`: Búsqueda rápida en la página actual
- `Ctrl + P`: Imprimir página o reporte
- `Ctrl + S`: Guardar (en formularios)
- `Esc`: Cerrar modal o ventana emergente
- `Tab`: Navegar entre campos de formulario

### Anexo B: Códigos de Estado de Documentos

| Estado | Descripción | Color |
|--------|-------------|-------|
| Registrado | Documento ingresado al sistema | Azul |
| Derivado | Asignado a un área | Amarillo |
| Recibido | El usuario confirmó recepción | Naranja |
| En Proceso | El usuario está trabajando en el documento | Morado |
| Observado | Requiere información adicional | Gris |
| Atendido | Trámite completado, pendiente de salida | Verde claro |
| Finalizado | Documento de salida registrado | Verde oscuro |

### Anexo C: Lista de Verificación para Registro

✅ **Antes de registrar un documento, verifique:**

- [ ] El documento está completo y legible
- [ ] La digitalización es de buena calidad
- [ ] El archivo es menor a 10 MB
- [ ] El formato es PDF, JPG o PNG
- [ ] Toda la información requerida está disponible
- [ ] El área destino es la correcta
- [ ] El asunto está correctamente descrito

---

## Control de Versiones

| Versión | Fecha | Autor | Cambios |
|---------|-------|-------|---------|
| 1.0 | Diciembre 2025 | Equipo de Desarrollo UTP | Versión inicial del manual |

---

## Notas Finales

Este manual está sujeto a actualizaciones conforme se implementen nuevas funcionalidades en el sistema. Para sugerencias o comentarios sobre este manual, contacte al equipo de soporte técnico.

**Desarrollado por:**
- García Ortega Shayuri - Mantenimiento y Documentación
- López Díaz Maryafernanda - Testing
- Mantari Licapa Walter - Testing y Monitoreo
- Rodriguez Munaylla Marcela - Backend y Frontend

**Universidad Tecnológica del Perú**  
**Facultad de Ingeniería de Sistemas e Informática**  
**Curso Integrador I: Sistemas Software**  
**2025 - 2**

---

**© 2025 Policía Nacional del Perú - Todos los derechos reservados**
