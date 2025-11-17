@echo off
echo =========================================
echo    INICIANDO BACKEND - Mesa de Partes
echo =========================================
echo.
cd /d "%~dp0backend"
echo Directorio actual: %CD%
echo.
echo Iniciando Spring Boot...
echo.
call mvnw.cmd spring-boot:run
