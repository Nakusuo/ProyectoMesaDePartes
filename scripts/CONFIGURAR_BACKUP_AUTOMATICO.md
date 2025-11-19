# 🔄 Configuración de Backups Automáticos - Mesa de Partes PNP

## 📋 Requisitos Previos

✅ MySQL 8.0.40 instalado  
✅ Windows 10/11  
✅ Privilegios de administrador  
✅ Espacio en disco (mínimo 5GB recomendado)

---

## 🛠️ Paso 1: Configurar el Script de Backup

### 1.1 Abrir el script en modo edición

```cmd
notepad C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat
```

### 1.2 Verificar y ajustar estas variables (líneas 12-17):

```batch
SET BACKUP_DIR=C:\backups\mesa_partes          ← Carpeta donde se guardarán los backups
SET DB_USER=root                                ← Tu usuario MySQL
SET DB_PASS=root                                ← Tu contraseña MySQL
SET DB_NAME=mesa_partes_db                      ← Nombre de tu base de datos
SET MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"  ← Ruta de MySQL
SET UPLOADS_DIR=C:\Users\User\ProyectoMesaDePartes\backend\uploads        ← Carpeta uploads
```

### 1.3 Guardar el archivo (Ctrl + S)

---

## ⚡ Paso 2: Probar el Backup Manual

### 2.1 Abrir CMD como Administrador
- Presiona `Win + X`
- Selecciona "Terminal (Administrador)" o "CMD (Administrador)"

### 2.2 Ejecutar el script manualmente

```cmd
cd C:\Users\User\ProyectoMesaDePartes\scripts
backup_windows.bat
```

### 2.3 Verificar que se creó el backup

```cmd
dir C:\backups\mesa_partes
```

**Deberías ver:**
```
C:\backups\mesa_partes\
├── db\
│   └── backup_20251117_143022.sql
├── uploads\
│   └── backup_20251117_143022\
└── backup_completo_20251117_143022.zip (si tienes 7-Zip)
```

---

## 🤖 Paso 3: Programar Backup Automático cada 5 horas

### 3.1 Abrir el Programador de Tareas
- Presiona `Win + R`
- Escribe: `taskschd.msc`
- Presiona Enter

### 3.2 Crear Nueva Tarea
1. Clic en **"Crear tarea..."** (panel derecho)
2. **NO** uses "Crear tarea básica", usa "Crear tarea..."

### 3.3 Pestaña "General"
- **Nombre:** `Backup Mesa de Partes PNP`
- **Descripción:** `Backup automático cada 5 horas de base de datos y archivos`
- ✅ Marcar: **"Ejecutar con los privilegios más altos"**
- ✅ Marcar: **"Ejecutar tanto si el usuario inició sesión como si no"**
- **Configurar para:** Windows 10

### 3.4 Pestaña "Desencadenadores"
1. Clic en **"Nuevo..."**
2. Configurar:
   - **Iniciar la tarea:** Al iniciar sesión
   - ✅ Marcar: **"Repetir la tarea cada"** → Seleccionar **5 horas**
   - **Durante:** Indefinidamente
   - ✅ Marcar: **"Habilitado"**
3. Clic en **"Aceptar"**

4. Agregar otro desencadenador (para que empiece desde el inicio del sistema):
   - Clic en **"Nuevo..."**
   - **Iniciar la tarea:** Al iniciar
   - ✅ Marcar: **"Repetir la tarea cada"** → **5 horas**
   - **Durante:** Indefinidamente
   - Clic en **"Aceptar"**

### 3.5 Pestaña "Acciones"
1. Clic en **"Nuevo..."**
2. Configurar:
   - **Acción:** Iniciar un programa
   - **Programa o script:**
     ```
     C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat
     ```
   - **Iniciar en (opcional):**
     ```
     C:\Users\User\ProyectoMesaDePartes\scripts
     ```
3. Clic en **"Aceptar"**

### 3.6 Pestaña "Condiciones"
- ❌ **Desmarcar:** "Iniciar la tarea solo si el equipo está conectado a la corriente alterna"
- ✅ **Marcar:** "Activar la tarea si la conexión de red está disponible"

### 3.7 Pestaña "Configuración"
- ✅ Marcar: "Permitir que la tarea se ejecute a petición"
- ✅ Marcar: "Ejecutar la tarea lo antes posible después de perder un inicio programado"
- ✅ Marcar: "Si la tarea en ejecución no finaliza cuando se solicita, forzar su detención"
- **Si la tarea ya se está ejecutando:** "No iniciar una nueva instancia"

### 3.8 Finalizar
1. Clic en **"Aceptar"**
2. Si se solicita, ingresa tu contraseña de Windows

---

## ✅ Paso 4: Verificar la Tarea Programada

### 4.1 Ejecutar Tarea Manualmente
1. En el Programador de Tareas, busca "Backup Mesa de Partes PNP"
2. Clic derecho → **"Ejecutar"**
3. Espera 30 segundos
4. Verifica que se creó el backup en `C:\backups\mesa_partes`

### 4.2 Ver Historial de Ejecuciones
1. Clic derecho en la tarea → **"Propiedades"**
2. Pestaña **"Historial"**
3. Aquí verás cada vez que se ejecutó el backup

### 4.3 Ver Log de Backups
```cmd
notepad C:\backups\mesa_partes\backup.log
```

---

## 📊 Paso 5: Monitorear los Backups

### 5.1 Crear Script de Verificación

Crea un archivo `verificar_backups.bat` en la carpeta `scripts`:

```batch
@echo off
echo ==========================================
echo   VERIFICACION DE BACKUPS
echo ==========================================
echo.

SET BACKUP_DIR=C:\backups\mesa_partes

echo Ultimos 5 backups de base de datos:
dir "%BACKUP_DIR%\db\*.sql" /o-d /b | findstr /n "^" | findstr "^[1-5]:"

echo.
echo Espacio usado en backups:
dir "%BACKUP_DIR%" /s

echo.
echo Ultimo backup registrado en log:
type "%BACKUP_DIR%\backup.log" | findstr /n "^" | findstr /n "^" | findstr "[0-9]*:$"

echo.
echo ==========================================
pause
```

### 5.2 Ejecutar verificación periódicamente

```cmd
cd C:\Users\User\ProyectoMesaDePartes\scripts
verificar_backups.bat
```

---

## 🔧 Solución de Problemas

### ❌ Error: "mysqldump no se reconoce"
**Solución:** Verifica la ruta de MySQL en línea 16 del script:
```cmd
where mysqldump
```
Copia la ruta completa y actualiza `SET MYSQL_BIN=...`

### ❌ Error: "Access denied for user 'root'"
**Solución:** Verifica usuario y contraseña MySQL:
```cmd
mysql -u root -p
```
Si funciona, actualiza `DB_USER` y `DB_PASS` en el script.

### ❌ Error: La tarea no se ejecuta automáticamente
**Solución:**
1. Abre el Programador de Tareas
2. Encuentra tu tarea "Backup Mesa de Partes PNP"
3. Clic derecho → **"Propiedades"**
4. Verifica que:
   - ✅ Esté habilitada
   - ✅ "Ejecutar con los privilegios más altos" esté marcado
   - ✅ Los desencadenadores estén activos

### ❌ No se crean los archivos ZIP
**Solución:** Instala 7-Zip desde https://www.7-zip.org/
O usa la compresión PowerShell (ya incluida en el script alternativo).

---

## 📈 Cálculo de Espacio en Disco

### Estimación de tamaño de backup:

| Componente | Tamaño estimado | Backups/día | Espacio/mes |
|------------|-----------------|-------------|-------------|
| Base de datos SQL | ~5 MB | 4.8 | ~720 MB |
| Archivos uploads | ~50 MB | 4.8 | ~7.2 GB |
| **TOTAL** | **~55 MB** | **4.8** | **~7.92 GB** |

**Nota:** Con limpieza automática cada 30 días, necesitas ~8 GB de espacio.

---

## 🎯 Verificación Final

### Checklist de Configuración ✅

- [ ] Script `backup_windows.bat` configurado con rutas correctas
- [ ] Backup manual ejecutado exitosamente
- [ ] Carpeta `C:\backups\mesa_partes` creada
- [ ] Tarea programada creada en Windows
- [ ] Tarea configurada para repetir cada 5 horas
- [ ] Tarea ejecutada manualmente con éxito
- [ ] Log `backup.log` existe y se actualiza
- [ ] Suficiente espacio en disco (mínimo 10 GB)

---

## 📞 Comandos Rápidos

### Ejecutar backup manualmente
```cmd
C:\Users\User\ProyectoMesaDePartes\scripts\backup_windows.bat
```

### Ver últimos backups
```cmd
dir C:\backups\mesa_partes\db /o-d
```

### Ver log de backups
```cmd
type C:\backups\mesa_partes\backup.log
```

### Restaurar un backup
```cmd
C:\Users\User\ProyectoMesaDePartes\scripts\restaurar_backup_windows.bat backup_20251117_143022.sql
```

---

## 🎉 ¡Listo!

Tu sistema de backups automáticos está configurado. Se ejecutará:
- ✅ Cada 5 horas automáticamente
- ✅ Al iniciar el sistema
- ✅ Al iniciar sesión
- ✅ Con limpieza automática de backups > 30 días

**Próxima ejecución:** Verifica en el Programador de Tareas → "Backup Mesa de Partes PNP" → Propiedades → Desencadenadores

---

## 📚 Recursos Adicionales

- [Documentación MySQL Backup](https://dev.mysql.com/doc/refman/8.0/en/backup-and-recovery.html)
- [Programador de Tareas Windows](https://docs.microsoft.com/es-es/windows/win32/taskschd/task-scheduler-start-page)
- [Guía de Restauración](./restaurar_backup_windows.bat)
