ESTRUCTURA minimizada el 12 de noviembre de 2025.

Motivo: el detalle de la estructura del frontend está documentado en `frontend/README.md` y en `README.md` raíz; este archivo queda como nota breve.

Si necesitas la versión completa anterior, recupérala desde el historial de Git.# 📁 Estructura del Frontend - Mesa de Partes PNP

## Organización de Archivos

Esta es la estructura organizada y categorizada del frontend del proyecto:

```
frontend/
├── pages/                          # Páginas HTML categorizadas
│   ├── auth/                       # 🔐 Autenticación
│   │   ├── login.html             # Página de inicio de sesión
│   │   └── registro.html           # Registro de documentos (usuario interno)
│   │
│   ├── admin/                      # 👤 Administración
│   │   ├── gestion-usuarios.html   # Gestión de usuarios del sistema
│   │   └── bitacora.html           # Historial de acciones (logs)
│   │
│   ├── documents/                  # 📄 Gestión de Documentos
│   │   ├── documentos.html         # Lista y búsqueda de documentos
│   │   ├── registro-usuario.html   # Registro público de documentos
│   │   └── salida-documento.html   # Registro de salida de documentos
│   │
│   └── common/                     # 🏠 Páginas Comunes
│       ├── dashboard.html          # Panel principal
│       ├── index.html              # Página de entrada/loading
│       └── sidebar.html            # Componente de menú lateral
│
├── assets/
│   ├── js/
│   │   ├── core/                   # ⚙️ Funcionalidades Base
│   │   │   ├── config.js           # Configuración global (API_URL, etc.)
│   │   │   ├── auth.js             # Autenticación y manejo de sesiones
│   │   │   └── permissions.js      # Control de permisos por rol
│   │   │
│   │   ├── components/             # 🧩 Componentes UI Reutilizables
│   │   │   ├── sidebar.js          # Lógica del menú lateral
│   │   │   └── toast.js            # Notificaciones emergentes
│   │   │
│   │   ├── pages/                  # 📄 Scripts Específicos por Página
│   │   │   ├── auth/
│   │   │   │   ├── login.js        # Lógica de login
│   │   │   │   └── registro.js     # Lógica de registro (si existe)
│   │   │   │
│   │   │   ├── admin/
│   │   │   │   ├── gestion-usuarios.js
│   │   │   │   └── bitacora.js
│   │   │   │
│   │   │   ├── documents/
│   │   │   │   ├── documentos.js
│   │   │   │   ├── registrar-interno.js
│   │   │   │   ├── salida-documento.js
│   │   │   │   ├── derivaciones.js
│   │   │   │   └── trazabilidad.js
│   │   │   │
│   │   │   └── dashboard.js        # Lógica del panel principal
│   │   │
│   │   └── modules/                # 📦 Módulos Funcionales
│   │       ├── reportes.js         # Generación de reportes
│   │       ├── reportes-global.js  # Reportes a nivel sistema
│   │       └── notificaciones.js   # Sistema de notificaciones
│   │
│   └── css/
│       ├── core/                   # 🎨 Estilos Base
│       │   ├── style.css           # Estilos globales y variables CSS
│       │   └── toast.css           # Estilos de notificaciones
│       │
│       ├── components/             # 🧩 Estilos de Componentes
│       │   └── sidebar.css         # Estilos del menú lateral
│       │
│       ├── pages/                  # 📄 Estilos por Página
│       │   ├── auth/
│       │   │   ├── login.css
│       │   │   └── registro.css
│       │   │
│       │   ├── admin/
│       │   │   ├── gestion-usuarios.css
│       │   │   └── bitacora.css
│       │   │
│       │   ├── documents/
│       │   │   └── salida-documento.css
│       │   │
│       │   └── dashboard.css
│       │
│       └── features/               # ✨ Funcionalidades Específicas
│           └── nuevas-funcionalidades.css
```

## 📌 Convenciones de Rutas

### Para archivos en `pages/auth/` (2 niveles arriba de frontend):
```html
<!-- CSS -->
<link rel="stylesheet" href="../../assets/css/core/style.css">
<link rel="stylesheet" href="../../assets/css/pages/auth/login.css">

<!-- JavaScript -->
<script src="../../assets/js/core/config.js"></script>
<script src="../../assets/js/pages/auth/login.js"></script>
```

### Para archivos en `pages/admin/` o `pages/documents/` o `pages/common/`:
```html
<!-- CSS -->
<link rel="stylesheet" href="../../assets/css/core/style.css">
<link rel="stylesheet" href="../../assets/css/components/sidebar.css">

<!-- JavaScript -->
<script src="../../assets/js/core/auth.js"></script>
<script src="../../assets/js/components/sidebar.js"></script>
```

## 🎯 Beneficios de esta Estructura

### ✅ **Separación Clara de Responsabilidades**
- **core/**: Funcionalidades fundamentales del sistema
- **components/**: Elementos reutilizables
- **pages/**: Lógica específica de cada página
- **modules/**: Funcionalidades independientes

### ✅ **Escalabilidad**
- Fácil agregar nuevas páginas en su categoría correspondiente
- Módulos independientes pueden crecer sin afectar otros

### ✅ **Mantenibilidad**
- Archivos relacionados están juntos
- Fácil localizar y modificar código específico
- Reduce duplicación de código

### ✅ **Trabajo en Equipo**
- Diferentes desarrolladores pueden trabajar en diferentes módulos
- Menor probabilidad de conflictos en Git
- Estructura clara y auto-documentada

## 🔧 Orden de Carga Recomendado

Para páginas con sidebar y funcionalidad completa:

```html
<!-- 1. Estilos Core -->
<link rel="stylesheet" href="../../assets/css/core/style.css">
<link rel="stylesheet" href="../../assets/css/core/toast.css">

<!-- 2. Componentes -->
<link rel="stylesheet" href="../../assets/css/components/sidebar.css">

<!-- 3. Estilos de Página -->
<link rel="stylesheet" href="../../assets/css/pages/[categoria]/[pagina].css">

<!-- 4. JavaScript Core (orden importante) -->
<script src="../../assets/js/core/config.js"></script>
<script src="../../assets/js/core/permissions.js"></script>
<script src="../../assets/js/core/auth.js"></script>

<!-- 5. Componentes JavaScript -->
<script src="../../assets/js/components/toast.js"></script>
<script src="../../assets/js/components/sidebar.js"></script>

<!-- 6. JavaScript de Página -->
<script src="../../assets/js/pages/[categoria]/[archivo].js"></script>
```

## 📝 Notas Importantes

1. **Rutas Relativas**: Todas las rutas usan `../../` para subir dos niveles desde `pages/[categoria]/`
2. **Core Primero**: Los archivos core (config, auth, permissions) deben cargarse antes que otros
3. **No Duplicar**: Evitar duplicar archivos entre categorías
4. **Nomenclatura**: Usar kebab-case para nombres de archivos (ej: `gestion-usuarios.js`)

## 🚀 Próximos Pasos

- [ ] Considerar minificación de archivos JS/CSS para producción
- [ ] Implementar lazy loading para módulos grandes
- [ ] Agregar webpack o vite para bundling (opcional)
- [ ] Documentar APIs de cada módulo
