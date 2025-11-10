@echo off
REM ============================================
REM Script de Backup Automático - Windows
REM Sistema Mesa de Partes Digital PNP
REM ============================================

REM Configuración
SET BACKUP_DIR=C:\backup\mesa_partes
SET MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin"
SET DB_NAME=mesa_partes_db
SET DB_USER=root
SET DB_PASS=root
SET UPLOADS_DIR=C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\ProyectoMesaDePartes\backend\uploads

REM Crear marca de tiempo
FOR /f "tokens=2-4 delims=/ " %%a IN ('date /t') DO (SET mydate=%%c%%a%%b)
FOR /f "tokens=1-2 delims=/:" %%a IN ("%TIME%") DO (SET mytime=%%a%%b)
SET TIMESTAMP=%mydate%_%mytime%

REM Crear directorio de backup si no existe
IF NOT EXIST "%BACKUP_DIR%" (
    mkdir "%BACKUP_DIR%"
    echo [%date% %time%] Directorio de backup creado: %BACKUP_DIR%
) ELSE (
    echo [%date% %time%] Usando directorio de backup existente: %BACKUP_DIR%
)

echo.
echo ============================================
echo   BACKUP MESA DE PARTES DIGITAL - PNP
echo ============================================
echo Fecha: %date%
echo Hora: %time%
echo Base de datos: %DB_NAME%
echo Directorio destino: %BACKUP_DIR%
echo ============================================
echo.

REM Backup de la base de datos
echo [1/3] Realizando backup de base de datos...
%MYSQL_PATH%\mysqldump.exe -u %DB_USER% -p%DB_PASS% --single-transaction --routines --triggers --events %DB_NAME% > "%BACKUP_DIR%\db_%TIMESTAMP%.sql" 2>"%BACKUP_DIR%\error_db.log"

IF %ERRORLEVEL% EQU 0 (
    echo [OK] Backup de base de datos completado: db_%TIMESTAMP%.sql
    
    REM Comprimir el SQL
    echo [2/3] Comprimiendo backup de base de datos...
    powershell Compress-Archive -Path "%BACKUP_DIR%\db_%TIMESTAMP%.sql" -DestinationPath "%BACKUP_DIR%\db_%TIMESTAMP%.zip" -Force
    
    IF EXIST "%BACKUP_DIR%\db_%TIMESTAMP%.zip" (
        echo [OK] Archivo comprimido: db_%TIMESTAMP%.zip
        del "%BACKUP_DIR%\db_%TIMESTAMP%.sql"
        echo [OK] Archivo SQL original eliminado
    )
) ELSE (
    echo [ERROR] Fallo al realizar backup de base de datos
    echo Ver detalles en: %BACKUP_DIR%\error_db.log
)

REM Backup de archivos uploads
IF EXIST "%UPLOADS_DIR%" (
    echo [3/3] Realizando backup de archivos uploads...
    powershell Compress-Archive -Path "%UPLOADS_DIR%\*" -DestinationPath "%BACKUP_DIR%\uploads_%TIMESTAMP%.zip" -Force
    
    IF EXIST "%BACKUP_DIR%\uploads_%TIMESTAMP%.zip" (
        echo [OK] Backup de uploads completado: uploads_%TIMESTAMP%.zip
    ) ELSE (
        echo [ERROR] Fallo al realizar backup de uploads
    )
) ELSE (
    echo [AVISO] Directorio uploads no encontrado: %UPLOADS_DIR%
)

REM Limpiar backups antiguos (mayores a 30 días)
echo.
echo Limpiando backups antiguos (mayores a 30 días)...
forfiles /p "%BACKUP_DIR%" /s /m *.zip /d -30 /c "cmd /c del @path" 2>nul
IF %ERRORLEVEL% EQU 0 (
    echo [OK] Backups antiguos eliminados
) ELSE (
    echo [INFO] No hay backups antiguos para eliminar
)

REM Resumen
echo.
echo ============================================
echo   RESUMEN DEL BACKUP
echo ============================================
dir "%BACKUP_DIR%\*%TIMESTAMP%*" /b 2>nul
echo ============================================
echo [%date% %time%] Backup completado exitosamente
echo ============================================
echo.

REM Registrar en log
echo [%date% %time%] Backup completado - db_%TIMESTAMP%.zip, uploads_%TIMESTAMP%.zip >> "%BACKUP_DIR%\backup_history.log"

pause
