@echo off
REM ============================================
REM   BACKUP AUTOMÁTICO - Mesa de Partes PNP
REM   Script actualizado con mejoras de seguridad
REM ============================================

echo.
echo ==========================================
echo   BACKUP AUTOMATICO - Mesa de Partes PNP
echo ==========================================
echo.

REM Configuración - EDITAR ESTOS VALORES
SET BACKUP_DIR=C:\backups\mesa_partes
SET DB_USER=root
SET DB_PASS=root
SET DB_NAME=mesa_partes_db
SET MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
SET UPLOADS_DIR=C:\Users\User\ProyectoMesaDePartes\backend\uploads

REM Crear fecha y hora para nombre de archivo
SET FECHA=%date:~-4%%date:~3,2%%date:~0,2%
SET HORA=%time:~0,2%%time:~3,2%%time:~6,2%
SET HORA=%HORA: =0%
SET TIMESTAMP=%FECHA%_%HORA%

REM Crear directorios de backup si no existen
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
if not exist "%BACKUP_DIR%\db" mkdir "%BACKUP_DIR%\db"
if not exist "%BACKUP_DIR%\uploads" mkdir "%BACKUP_DIR%\uploads"

echo [%date% %time%] Iniciando backup...
echo Ubicacion: %BACKUP_DIR%
echo.

REM ==========================================
REM PASO 1: Backup de Base de Datos
REM ==========================================
echo [1/3] Respaldando base de datos %DB_NAME%...
%MYSQL_BIN% -u %DB_USER% -p%DB_PASS% --single-transaction --routines --triggers --events %DB_NAME% > "%BACKUP_DIR%\db\backup_%TIMESTAMP%.sql" 2>"%BACKUP_DIR%\error.log"

if %ERRORLEVEL% EQU 0 (
    echo [OK] Base de datos respaldada: backup_%TIMESTAMP%.sql
    REM Obtener tamaño del archivo
    for %%A in ("%BACKUP_DIR%\db\backup_%TIMESTAMP%.sql") do (
        set SIZE=%%~zA
    )
    echo     Tamano: %SIZE% bytes
) else (
    echo [ERROR] Fallo al respaldar base de datos
    echo Ver detalles en: %BACKUP_DIR%\error.log
    exit /b 1
)

REM ==========================================
REM PASO 2: Backup de Archivos Uploads
REM ==========================================
echo.
echo [2/3] Respaldando archivos uploads...
if exist "%UPLOADS_DIR%" (
    xcopy "%UPLOADS_DIR%" "%BACKUP_DIR%\uploads\backup_%TIMESTAMP%\" /E /I /H /Y /Q >nul 2>&1
    
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Archivos respaldados: backup_%TIMESTAMP%\
        REM Contar archivos copiados
        for /f %%A in ('dir /b /s "%BACKUP_DIR%\uploads\backup_%TIMESTAMP%" ^| find /c /v ""') do set COUNT=%%A
        echo     Archivos copiados: %COUNT%
    ) else (
        echo [ERROR] Fallo al respaldar archivos
    )
) else (
    echo [AVISO] No se encontro carpeta uploads en: %UPLOADS_DIR%
)

REM ==========================================
REM PASO 3: Comprimir Backup (Opcional)
REM ==========================================
echo.
echo [3/3] Comprimiendo backup...
if exist "C:\Program Files\7-Zip\7z.exe" (
    "C:\Program Files\7-Zip\7z.exe" a -tzip "%BACKUP_DIR%\backup_completo_%TIMESTAMP%.zip" "%BACKUP_DIR%\db\backup_%TIMESTAMP%.sql" "%BACKUP_DIR%\uploads\backup_%TIMESTAMP%" >nul 2>&1
    
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Backup comprimido: backup_completo_%TIMESTAMP%.zip
        REM Opcional: Eliminar archivos sin comprimir para ahorrar espacio
        REM del "%BACKUP_DIR%\db\backup_%TIMESTAMP%.sql"
        REM rmdir /s /q "%BACKUP_DIR%\uploads\backup_%TIMESTAMP%"
    ) else (
        echo [AVISO] No se pudo comprimir (7-Zip no disponible)
    )
) else (
    echo [AVISO] 7-Zip no instalado, backup sin comprimir
    echo Instalar desde: https://www.7-zip.org/
)

REM ==========================================
REM PASO 4: Limpiar Backups Antiguos
REM ==========================================
echo.
echo [4/4] Limpiando backups antiguos (mas de 30 dias)...
forfiles /p "%BACKUP_DIR%\db" /s /m *.sql /d -30 /c "cmd /c del @path" 2>nul
forfiles /p "%BACKUP_DIR%\uploads" /s /d -30 /c "cmd /c rmdir /s /q @path" 2>nul
forfiles /p "%BACKUP_DIR%" /m *.zip /d -30 /c "cmd /c del @path" 2>nul
echo [OK] Limpieza completada

REM ==========================================
REM REGISTRO EN LOG
REM ==========================================
echo.
echo ==========================================
echo   BACKUP COMPLETADO: %TIMESTAMP%
echo ==========================================
echo Ubicacion: %BACKUP_DIR%
echo.

REM Registrar en log
echo [%date% %time%] Backup completado: %TIMESTAMP% >> "%BACKUP_DIR%\backup.log"

REM Mostrar resumen
echo RESUMEN DEL BACKUP:
echo - Base de datos: backup_%TIMESTAMP%.sql
echo - Archivos: backup_%TIMESTAMP%\
if exist "%BACKUP_DIR%\backup_completo_%TIMESTAMP%.zip" (
    echo - Comprimido: backup_completo_%TIMESTAMP%.zip
)
echo.
echo Para restaurar este backup, ejecutar:
echo   restaurar_backup_windows.bat backup_%TIMESTAMP%.sql
echo.

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
