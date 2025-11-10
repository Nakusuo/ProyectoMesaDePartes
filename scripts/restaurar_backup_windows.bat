@echo off
REM ============================================
REM Script de Restauración de Backup - Windows
REM Sistema Mesa de Partes Digital PNP
REM ============================================

REM Configuración
SET BACKUP_DIR=C:\backup\mesa_partes
SET MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin"
SET DB_NAME=mesa_partes_db
SET DB_USER=root
SET DB_PASS=root

echo.
echo ============================================
echo   RESTAURACION DE BACKUP - MESA DE PARTES
echo ============================================
echo.

REM Listar backups disponibles
echo Backups disponibles:
echo ============================================
dir "%BACKUP_DIR%\db_*.zip" /b /o-d
echo ============================================
echo.

REM Solicitar nombre del archivo
SET /P BACKUP_FILE="Ingrese el nombre del archivo a restaurar (ej: db_20251110_1430.zip): "

IF NOT EXIST "%BACKUP_DIR%\%BACKUP_FILE%" (
    echo [ERROR] Archivo no encontrado: %BACKUP_FILE%
    pause
    exit /b 1
)

echo.
echo [AVISO] Esta operación SOBRESCRIBIRA la base de datos actual
echo Base de datos: %DB_NAME%
echo Archivo: %BACKUP_FILE%
echo.
SET /P CONFIRM="¿Está seguro de continuar? (SI/NO): "

IF NOT "%CONFIRM%"=="SI" (
    echo Operación cancelada por el usuario
    pause
    exit /b 0
)

echo.
echo [1/3] Descomprimiendo backup...
powershell Expand-Archive -Path "%BACKUP_DIR%\%BACKUP_FILE%" -DestinationPath "%BACKUP_DIR%\temp_restore" -Force

REM Buscar archivo SQL descomprimido
FOR %%F IN ("%BACKUP_DIR%\temp_restore\*.sql") DO SET SQL_FILE=%%F

IF NOT DEFINED SQL_FILE (
    echo [ERROR] No se encontró archivo SQL en el backup
    rmdir /s /q "%BACKUP_DIR%\temp_restore"
    pause
    exit /b 1
)

echo [OK] Archivo descomprimido: %SQL_FILE%

echo.
echo [2/3] Eliminando base de datos actual...
%MYSQL_PATH%\mysql.exe -u %DB_USER% -p%DB_PASS% -e "DROP DATABASE IF EXISTS %DB_NAME%;"
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al eliminar base de datos
    rmdir /s /q "%BACKUP_DIR%\temp_restore"
    pause
    exit /b 1
)
echo [OK] Base de datos eliminada

echo.
echo [3/3] Creando base de datos y restaurando backup...
%MYSQL_PATH%\mysql.exe -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
%MYSQL_PATH%\mysql.exe -u %DB_USER% -p%DB_PASS% %DB_NAME% < "%SQL_FILE%"

IF %ERRORLEVEL% EQU 0 (
    echo [OK] Backup restaurado exitosamente
) ELSE (
    echo [ERROR] Fallo al restaurar backup
    rmdir /s /q "%BACKUP_DIR%\temp_restore"
    pause
    exit /b 1
)

REM Limpiar archivos temporales
rmdir /s /q "%BACKUP_DIR%\temp_restore"
echo [OK] Archivos temporales eliminados

echo.
echo ============================================
echo   RESTAURACION COMPLETADA EXITOSAMENTE
echo ============================================
echo Base de datos: %DB_NAME%
echo Archivo restaurado: %BACKUP_FILE%
echo Fecha: %date% %time%
echo ============================================
echo.

REM Registrar en log
echo [%date% %time%] Backup restaurado: %BACKUP_FILE% >> "%BACKUP_DIR%\restore_history.log"

pause
