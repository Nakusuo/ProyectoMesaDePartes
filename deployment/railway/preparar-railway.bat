@echo off
echo =====================================================
echo PREPARACION PARA RAILWAY - Mesa de Partes PNP
echo =====================================================
echo.

echo Verificando Git...
where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Git no esta instalado
    echo.
    echo Por favor instala Git:
    echo 1. Ve a: https://git-scm.com/download/win
    echo 2. Descarga e instala Git for Windows
    echo 3. Ejecuta este script nuevamente
    echo.
    pause
    exit /b 1
)

echo [OK] Git instalado
echo.

echo =====================================================
echo PASO 1: Verificar estado del repositorio
echo =====================================================
git status
echo.

echo =====================================================
echo PASO 2: Agregar todos los archivos nuevos
echo =====================================================
git add .
echo [OK] Archivos agregados
echo.

echo =====================================================
echo PASO 3: Crear commit
echo =====================================================
git commit -m "Preparar proyecto para Railway - Agregar Dockerfile y configuracion"
echo [OK] Commit creado
echo.

echo =====================================================
echo PASO 4: Subir a GitHub
echo =====================================================
git push origin main
echo.

if %ERRORLEVEL% EQU 0 (
    echo =====================================================
    echo [EXITO] Todo listo para Railway!
    echo =====================================================
    echo.
    echo Proximos pasos:
    echo 1. Ve a: https://railway.app
    echo 2. Login with GitHub
    echo 3. New Project - Deploy from GitHub repo
    echo 4. Selecciona: ProyectoMesaDePartes
    echo 5. Agrega MySQL: + New - Database - MySQL
    echo 6. Configura variables (ver RAILWAY_DEPLOY.md)
    echo.
    echo Guia completa: RAILWAY_DEPLOY.md
    echo.
) else (
    echo.
    echo [ERROR] Hubo un problema al subir a GitHub
    echo.
    echo Verifica:
    echo - Que tengas conexion a internet
    echo - Que hayas configurado tu repositorio remoto
    echo - Que tengas permisos en el repositorio
    echo.
)

pause
