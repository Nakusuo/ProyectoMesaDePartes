# 📁 Estructura del Proyecto Mesa de Partes PNP

## 🏗️ Organización de Carpetas

```
ProyectoMesaDePartes/
├── backend/                    # 🔧 Backend Spring Boot (Puerto 8080)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Código fuente Java
│   │   │   └── resources/     # Configuraciones y recursos
│   │   └── test/              # Pruebas unitarias
│   ├── uploads/               # Archivos subidos (documentos y cargos)
│   ├── pom.xml               # Dependencias Maven
│   ├── mvnw.cmd              # Maven Wrapper para Windows
│   └── start-app.bat         # Script de inicio del backend
│
├── frontend/                  # 🎨 Frontend (HTML, CSS, JS)
│   ├── assets/
│   │   ├── css/              # Estilos CSS
│   │   └── js/               # JavaScript
│   ├── pages/                # Páginas HTML
│   │   ├── auth/            # Login y registro
│   │   ├── admin/           # Administración
│   │   ├── documents/       # Gestión de documentos
│   │   └── common/          # Páginas comunes
│   └── test-*.html          # Páginas de prueba
│
├── ProyectoMesaDePartes/     # 📦 Carpeta adicional del proyecto
│
├── scripts/                   # 📜 Scripts de utilidad
│   ├── backup_windows.bat    # Backup en Windows
│   ├── restaurar_backup_windows.bat
│   └── README_BACKUPS.md
│
├── SQL/                      # 💾 Scripts de base de datos
│   └── mesa_partes_db_completa_actualizada.sql
│
├── iniciar-backend.bat       # 🚀 Script para iniciar el backend desde raíz
└── README.md                 # Documentación principal
```

## 🚀 Cómo Iniciar el Sistema

### Opción 1: Desde la raíz del proyecto
```cmd
iniciar-backend.bat
```

### Opción 2: Desde la carpeta backend
```cmd
cd backend
start-app.bat
```

### Frontend
Abrir `frontend/pages/auth/login.html` con Live Server o servidor web.

## 🔧 Configuración del Backend

El archivo `backend/src/main/resources/application.properties` está configurado para:
- Base de datos MySQL en `localhost:3306/mesa_partes_db`
- Backend en puerto `8080`
- Frontend servido desde ruta relativa `../frontend/`
- CORS habilitado para `localhost:5500` y `localhost:3000`

## 📝 Notas Importantes

1. ✅ La carpeta `backend` está ahora en la raíz del proyecto
2. ✅ Las rutas del `application.properties` se actualizaron a `../frontend/`
3. ✅ Los estilos de la página de salida de documentos se mejoraron
4. ✅ El sistema está listo para funcionar con la nueva estructura

## 🎨 Mejoras en Salida de Documentos

Se aplicaron las siguientes mejoras visuales:

### Info Cards
- Gradientes suaves y modernos
- Sombras más pronunciadas con efecto hover
- Bordes izquierdos más gruesos
- Tipografía mejorada

### Input de Archivos
- Diseño más atractivo con gradientes
- Botones con efectos hover 3D
- Animaciones suaves
- Estados de éxito/error más visibles

### Tabla de Salidas
- Sombras y bordes redondeados
- Efecto hover con transformación
- Badges con gradientes y sombras
- Botones de acción más modernos
- Responsive mejorado

## 🛠️ Tecnologías

- **Backend**: Spring Boot 3.x + MySQL
- **Frontend**: HTML5 + CSS3 + JavaScript (Vanilla)
- **Seguridad**: JWT + Spring Security
- **Build**: Maven

---
**Actualizado**: 17 de noviembre de 2025
