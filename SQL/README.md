# 📦 Base de Datos Mesa de Partes PNP

## 🎯 Archivo Principal

**`mesa_partes_db_completa_con_bitacora_FINAL.sql`**

Este archivo contiene **TODA** la estructura completa de la base de datos incluyendo:

### ✅ Tablas Incluidas (12 tablas)
1. **areas** - Departamentos PNP y áreas de trabajo
2. **roles** - Roles del sistema (ADMIN, JEFE, USUARIO, etc.)
3. **usuarios** - Usuarios del sistema (CAS, LOCADOR, PNP)
4. **usuario_roles** - Relación usuarios-roles
5. **tipos_documento** - Tipos de documentos (Oficio, Memorándum, etc.)
6. **documentos** - Documentos de entrada
7. **hojas_tramite** - Hojas de trámite (HT)
8. **tramites** - Trámites asociados a documentos
9. **derivaciones** - Derivaciones entre áreas
10. **notificaciones** - Sistema de notificaciones
11. **salidas_documento** - Documentos de salida
12. **bitacora** - Auditoría de entradas y salidas

### 🔧 Funcionalidades Incluidas
- ✅ **Índices optimizados** para consultas rápidas
- ✅ **Claves foráneas** para integridad referencial
- ✅ **Triggers automáticos** para bitácora
- ✅ **Datos de prueba** (áreas, roles, usuarios, documentos)
- ✅ **Validaciones** con ENUM y constraints
- ✅ **Timestamps** automáticos (created_at, updated_at)

### 📊 Datos de Ejemplo Incluidos
- 🏢 **10 Departamentos PNP** (SECEJE, DIRADM, OFAD, OFIPLAN, etc.)
- 👥 **34 Áreas de trabajo** (Mesa de Partes, Secretaría, etc.)
- 🔐 **5 Roles** (ADMIN, JEFE, USUARIO, INVITADO, AUDITOR)
- 👤 **4 Usuarios de prueba** (admin, jefe1, usuario1, auditor1)
- 📄 **5 Tipos de documento** (Oficio, Memorándum, Carta, Informe, Solicitud)
- 📋 **2 Documentos de ejemplo**

## 🚀 Cómo Usar

### Opción 1: MySQL Workbench
1. Abrir MySQL Workbench
2. Conectarse al servidor MySQL
3. Abrir `mesa_partes_db_completa_con_bitacora_FINAL.sql`
4. Ejecutar el script completo (⚡ Lightning icon o Ctrl+Shift+Enter)

### Opción 2: Línea de comandos
```bash
mysql -u root -p < mesa_partes_db_completa_con_bitacora_FINAL.sql
```

## ⚠️ Importante

- Este script **ELIMINA** la base de datos existente (`DROP DATABASE IF EXISTS mesa_partes_db`)
- Asegúrate de hacer **backup** antes de ejecutar si tienes datos importantes
- Contraseñas de usuarios de prueba: `password123` (hash BCrypt)

## 📝 Usuarios de Prueba

| Usuario | Contraseña | Rol | Área |
|---------|-----------|-----|------|
| admin | password123 | ADMIN | Mesa de Partes |
| jefe1 | password123 | JEFE | Secretaría General |
| usuario1 | password123 | USUARIO | Oficina de Administración |
| auditor1 | password123 | AUDITOR | Auditoría Interna |

## 🔄 Versión
**Última actualización:** 17 de Noviembre de 2025  
**Versión:** FINAL - Completa con Bitácora  
**Total de líneas:** 640

---
💡 **Nota:** Este es el único archivo SQL necesario para instalar el sistema completo.
