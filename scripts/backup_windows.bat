@echo off
REM Backup automático para ProyectoMesaDePartes (Windows)
REM Fecha: 12 de noviembre de 2025

:: Configuración (editar según entorno)
set BACKUP_DIR=C:\backup\mesa_partes
set DATE=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set DB_NAME=mesa_partes_db
set DB_USER=root
set DB_PASS=root

:: Crear directorio si no existe
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

:: Ruta a mysqldump (ajustar si es necesario)
set MYSQLDUMP="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
if not exist %MYSQLDUMP% (
    set MYSQLDUMP=mysqldump
)

echo Iniciando backup de la base de datos: %DB_NAME% a %BACKUP_DIR%\db_%DATE%.sql
%MYSQLDUMP% -u %DB_USER% -p%DB_PASS% %DB_NAME% > "%BACKUP_DIR%\db_%DATE%.sql"

echo Comprimiendo backup SQL...
powershell -Command "Compress-Archive -Path '%BACKUP_DIR%\db_%DATE%.sql' -DestinationPath '%BACKUP_DIR%\db_%DATE%.zip' -Force"
del "%BACKUP_DIR%\db_%DATE%.sql"

echo Backup de archivos uploads...
set UPLOADS_DIR=%~dp0\ProyectoMesaDePartes\backend\uploads
if exist "%UPLOADS_DIR%" (
    powershell -Command "Compress-Archive -Path '%UPLOADS_DIR%\*' -DestinationPath '%BACKUP_DIR%\uploads_%DATE%.zip' -Force"
) else (
    echo No se encontró carpeta uploads en %UPLOADS_DIR%
)

echo Eliminando backups más antiguos a 30 días...
powershell -Command "Get-ChildItem -Path '%BACKUP_DIR%' -Recurse | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-30) } | Remove-Item -Force"

echo Backup completado: %DATE%
exit /b 0
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
