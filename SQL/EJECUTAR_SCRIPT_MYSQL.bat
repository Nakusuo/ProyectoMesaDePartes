@echo off
chcp 65001 >nul
echo ================================================================
echo    EJECUTAR SCRIPT SQL - Mesa de Partes con BITÁCORA
echo ================================================================
echo.
echo Este script ejecutará mesa_partes_db_completa_con_bitacora_FINAL.sql
echo.
echo IMPORTANTE: 
echo - Necesitas tener MySQL instalado
echo - Debes conocer la contraseña de root
echo.
pause

echo.
echo Buscando MySQL...
echo.

REM Buscar mysql.exe en ubicaciones comunes
set MYSQL_PATH=
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe
if exist "C:\xampp\mysql\bin\mysql.exe" set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe
if exist "C:\wamp64\bin\mysql\mysql8.0.36\bin\mysql.exe" set MYSQL_PATH=C:\wamp64\bin\mysql\mysql8.0.36\bin\mysql.exe

if "%MYSQL_PATH%"=="" (
    echo ❌ No se encontró MySQL en las ubicaciones comunes.
    echo.
    echo Por favor, ejecuta manualmente en MySQL Workbench:
    echo.
    echo 1. Abre MySQL Workbench
    echo 2. Conéctate como root
    echo 3. File -^> Open SQL Script
    echo 4. Selecciona: mesa_partes_db_completa_con_bitacora_FINAL.sql
    echo 5. Presiona el botón de rayo ⚡ o Ctrl+Shift+Enter
    echo.
    pause
    exit
)

echo ✅ MySQL encontrado en: %MYSQL_PATH%
echo.

REM Ejecutar el script
echo Ejecutando script...
echo Ingresa la contraseña de MySQL root cuando se solicite:
echo.

"%MYSQL_PATH%" -u root -p < "%~dp0mesa_partes_db_completa_con_bitacora_FINAL.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================================
    echo    ✅ ¡ÉXITO! Script ejecutado correctamente
    echo ================================================================
    echo.
    echo La tabla bitácora ha sido creada exitosamente.
    echo Los triggers están configurados.
    echo Se insertaron 10 registros históricos.
    echo.
    echo Ahora puedes:
    echo 1. Recargar la página de bitácora en el navegador
    echo 2. El error 404 debe desaparecer
    echo 3. Verás los 10 documentos de entrada
    echo.
) else (
    echo.
    echo ================================================================
    echo    ❌ ERROR al ejecutar el script
    echo ================================================================
    echo.
    echo Posibles causas:
    echo - Contraseña incorrecta
    echo - MySQL no está corriendo
    echo.
    echo SOLUCIÓN: Ejecuta manualmente en MySQL Workbench
    echo.
)

pause
