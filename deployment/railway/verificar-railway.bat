@echo off
REM ===================================================
REM SCRIPT DE VERIFICACIÓN LOCAL - Railway Deploy
REM ===================================================

echo.
echo ========================================
echo   VERIFICACION PROYECTO RAILWAY
echo ========================================
echo.

REM 1. Verificar archivos críticos
echo [1/6] Verificando archivos criticos...
if not exist "Dockerfile" (
    echo   [X] FALTA: Dockerfile
    goto :error
)
if not exist "railway.json" (
    echo   [X] FALTA: railway.json
    goto :error
)
if not exist "backend\src\main\resources\application-railway.properties" (
    echo   [X] FALTA: application-railway.properties
    goto :error
)
echo   [OK] Todos los archivos presentes
echo.

REM 2. Verificar pom.xml tiene Actuator
echo [2/6] Verificando dependencia Actuator en pom.xml...
findstr /C:"spring-boot-starter-actuator" backend\pom.xml >nul
if errorlevel 1 (
    echo   [X] FALTA: spring-boot-starter-actuator en pom.xml
    goto :error
)
echo   [OK] Actuator presente en pom.xml
echo.

REM 3. Verificar SecurityConfig permite /actuator/**
echo [3/6] Verificando SecurityConfig permite actuator...
findstr /C:"actuator" backend\src\main\java\com\pnp\mesadepartes\config\SecurityConfig.java >nul
if errorlevel 1 (
    echo   [!] ADVERTENCIA: No se encontro configuracion de actuator en SecurityConfig
)
echo   [OK] SecurityConfig revisado
echo.

REM 4. Mostrar configuración de healthcheck
echo [4/6] Configuracion de healthcheck en railway.json:
findstr /C:"healthcheckPath" railway.json
findstr /C:"healthcheckTimeout" railway.json
echo.

REM 5. Verificar archivos Java corregidos
echo [5/6] Verificando archivos Java corregidos...
if not exist "backend\src\main\java\com\pnp\mesadepartes\controller\DocumentoController.java" (
    echo   [X] FALTA: DocumentoController.java
    goto :error
)
if not exist "backend\src\main\java\com\pnp\mesadepartes\controller\ReporteController.java" (
    echo   [X] FALTA: ReporteController.java
    goto :error
)
if not exist "backend\src\main\java\com\pnp\mesadepartes\service\ReporteService.java" (
    echo   [X] FALTA: ReporteService.java
    goto :error
)
echo   [OK] Archivos Java presentes
echo.

REM 6. Checklist final
echo [6/6] CHECKLIST RAILWAY:
echo.
echo   [ ] MySQL servicio agregado en Railway
echo   [ ] JWT_SECRET configurado en Variables
echo   [ ] Variables MySQL vinculadas (${{MySQL.*}})
echo   [ ] Archivos subidos a GitHub
echo   [ ] Deploy iniciado en Railway
echo.

echo ========================================
echo   VERIFICACION COMPLETADA
echo ========================================
echo.
echo PROXIMOS PASOS:
echo   1. Sube los archivos a GitHub
echo   2. Configura JWT_SECRET en Railway Variables
echo   3. Agrega servicio MySQL en Railway
echo   4. Espera el deploy (10 minutos max)
echo   5. Revisa logs en Railway
echo.
pause
exit /b 0

:error
echo.
echo ========================================
echo   ERROR EN VERIFICACION
echo ========================================
echo.
echo Hay archivos faltantes. Revisa los errores arriba.
echo.
pause
exit /b 1
