@echo off
chcp 65001 > nul
echo ====================================
echo EJECUTANDO SCRIPT COMPLETO DE BITÁCORA
echo ====================================
echo.

REM Buscar MySQL en rutas comunes
set "MYSQL_PATH="
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
) else if exist "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe" (
    set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe"
) else if exist "C:\xampp\mysql\bin\mysql.exe" (
    set "MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe"
) else if exist "C:\wamp64\bin\mysql\mysql8.0.39\bin\mysql.exe" (
    set "MYSQL_PATH=C:\wamp64\bin\mysql\mysql8.0.39\bin\mysql.exe"
) else (
    echo ❌ ERROR: No se encontró MySQL en las rutas comunes.
    echo.
    echo Por favor, ejecuta el script manualmente en MySQL Workbench:
    echo 1. Abre MySQL Workbench
    echo 2. Conecta como root
    echo 3. File → Open SQL Script
    echo 4. Selecciona: mesa_partes_db_completa_con_bitacora_FINAL.sql
    echo 5. Ejecuta con Ctrl+Shift+Enter (todo el script)
    echo.
    pause
    exit /b 1
)

echo ✅ MySQL encontrado en: %MYSQL_PATH%
echo.

REM Solicitar contraseña
set /p MYSQL_PASSWORD="Ingresa la contraseña de root de MySQL: "
echo.

REM Ejecutar script SQL
echo 🔄 Ejecutando script SQL completo...
echo.

"%MYSQL_PATH%" -u root -p%MYSQL_PASSWORD% < mesa_partes_db_completa_con_bitacora_FINAL.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ✅ SCRIPT EJECUTADO EXITOSAMENTE
    echo ========================================
    echo.
    echo 📊 Verificando tabla bitácora...
    echo.
    
    "%MYSQL_PATH%" -u root -p%MYSQL_PASSWORD% -e "USE mesa_partes_db; SELECT COUNT(*) AS 'Total_Registros_Bitacora' FROM bitacora;"
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✅ Tabla bitácora creada correctamente con registros históricos
        echo.
        echo 🎉 ¡TODO LISTO! Ahora puedes:
        echo    1. Recargar la página de bitácora en el navegador (F5)
        echo    2. Deberías ver 10 registros de ENTRADA
        echo    3. El error 404 desaparecerá
        echo.
    ) else (
        echo ❌ Error al verificar la tabla bitácora
    )
) else (
    echo.
    echo ========================================
    echo ❌ ERROR AL EJECUTAR EL SCRIPT
    echo ========================================
    echo.
    echo Posibles causas:
    echo 1. Contraseña incorrecta
    echo 2. MySQL no está corriendo
    echo 3. Error de sintaxis en el script
    echo.
    echo 💡 SOLUCIÓN: Ejecuta manualmente en MySQL Workbench:
    echo    1. Abre MySQL Workbench
    echo    2. Conecta como root con tu contraseña
    echo    3. File → Open SQL Script
    echo    4. Selecciona: %~dp0mesa_partes_db_completa_con_bitacora_FINAL.sql
    echo    5. Presiona el rayo ⚡ para ejecutar TODO el script
    echo    6. Espera los mensajes de confirmación
    echo.
)

pause
