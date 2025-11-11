# 🧪 Guía de Pruebas - Frontend Reorganizado

## ✅ Checklist de Verificación

### 1. Iniciar el Servidor Backend

```cmd
cd c:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\ProyectoMesaDePartes\backend
mvn spring-boot:run
```

Esperar a que aparezca:
```
Started MesadepartesApplication in X.XXX seconds
Tomcat started on port 8080
```

---

### 2. Probar Páginas de Autenticación

#### 📝 Login
- **URL:** http://localhost:8080/pages/auth/login.html
- **Verificar:**
  - ✅ Página carga correctamente
  - ✅ CSS aplicado (fondo verde PNP, logo)
  - ✅ Sin errores 404 en consola del navegador (F12)
  - ✅ Botón de login funciona

#### 📝 Registro de Documentos (Usuario Interno)
- **URL:** http://localhost:8080/pages/auth/registro.html
- **Verificar:**
  - ✅ Sidebar carga correctamente
  - ✅ Formulario de registro visible
  - ✅ CSS aplicado
  - ✅ Sin errores en consola

---

### 3. Probar Páginas de Administración

#### 👤 Gestión de Usuarios
- **URL:** http://localhost:8080/pages/admin/gestion-usuarios.html
- **Verificar:**
  - ✅ Sidebar carga
  - ✅ Tabla de usuarios visible
  - ✅ Botones de acción funcionan
  - ✅ CSS de gestion-usuarios aplicado

#### 📊 Bitácora
- **URL:** http://localhost:8080/pages/admin/bitacora.html
- **Verificar:**
  - ✅ Sidebar carga
  - ✅ Tabla de logs visible
  - ✅ Filtros funcionan
  - ✅ CSS de bitacora aplicado

---

### 4. Probar Páginas de Documentos

#### 📄 Lista de Documentos
- **URL:** http://localhost:8080/pages/documents/documentos.html
- **Verificar:**
  - ✅ Sidebar carga
  - ✅ Tabla de documentos visible
  - ✅ Búsqueda funciona
  - ✅ Dashboard CSS aplicado

#### 📝 Registro de Usuario (Público)
- **URL:** http://localhost:8080/pages/documents/registro-usuario.html
- **Verificar:**
  - ✅ Formulario público visible
  - ✅ Sin sidebar (página pública)
  - ✅ Login CSS aplicado

#### 📤 Salida de Documentos
- **URL:** http://localhost:8080/pages/documents/salida-documento.html
- **Verificar:**
  - ✅ Sidebar carga
  - ✅ Formulario de salida visible
  - ✅ Toast notifications funcionan
  - ✅ CSS específico aplicado

---

### 5. Probar Páginas Comunes

#### 🏠 Dashboard
- **URL:** http://localhost:8080/pages/common/dashboard.html
- **Verificar:**
  - ✅ Sidebar carga
  - ✅ Tarjetas de estadísticas visibles
  - ✅ Gráficos se renderizan
  - ✅ Dashboard CSS aplicado

#### 🔄 Index (Página de Entrada)
- **URL:** http://localhost:8080/pages/common/index.html
- **Verificar:**
  - ✅ Pantalla de loading
  - ✅ Redirección automática funciona

---

### 6. Verificar Navegación del Sidebar

**Desde cualquier página con sidebar, hacer clic en:**

1. ✅ **Dashboard** → Debe ir a `../common/dashboard.html`
2. ✅ **Registrar Documento** → Debe ir a `../auth/registro.html`
3. ✅ **Usuarios** → Debe ir a `../admin/gestion-usuarios.html`
4. ✅ **Bitácora** → Debe ir a `../admin/bitacora.html`
5. ✅ **Salida de Documentos** → Debe ir a `../documents/salida-documento.html`
6. ✅ **Mis Documentos** → Debe ir a `../documents/documentos.html`

---

### 7. Verificar Consola del Navegador (F12)

**En TODAS las páginas, verificar que NO aparezcan:**
- ❌ `404 Not Found` para archivos CSS
- ❌ `404 Not Found` para archivos JS
- ❌ `ReferenceError: API_URL is not defined`
- ❌ `ReferenceError: verificarAutenticacion is not defined`
- ❌ Errores de CORS

**Debe aparecer:**
- ✅ Mensajes de carga exitosa
- ✅ Logs de autenticación (si aplica)
- ✅ Respuestas correctas del API

---

### 8. Verificar Funcionalidades por Rol

#### 👤 Como ADMIN:
- ✅ Ver todas las opciones del menú
- ✅ Acceder a gestión de usuarios
- ✅ Acceder a bitácora
- ✅ Ver todos los documentos

#### 📝 Como USUARIO:
- ✅ Ver dashboard
- ✅ Registrar documentos
- ✅ Ver solo documentos asignados
- ❌ NO ver gestión de usuarios

#### 👁️ Como CONSULTA:
- ✅ Ver dashboard (solo lectura)
- ❌ NO registrar documentos
- ❌ NO modificar nada

---

## 🐛 Solución de Problemas Comunes

### ❌ Error: "404 Not Found" para archivos CSS/JS

**Causa:** Ruta incorrecta en el HTML
**Solución:**
1. Abrir el archivo HTML problemático
2. Verificar que las rutas usen `../../assets/`
3. Verificar que la categoría sea correcta (core, components, pages)

### ❌ Sidebar no carga

**Causa:** Ruta incorrecta en sidebar.js o HTML
**Solución:**
1. Verificar que `sidebar.js` esté en `../../assets/js/components/`
2. Verificar que se cargue DESPUÉS de `auth.js` y `permissions.js`

### ❌ API_URL undefined

**Causa:** `config.js` no se carga primero
**Solución:**
1. Verificar que `config.js` sea el PRIMER script cargado
2. Ruta debe ser `../../assets/js/core/config.js`

### ❌ Estilos no se aplican correctamente

**Causa:** Orden de carga de CSS incorrecto
**Solución:**
1. Cargar en orden: core → components → pages
2. Ejemplo:
   ```html
   <link rel="stylesheet" href="../../assets/css/core/style.css">
   <link rel="stylesheet" href="../../assets/css/components/sidebar.css">
   <link rel="stylesheet" href="../../assets/css/pages/admin/bitacora.css">
   ```

---

## 📊 Checklist Rápido de Archivos

### Archivos Core que DEBEN cargarse en TODAS las páginas:
- ✅ `core/style.css` - Estilos globales
- ✅ `core/config.js` - Configuración global

### Archivos que necesitan páginas CON sidebar:
- ✅ `components/sidebar.css`
- ✅ `core/permissions.js`
- ✅ `components/sidebar.js`
- ✅ `core/auth.js`

### Archivos opcionales según funcionalidad:
- ⚠️ `core/toast.css` + `components/toast.js` - Si usa notificaciones
- ⚠️ `modules/reportes.js` - Si genera reportes
- ⚠️ `modules/notificaciones.js` - Si usa notificaciones del sistema

---

## ✅ Resultado Esperado

Después de completar todas las pruebas:
- ✅ TODAS las páginas cargan sin errores
- ✅ CSS se aplica correctamente en todas las páginas
- ✅ JavaScript funciona sin errores en consola
- ✅ Navegación entre páginas funciona
- ✅ Sidebar se muestra correctamente
- ✅ Permisos por rol funcionan
- ✅ Sistema completamente operativo

---

## 📝 Reporte de Pruebas

```
Fecha: __________
Probado por: __________

[ ] 1. Login funciona
[ ] 2. Registro funciona
[ ] 3. Dashboard carga correctamente
[ ] 4. Gestión de usuarios accesible
[ ] 5. Bitácora accesible
[ ] 6. Documentos se listan correctamente
[ ] 7. Salida de documentos funciona
[ ] 8. Sidebar navega correctamente
[ ] 9. Sin errores en consola
[ ] 10. Permisos por rol funcionan

Errores encontrados:
_________________________________
_________________________________
_________________________________
```

---

**Última actualización:** 11 de noviembre de 2025
**Estado de reorganización:** ✅ Completo
