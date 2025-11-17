# 📋 CONFIGURAR BACKUP AUTOMÁTICO EN WINDOWS

## Programar Tarea Automática cada 5 horas

### Método 1: Interfaz Gráfica (Recomendado)

1. **Abrir Programador de Tareas**
   - Presionar `Win + R`
   - Escribir: `taskschd.msc`
   - Presionar Enter

2. **Crear Tarea Básica**
   - Click derecho en "Biblioteca del Programador de Tareas"
   - Seleccionar "Crear tarea básica..."

3. **Configurar Tarea**
   ```
   Nombre: Backup Mesa de Partes PNP
   Descripción: Backup automático cada 5 horas de BD y archivos
   ```

4. **Desencadenador**
   - Seleccionar: "Diariamente"
   - Hora de inicio: `01:00:00` (1 AM)
   - Repetir cada: `5 horas`
   - Durante: `Indefinidamente`

5. **Acción**
   - Seleccionar: "Iniciar un programa"
   - Programa: `C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat`

6. **Opciones Avanzadas (Importante)**
   - Ir a pestaña "General"
   - ✅ Marcar: "Ejecutar tanto si el usuario inició sesión como si no"
   - ✅ Marcar: "Ejecutar con los privilegios más altos"
   - Configurar para: Windows 10/11

7. **Condiciones**
   - Pestaña "Condiciones"
   - ❌ Desmarcar: "Iniciar solo si el equipo está conectado a CA"
   - ❌ Desmarcar: "Detener si el equipo deja de estar conectado a CA"

8. **Configuración**
   - Pestaña "Configuración"
   - ✅ Marcar: "Permitir que la tarea se ejecute a petición"
   - ✅ Marcar: "Ejecutar la tarea lo antes posible si se omite..."
   - ✅ Marcar: "Si la tarea no se completa, reintentar cada: 10 minutos"

---

### Método 2: Línea de Comandos (PowerShell)

```powershell
# Ejecutar PowerShell como Administrador

# Crear la tarea programada
$action = New-ScheduledTaskAction -Execute "C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat"

$trigger = New-ScheduledTaskTrigger -Daily -At "01:00AM"
$trigger.Repetition = New-ScheduledTaskTrigger -Once -At "01:00AM" -RepetitionInterval (New-TimeSpan -Hours 5) -RepetitionDuration ([TimeSpan]::MaxValue)

$principal = New-ScheduledTaskPrincipal -UserId "SYSTEM" -LogonType ServiceAccount -RunLevel Highest

$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable

Register-ScheduledTask -TaskName "Backup Mesa de Partes PNP" -Action $action -Trigger $trigger -Principal $principal -Settings $settings -Description "Backup automático cada 5 horas"
```

---

### Verificar la Tarea

```cmd
# Ver tareas programadas
schtasks /query /tn "Backup Mesa de Partes PNP"

# Ejecutar tarea manualmente (prueba)
schtasks /run /tn "Backup Mesa de Partes PNP"

# Ver historial de ejecuciones
Get-EventLog -LogName Application -Source "Task Scheduler" | Where-Object {$_.Message -like "*Backup Mesa de Partes*"}
```

---

### Modificar Credenciales en el Script

Antes de programar la tarea, editar el archivo `backup_windows.bat`:

```batch
REM Configuración - EDITAR ESTOS VALORES
SET BACKUP_DIR=C:\backups\mesa_partes
SET DB_USER=root
SET DB_PASS=TU_PASSWORD_AQUI          ← CAMBIAR
SET DB_NAME=mesa_partes_db
SET MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
SET UPLOADS_DIR=C:\Users\User\ProyectoMesaDePartes\backend\uploads  ← VERIFICAR RUTA
```

---

### Verificar que Funciona

1. **Ejecutar manualmente el script**
   ```cmd
   cd C:\Users\User\ProyectoMesaDePartes\scripts
   backup_windows.bat
   ```

2. **Verificar que se crearon los archivos**
   - Ir a: `C:\backups\mesa_partes\`
   - Debe ver carpetas: `db\` y `uploads\`
   - Debe ver archivo: `backup.log`

3. **Probar la tarea programada**
   ```cmd
   schtasks /run /tn "Backup Mesa de Partes PNP"
   ```

4. **Revisar el log**
   ```cmd
   type C:\backups\mesa_partes\backup.log
   ```

---

### Ubicaciones de los Backups

```
C:\backups\mesa_partes\
├── db\
│   ├── backup_20251117_010000.sql
│   ├── backup_20251117_060000.sql
│   └── backup_20251117_110000.sql
├── uploads\
│   ├── backup_20251117_010000\
│   ├── backup_20251117_060000\
│   └── backup_20251117_110000\
├── backup_completo_20251117_010000.zip (si 7-Zip está instalado)
├── backup.log
└── error.log (solo si hay errores)
```

---

### Instalar 7-Zip para Compresión (Opcional)

Para habilitar la compresión automática de backups:

1. Descargar 7-Zip: https://www.7-zip.org/
2. Instalar en la ruta por defecto: `C:\Program Files\7-Zip\`
3. El script detectará automáticamente 7-Zip y comprimirá los backups

---

### Notificaciones por Email (Próximamente)

Para recibir notificaciones por email cuando se complete un backup:

```batch
REM Agregar al final del script backup_windows.bat
powershell -Command "Send-MailMessage -To 'admin@pnp.gob.pe' -From 'backup@pnp.gob.pe' -Subject 'Backup Completado' -Body 'Backup del %TIMESTAMP% completado exitosamente' -SmtpServer 'smtp.gmail.com' -Port 587 -UseSsl -Credential (Get-Credential)"
```

---

### Troubleshooting

#### Error: "mysqldump no reconocido"
```cmd
# Verificar ruta de MySQL
dir "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"

# Si no existe, buscar la instalación de MySQL
dir "C:\Program Files\MySQL\" /s /b | findstr mysqldump.exe
```

#### Error: "Acceso denegado"
- Ejecutar el Programador de Tareas como Administrador
- Verificar que la tarea está configurada con "privilegios más altos"

#### Backups no se crean
- Verificar que MySQL está corriendo
- Verificar credenciales en el script
- Revisar el archivo `error.log` en la carpeta de backups

---

### Restaurar un Backup

Para restaurar desde un backup:

```cmd
cd C:\Users\User\ProyectoMesaDePartes\scripts
restaurar_backup_windows.bat backup_20251117_010000.sql
```

---

**Última actualización:** 17 de noviembre de 2025  
**Próxima revisión:** Mensual
