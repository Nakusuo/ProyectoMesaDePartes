# ⚡ GUÍA RÁPIDA: Configurar Backup Automático en 5 Minutos

## 🎯 Objetivo
Configurar backups automáticos cada 5 horas de tu base de datos y archivos.

---

## 📝 PASO 1: Configurar Rutas (1 minuto)

### Abre el archivo:
```
C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat
```

### Verifica estas líneas (12-17):
```batch
SET BACKUP_DIR=C:\backups\mesa_partes     ← ¿Dónde guardar backups?
SET DB_USER=root                           ← ¿Tu usuario MySQL?
SET DB_PASS=root                           ← ¿Tu contraseña MySQL?
SET DB_NAME=mesa_partes_db                 ← ¿Nombre de tu BD?
SET MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
SET UPLOADS_DIR=C:\Users\User\ProyectoMesaDePartes\backend\uploads
```

**¿Estás en otro disco?** Cambia `C:\backups` por `D:\backups` o donde prefieras.

---

## 🧪 PASO 2: Probar el Backup Manual (1 minuto)

### Abre CMD como Administrador:
1. Presiona `Win + X`
2. Selecciona "Terminal (Administrador)"

### Ejecuta:
```cmd
cd C:\Users\User\ProyectoMesaDePartes\scripts
backup_windows.bat
```

### ✅ Deberías ver:
```
==========================================
  BACKUP AUTOMATICO - Mesa de Partes PNP
==========================================

[1/3] Respaldando base de datos mesa_partes_db...
[OK] Base de datos respaldada: backup_20251117_143022.sql
    Tamano: 2456789 bytes

[2/3] Respaldando archivos uploads...
[OK] Archivos respaldados: backup_20251117_143022\
    Archivos copiados: 15

[3/3] Comprimiendo backup...
[OK] Backup comprimido: backup_completo_20251117_143022.zip

==========================================
  BACKUP COMPLETADO: 20251117_143022
==========================================
```

**❌ ¿Hubo error?** Lee la sección "Solución de Problemas" al final.

---

## 🤖 PASO 3: Programar Tarea Automática (3 minutos)

### 3.1 Abrir Programador de Tareas
1. Presiona `Win + R`
2. Escribe: `taskschd.msc`
3. Enter

### 3.2 Crear Tarea Nueva
- Clic en **"Crear tarea..."** (panel derecho)
- ⚠️ NO uses "Crear tarea básica"

### 3.3 Configurar Pestaña "GENERAL"
```
Nombre: Backup Mesa de Partes PNP
Descripción: Backup automático cada 5 horas

☑ Ejecutar con los privilegios más altos
☑ Ejecutar tanto si el usuario inició sesión como si no
```

### 3.4 Configurar Pestaña "DESENCADENADORES"
Clic en **"Nuevo..."**

```
Iniciar la tarea: Al iniciar sesión
☑ Repetir la tarea cada: 5 horas
   Durante: Indefinidamente
☑ Habilitado
```

Clic en **"Aceptar"**

**OPCIONAL:** Agrega otro desencadenador "Al iniciar" para backups desde que arranca Windows.

### 3.5 Configurar Pestaña "ACCIONES"
Clic en **"Nuevo..."**

```
Acción: Iniciar un programa

Programa o script:
C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat

Iniciar en (opcional):
C:\Users\User\ProyectoMesaDePartes\scripts
```

Clic en **"Aceptar"**

### 3.6 Configurar Pestaña "CONDICIONES"
```
☐ Iniciar la tarea solo si el equipo está conectado a CA (DESMARCAR)
☑ Activar la tarea si la conexión de red está disponible
```

### 3.7 Configurar Pestaña "CONFIGURACIÓN"
```
☑ Permitir que la tarea se ejecute a petición
☑ Ejecutar la tarea lo antes posible después de perder un inicio
☑ Si la tarea no finaliza cuando se solicita, forzar su detención

Si la tarea ya se está ejecutando: No iniciar una nueva instancia
```

### 3.8 Guardar Tarea
1. Clic en **"Aceptar"**
2. Ingresa tu contraseña de Windows (si se solicita)

---

## ✅ PASO 4: Verificar que Funciona (1 minuto)

### 4.1 Ejecutar Manualmente la Tarea
1. En el Programador, busca "Backup Mesa de Partes PNP"
2. Clic derecho → **"Ejecutar"**
3. Espera 30 segundos

### 4.2 Verificar que se creó el backup
Abre explorador de archivos:
```
C:\backups\mesa_partes
```

Deberías ver:
```
📁 db\
   └── backup_20251117_144530.sql
📁 uploads\
   └── backup_20251117_144530\
📄 backup_completo_20251117_144530.zip
📄 backup.log
```

### 4.3 Verificar con el Script
```cmd
cd C:\Users\User\ProyectoMesaDePartes\scripts
verificar_backups.bat
```

---

## 🎉 ¡LISTO! Tu sistema de backups está activo

### 📊 ¿Qué hace ahora?
- ✅ Backup automático cada **5 horas**
- ✅ Backup al iniciar sesión
- ✅ Backup al iniciar Windows (si agregaste el 2do desencadenador)
- ✅ Limpia backups mayores a **30 días** automáticamente
- ✅ Guarda log de todas las ejecuciones

### 📅 Próximas ejecuciones
Para ver cuándo se ejecutará:
1. Programador de Tareas
2. Busca "Backup Mesa de Partes PNP"
3. Clic derecho → Propiedades
4. Pestaña "Desencadenadores" - verás las próximas ejecuciones

---

## 🔧 Solución de Problemas Rápida

### ❌ "mysqldump no se reconoce como comando"
**Causa:** Ruta incorrecta de MySQL  
**Solución:**
```cmd
where mysqldump
```
Copia la ruta completa y pégala en línea 16 del script `backup_windows.bat`:
```batch
SET MYSQL_BIN="TU_RUTA_AQUI\mysqldump.exe"
```

### ❌ "Access denied for user 'root'@'localhost'"
**Causa:** Contraseña incorrecta  
**Solución:** Verifica tu contraseña MySQL:
```cmd
mysql -u root -p
```
Si funciona, actualiza línea 14:
```batch
SET DB_PASS=tu_contraseña_real
```

### ❌ La tarea no se ejecuta automáticamente
**Causa:** Faltan permisos  
**Solución:**
1. Programador de Tareas
2. Clic derecho en tu tarea → Propiedades
3. Pestaña "General"
4. ☑ Marca "Ejecutar con los privilegios más altos"
5. ☑ Marca "Ejecutar tanto si el usuario inició sesión como si no"

### ❌ "No se pudo comprimir (7-Zip no disponible)"
**No es crítico.** El backup se guarda sin comprimir.  
**Si quieres compresión:** Instala 7-Zip desde https://www.7-zip.org/

---

## 📞 Comandos Útiles

### Ver últimos backups
```cmd
dir C:\backups\mesa_partes\db /o-d
```

### Ver log completo
```cmd
type C:\backups\mesa_partes\backup.log
```

### Ejecutar backup ahora
```cmd
C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat
```

### Verificar estado de backups
```cmd
C:\Users\User\ProyectoMesaDePartes\scripts\verificar_backups.bat
```

---

## 📈 ¿Cuánto espacio necesito?

### Cálculo estimado:

| Componente | Tamaño | Backups/día | Espacio/mes |
|------------|--------|-------------|-------------|
| Base de datos | 5 MB | ~5 | ~750 MB |
| Uploads | 50 MB | ~5 | ~7.5 GB |
| **TOTAL** | **55 MB** | **~5** | **~8 GB** |

**Recomendación:** Mantén al menos **10 GB libres** en el disco donde guardas backups.

---

## 🎯 Checklist Final

Marca cada item cuando lo completes:

- [ ] ✅ Script configurado con mis rutas
- [ ] ✅ Backup manual ejecutado con éxito
- [ ] ✅ Carpeta C:\backups\mesa_partes existe
- [ ] ✅ Tarea programada creada
- [ ] ✅ Tarea configurada cada 5 horas
- [ ] ✅ Tarea ejecutada manualmente (funciona)
- [ ] ✅ Archivo backup.log se crea
- [ ] ✅ Tengo 10+ GB de espacio libre

---

## 💡 Tips Adicionales

### Para backups más frecuentes (cada 3 horas):
En el Programador de Tareas:
- Propiedades → Desencadenadores → Editar
- Cambiar "5 horas" por "3 horas"

### Para guardar backups más días (45 días en vez de 30):
Edita `backup_windows.bat` línea 102:
```batch
forfiles /p "%BACKUP_DIR%\db" /s /m *.sql /d -45 /c "cmd /c del @path" 2>nul
```

### Para recibir notificaciones por email:
Necesitarás configurar un script PowerShell adicional con SMTP.
(Contacta si lo necesitas)

---

## 📚 Archivos Creados

Después de esta configuración tendrás:

```
C:\Users\User\ProyectoMesaDePartes\scripts\
├── backup_windows.bat                    ← Script principal de backup
├── restaurar_backup_windows.bat          ← Para restaurar backups
├── verificar_backups.bat                 ← Para verificar estado
├── CONFIGURAR_BACKUP_AUTOMATICO.md       ← Guía completa
└── GUIA_RAPIDA_BACKUP.md                 ← Esta guía (5 minutos)

C:\backups\mesa_partes\
├── db\                                    ← Backups de base de datos
├── uploads\                               ← Backups de archivos
├── backup.log                             ← Registro de ejecuciones
└── backup_completo_*.zip                  ← Backups comprimidos
```

---

## 🎉 ¡Felicidades!

Tu sistema Mesa de Partes ahora tiene backups automáticos configurados.

**Siguiente paso:** Revisa este documento en 24 horas para verificar que se ejecutaron al menos 4-5 backups.

```cmd
verificar_backups.bat
```

**¿Dudas?** Consulta el documento completo: `CONFIGURAR_BACKUP_AUTOMATICO.md`
