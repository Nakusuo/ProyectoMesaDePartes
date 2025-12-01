# Directorio de Backups

Este directorio almacena los backups automáticos de la base de datos del sistema.

## 📁 Estructura

Los archivos de backup siguen el formato:
```
backup_[nombre_db]_[YYYYMMDD]_[HHmmss].sql
```

**Ejemplo:**
```
backup_mesa_partes_db_20251201_020015.sql
```

## ⚙️ Configuración

- **Frecuencia:** Diaria (2:00 AM por defecto)
- **Retención:** 30 días (configurable)
- **Limpieza:** Automática (elimina backups antiguos)

## 🔒 Seguridad

**IMPORTANTE:** Este directorio contiene información sensible de la base de datos.

Recomendaciones:
- Mantener permisos restrictivos
- No subir a repositorios públicos
- Realizar copias en ubicación externa

---

**Sistema de Backup Automático:**
- Frecuencia: Diaria (2:00 AM por defecto)
- Retención: 30 días (configurable)
- Limpieza: Automática

Ver configuración en: `backend/src/main/resources/application.properties`
