# 🚀 Despliegue en Railway

Esta carpeta contiene los archivos necesarios para el despliegue del sistema en **Railway**, una plataforma de hosting en la nube.

## 📁 Contenido

| Archivo | Descripción |
|---------|-------------|
| `railway.json` | Configuración de servicios Railway (backend, base de datos) |
| `preparar-railway.bat` | Script de preparación y validación pre-despliegue |
| `verificar-railway.bat` | Script de verificación post-despliegue (health checks) |
| `railwayignore` | Archivos/carpetas excluidos del despliegue |

## 🔧 Uso

### 1. Preparación
```cmd
preparar-railway.bat
```
Valida configuración, dependencias y archivos necesarios.

### 2. Despliegue
Instalar Railway CLI y ejecutar:
```cmd
railway login
railway init
railway up
```

### 3. Verificación
```cmd
verificar-railway.bat
```
Verifica estado del servicio, base de datos y endpoints.

## 📚 Documentación Completa

Para instrucciones detalladas, consultar: `GUIA_DEPLOY_RAILWAY.md` (raíz del proyecto)

## ⚠️ Notas Importantes

- Este deployment está **preparado pero no activado** (proyecto académico)
- Requiere cuenta Railway y configuración de variables de entorno
- Perfil de configuración: `application-railway.properties`
