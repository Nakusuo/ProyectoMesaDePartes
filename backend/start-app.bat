@echo off
cd /d %~dp0
echo ====================================
echo Iniciando aplicación en modo LOCAL
echo ====================================
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
