@echo off
REM ============================================
REM   VERIFICACION DE BACKUPS - Mesa de Partes
REM ============================================

SET BACKUP_DIR=C:\backups\mesa_partes

echo.
echo ==========================================
echo   VERIFICACION DE BACKUPS
echo   Sistema Mesa de Partes Digital PNP
echo ==========================================
echo Fecha: %date%
echo Hora: %time%
echo ==========================================
echo.

REM Verificar si existe el directorio de backups
if not exist "%BACKUP_DIR%" (
    echo [ERROR] Directorio de backups no encontrado: %BACKUP_DIR%
    echo.
    echo SOLUCION: Ejecuta primero backup_windows.bat
    pause
    exit /b 1
)

REM Contar backups de base de datos
echo [1/5] Verificando backups de base de datos...
set COUNT=0
for %%F in ("%BACKUP_DIR%\db\*.sql") do set /a COUNT+=1
echo       Backups SQL encontrados: %COUNT%

REM Mostrar últimos 5 backups de BD
echo       Ultimos 5 backups:
dir "%BACKUP_DIR%\db\*.sql" /o-d /b 2>nul | findstr /n "^" | findstr "^[1-5]:"
echo.

REM Contar backups de uploads
echo [2/5] Verificando backups de uploads...
set COUNT_UPLOADS=0
for /d %%D in ("%BACKUP_DIR%\uploads\*") do set /a COUNT_UPLOADS+=1
echo       Carpetas de uploads: %COUNT_UPLOADS%
echo.

REM Contar archivos comprimidos
echo [3/5] Verificando archivos comprimidos...
set COUNT_ZIP=0
for %%Z in ("%BACKUP_DIR%\*.zip") do set /a COUNT_ZIP+=1
echo       Archivos ZIP encontrados: %COUNT_ZIP%
echo.

REM Calcular espacio total usado
echo [4/5] Calculando espacio en disco...
for /f "tokens=3" %%a in ('dir "%BACKUP_DIR%" /s /-c ^| find "bytes"') do set BYTES=%%a
set /a MB=%BYTES:~0,-6%
echo       Espacio total usado: %MB% MB
echo       Espacio disponible necesario: ~8 GB (para 30 dias)
echo.

REM Mostrar último registro del log
echo [5/5] Ultimo backup registrado:
if exist "%BACKUP_DIR%\backup.log" (
    for /f "delims=" %%i in ('type "%BACKUP_DIR%\backup.log"') do set LASTLINE=%%i
    echo       %LASTLINE%
) else (
    echo       [AVISO] No existe archivo backup.log
)

echo.
echo ==========================================
echo   RESUMEN DE VERIFICACION
echo ==========================================
echo Backups de BD (SQL): %COUNT%
echo Backups de Uploads: %COUNT_UPLOADS%
echo Archivos comprimidos: %COUNT_ZIP%
echo Espacio usado: %MB% MB
echo ==========================================

REM Verificar si hay suficientes backups
if %COUNT% LSS 3 (
    echo.
    echo [ADVERTENCIA] Menos de 3 backups encontrados
    echo Recomendacion: Ejecutar backup_windows.bat manualmente
)

REM Verificar si hay backups recientes
for /f "tokens=1-3 delims=/ " %%a in ("%date%") do (
    set TODAY=%%c%%a%%b
)

echo.
echo ==========================================
echo   DETALLES DE BACKUPS
echo ==========================================
echo.
echo Backups de Base de Datos (DB):
dir "%BACKUP_DIR%\db" /o-d /t:c 2>nul
echo.
echo Backups Comprimidos (ZIP):
dir "%BACKUP_DIR%\*.zip" /o-d /t:c 2>nul

echo.
echo ==========================================
echo Verificacion completada: %date% %time%
echo ==========================================
echo.
pause
