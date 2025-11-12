Este archivo fue minimizado el 12 de noviembre de 2025.

Motivo: el contenido fue consolidado en el `README.md` raíz. Mantengo este archivo con una nota para trazabilidad.

Si necesitas la versión completa anterior, recupérala desde el historial de Git.# 🚀 GUÍA RÁPIDA - Configurar Backups en 5 Minutos

## ⚡ Para Windows (lo más común)

### 1️⃣ Ejecutar Backup Manual (AHORA MISMO)

```cmd
cd C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\scripts
backup_windows.bat
```

✅ Verás: "Backup completado exitosamente"  
📁 Los archivos estarán en: `C:\backup\mesa_partes\`

---

### 2️⃣ Programar Automático (cada 5 horas)

1. **Abrir Programador de Tareas:**
   - Presiona `Win + R`
   - Escribe: `taskschd.msc`
   - Enter

2. **Crear Tarea:**
   - Click derecho en "Biblioteca del Programador de tareas"
   - "Crear tarea básica..."

3. **Configurar:**
   - **Nombre:** `Backup Mesa de Partes`
   - **Desencadenador:** Diaria
   - **Repetir cada:** 5 horas
   - **Acción:** Iniciar programa
   - **Programa:** 
     ```
     C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\scripts\backup_windows.bat
     ```

4. **¡Listo!** El backup se ejecutará automáticamente cada 5 horas

---

### 3️⃣ Verificar que Funciona

```cmd
dir C:\backup\mesa_partes\*.zip
```

Deberías ver archivos como:
- `db_20251110_1430.zip` (base de datos)
- `uploads_20251110_1430.zip` (archivos PDF)

---

## 🔄 ¿Cómo Restaurar un Backup?

1. Ejecutar:
   ```cmd
   cd C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\scripts
   restaurar_backup_windows.bat
   ```

2. Seleccionar el archivo de backup
3. Escribir `SI` para confirmar
4. ¡Listo! Base de datos restaurada

---

## 📊 ¿Qué se Respalda?

✅ Base de datos completa (`mesa_partes_db`)  
✅ Todos los documentos PDF subidos (`uploads/documentos/`)  
✅ Comprimido automáticamente (ahorra espacio)  
✅ Se guardan por 30 días (luego se borran automáticamente)

---

## ❓ Problemas Comunes

### "No se encuentra MySQL"
**Solución:** Editar `backup_windows.bat` y cambiar la ruta de MySQL:
```batch
SET MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin"
```

### "Access denied"
**Solución:** Verificar usuario y contraseña en el script:
```batch
SET DB_USER=root
SET DB_PASS=root
```

### "No se puede crear directorio"
**Solución:** Ejecutar CMD como Administrador

---

## 🎯 Checklist de Implementación

- [ ] Ejecuté backup manual y funcionó
- [ ] Verifiqué que se crearon los archivos .zip
- [ ] Programé la tarea automática cada 5 horas
- [ ] Probé restaurar un backup
- [ ] Documenté la ubicación de los backups
- [ ] ✅ **RNF03 - Fiabilidad COMPLETADO**

---

## 📞 ¿Necesitas Ayuda?

Revisa el archivo completo: `README_BACKUPS.md`

O verifica los logs:
```cmd
type C:\backup\mesa_partes\backup_history.log
```
