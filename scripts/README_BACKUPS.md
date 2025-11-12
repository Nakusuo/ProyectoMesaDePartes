Este archivo fue minimizado el 12 de noviembre de 2025.

Motivo: el contenido relevante de backups y procedimientos fue consolidado en el `README.md` raíz del proyecto.

Si necesitas la versión completa anterior, recupérala desde el historial de Git o solicita restauración.

Ruta original de scripts: `scripts/backup_windows.bat`, `scripts/backup_linux.sh`.# 📦 Sistema de Backups Automáticos - Mesa de Partes Digital PNP

## 📋 Tabla de Contenidos
- [Descripción](#descripción)
- [Requisitos](#requisitos)
- [Configuración Windows](#configuración-windows)
- [Configuración Linux/Mac](#configuración-linuxmac)
- [Restauración de Backups](#restauración-de-backups)
- [Verificación](#verificación)
- [Troubleshooting](#troubleshooting)

---

## 📖 Descripción

Sistema de respaldo automático que cumple con el **Requerimiento RNF03 - Fiabilidad**:
- ✅ Backups automáticos cada 5 horas
- ✅ Respaldo de base de datos MySQL
- ✅ Respaldo de archivos uploads (documentos PDF)
- ✅ Compresión automática para ahorrar espacio
- ✅ Limpieza automática de backups antiguos (>30 días)
- ✅ Log histórico de operaciones

---

## 🔧 Requisitos

### Windows
- MySQL Server 8.0 instalado
- PowerShell 5.0 o superior
- Permisos de administrador

### Linux/Mac
- MySQL Server 8.0 o MariaDB
- Bash shell
- Utilidades: `gzip`, `tar`, `find`
- Permisos de escritura en `/backup/`

---

## 🪟 Configuración Windows

### Paso 1: Configurar el Script

Editar `backup_windows.bat` y ajustar las rutas:

```batch
SET BACKUP_DIR=C:\backup\mesa_partes
SET MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin"
SET DB_NAME=mesa_partes_db
SET DB_USER=root
SET DB_PASS=root
SET UPLOADS_DIR=C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\ProyectoMesaDePartes\backend\uploads
```

### Paso 2: Probar el Script Manualmente

1. Abrir **CMD como Administrador**
2. Navegar a la carpeta de scripts:
   ```cmd
   cd C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\scripts
   ```
3. Ejecutar el script:
   ```cmd
   backup_windows.bat
   ```
4. Verificar que se crearon los archivos en `C:\backup\mesa_partes\`

### Paso 3: Programar Tarea Automática (cada 5 horas)

1. Abrir **Programador de tareas** (Task Scheduler)
   - Presionar `Win + R`, escribir `taskschd.msc`

2. **Crear Tarea Básica**
   - Click en "Crear tarea básica..."
   - Nombre: `Backup Mesa de Partes`
   - Descripción: `Backup automático cada 5 horas de la base de datos y archivos`

3. **Desencadenador**
   - Seleccionar: "Diaria"
   - Inicio: Fecha y hora actual
   - Repetir cada: **1 día**
   - Click en "Repetir la tarea cada: **5 horas**"
   - Durante: **Indefinidamente**

4. **Acción**
   - Acción: "Iniciar un programa"
   - Programa: `C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\scripts\backup_windows.bat`

5. **Condiciones**
   - ✅ Marcar: "Iniciar la tarea solo si el equipo está conectado a la corriente alterna"
   - ✅ Marcar: "Activar la tarea si el equipo pasa a alimentarse por CA"

6. **Configuración**
   - ✅ Marcar: "Permitir que la tarea se ejecute a petición"
   - ✅ Marcar: "Ejecutar la tarea lo antes posible después de un inicio programado perdido"

7. Click en **Aceptar** y proporcionar credenciales de administrador

### Paso 4: Verificar Tarea Programada

```cmd
schtasks /query /tn "Backup Mesa de Partes" /fo LIST /v
```

### Paso 5: Ejecutar Manualmente (prueba)

```cmd
schtasks /run /tn "Backup Mesa de Partes"
```

---

## 🐧 Configuración Linux/Mac

### Paso 1: Configurar el Script

Editar `backup_linux.sh`:

```bash
nano /path/to/scripts/backup_linux.sh
```

Ajustar variables:
```bash
BACKUP_DIR="/backup/mesa_partes"
DB_NAME="mesa_partes_db"
DB_USER="root"
DB_PASS="root"
UPLOADS_DIR="/path/to/ProyectoMesaDePartes/backend/uploads"
```

### Paso 2: Dar Permisos de Ejecución

```bash
chmod +x backup_linux.sh
```

### Paso 3: Probar el Script Manualmente

```bash
sudo ./backup_linux.sh
```

### Paso 4: Programar Cron Job (cada 5 horas)

1. Editar crontab:
   ```bash
   sudo crontab -e
   ```

2. Agregar línea (ejecutar cada 5 horas):
   ```cron
   0 */5 * * * /path/to/scripts/backup_linux.sh >> /var/log/backup_mesa_partes.log 2>&1
   ```

   Alternativa (horas específicas: 00:00, 05:00, 10:00, 15:00, 20:00):
   ```cron
   0 0,5,10,15,20 * * * /path/to/scripts/backup_linux.sh >> /var/log/backup_mesa_partes.log 2>&1
   ```

3. Guardar y salir (Ctrl+X, luego Y, luego Enter)

### Paso 5: Verificar Cron Job

```bash
sudo crontab -l
```

### Paso 6: Ver Logs

```bash
tail -f /var/log/backup_mesa_partes.log
```

---

## 🔄 Restauración de Backups

### Windows

1. Ejecutar script de restauración:
   ```cmd
   cd C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\scripts
   restaurar_backup_windows.bat
   ```

2. Seleccionar el archivo de backup a restaurar
3. Confirmar con `SI`
4. Esperar a que complete la restauración

### Linux/Mac

1. Listar backups disponibles:
   ```bash
   ls -lh /backup/mesa_partes/db_*.sql.gz
   ```

2. Descomprimir backup:
   ```bash
   gunzip /backup/mesa_partes/db_20251110_1430.sql.gz
   ```

3. Restaurar base de datos:
   ```bash
   mysql -u root -p mesa_partes_db < /backup/mesa_partes/db_20251110_1430.sql
   ```

4. Restaurar uploads (opcional):
   ```bash
   tar -xzf /backup/mesa_partes/uploads_20251110_1430.tar.gz -C /path/to/restore/
   ```

---

## ✅ Verificación

### Verificar que los Backups se Ejecutan

**Windows:**
```cmd
dir C:\backup\mesa_partes\*.zip /o-d
type C:\backup\mesa_partes\backup_history.log
```

**Linux:**
```bash
ls -lht /backup/mesa_partes/*.gz | head -10
cat /backup/mesa_partes/backup_history.log
```

### Verificar Tamaño de Backups

**Windows:**
```cmd
dir C:\backup\mesa_partes /s
```

**Linux:**
```bash
du -sh /backup/mesa_partes/*
```

### Probar Restauración (en entorno de prueba)

1. Crear base de datos de prueba
2. Restaurar backup en ella
3. Verificar integridad de datos

---

## 🐛 Troubleshooting

### Error: "Access denied for user"

**Solución:**
```sql
-- Conectar a MySQL como root
mysql -u root -p

-- Dar permisos al usuario
GRANT ALL PRIVILEGES ON mesa_partes_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### Error: "mysqldump: command not found"

**Windows:** Agregar MySQL al PATH
```cmd
setx PATH "%PATH%;C:\Program Files\MySQL\MySQL Server 8.0\bin"
```

**Linux:** Instalar MySQL client
```bash
sudo apt-get install mysql-client
```

### Error: "No space left on device"

**Solución:**
1. Verificar espacio disponible:
   ```bash
   df -h
   ```
2. Limpiar backups antiguos manualmente:
   ```bash
   find /backup/mesa_partes -name "*.gz" -mtime +15 -delete
   ```

### Backup no se ejecuta automáticamente

**Windows:**
1. Verificar que la tarea está habilitada
2. Revisar el historial en Task Scheduler
3. Verificar permisos de ejecución

**Linux:**
1. Verificar que cron está corriendo:
   ```bash
   sudo systemctl status cron
   ```
2. Revisar logs de cron:
   ```bash
   sudo grep CRON /var/log/syslog
   ```

---

## 📊 Ejemplo de Output Exitoso

```
============================================
  BACKUP MESA DE PARTES DIGITAL - PNP
============================================
Fecha: 2025-11-10
Hora: 14:30:00
Base de datos: mesa_partes_db
Directorio destino: C:\backup\mesa_partes
============================================

[1/3] Realizando backup de base de datos...
[OK] Backup de base de datos completado: db_20251110_1430.sql
[2/3] Comprimiendo backup de base de datos...
[OK] Archivo comprimido: db_20251110_1430.zip
[OK] Archivo SQL original eliminado
[3/3] Realizando backup de archivos uploads...
[OK] Backup de uploads completado: uploads_20251110_1430.zip

Limpiando backups antiguos (mayores a 30 días)...
[OK] Backups antiguos eliminados

============================================
  RESUMEN DEL BACKUP
============================================
db_20251110_1430.zip      (2.5 MB)
uploads_20251110_1430.zip (15.3 MB)
============================================
[2025-11-10 14:30:15] Backup completado exitosamente
============================================
```

---

## 📞 Soporte

Para problemas o consultas:
- Revisar logs en `C:\backup\mesa_partes\backup_history.log` (Windows)
- Revisar logs en `/var/log/backup_mesa_partes.log` (Linux)
- Consultar documentación de MySQL: https://dev.mysql.com/doc/

---

## ✨ Cumplimiento de Requerimientos

Este sistema cumple con:

✅ **RNF03 - Fiabilidad (respaldo de datos)**
- Backups automáticos cada 5 horas ✓
- Respaldo de base de datos ✓
- Respaldo de archivos adjuntos ✓
- Compresión para optimizar espacio ✓
- Limpieza automática de archivos antiguos ✓
- Procedimiento documentado de restauración ✓

**Estado:** ✅ IMPLEMENTADO (100%)
