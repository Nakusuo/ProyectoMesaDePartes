# Script para probar la exportación PDF con caché limpia
# Ejecuta este script para probar la funcionalidad

Write-Host "🧹 Limpiando caché del navegador..." -ForegroundColor Cyan
Write-Host ""

# Cerrar todas las instancias de Chrome
Write-Host "📌 Cerrando Chrome..." -ForegroundColor Yellow
Get-Process chrome -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Limpiar caché de Chrome
Write-Host "🗑️ Limpiando caché de Chrome..." -ForegroundColor Yellow
$chromeCachePath = "$env:LOCALAPPDATA\Google\Chrome\User Data\Default\Cache"
if (Test-Path $chromeCachePath) {
    Remove-Item "$chromeCachePath\*" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✅ Caché de Chrome limpiada" -ForegroundColor Green
}

# Limpiar caché de Edge
Write-Host "🗑️ Limpiando caché de Edge..." -ForegroundColor Yellow
$edgeCachePath = "$env:LOCALAPPDATA\Microsoft\Edge\User Data\Default\Cache"
if (Test-Path $edgeCachePath) {
    Remove-Item "$edgeCachePath\*" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✅ Caché de Edge limpiada" -ForegroundColor Green
}

Start-Sleep -Seconds 2

Write-Host ""
Write-Host "✅ Caché limpiada exitosamente" -ForegroundColor Green
Write-Host ""

# Verificar backend
Write-Host "🔍 Verificando backend..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/tipos-documento" -UseBasicParsing -TimeoutSec 5
    Write-Host "   ✅ Backend funcionando (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Backend no responde. Asegúrate de iniciarlo primero:" -ForegroundColor Red
    Write-Host "      cd backend" -ForegroundColor Yellow
    Write-Host "      .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Presiona Enter para continuar de todos modos..."
}

Write-Host ""
Write-Host "🚀 Abriendo aplicación..." -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 PASOS PARA PROBAR:" -ForegroundColor Yellow
Write-Host "  1. Inicia sesión con: nakusu / 123456" -ForegroundColor White
Write-Host "  2. Ve a Administración > Bitácora" -ForegroundColor White
Write-Host "  3. Presiona F12 para abrir DevTools" -ForegroundColor White
Write-Host "  4. Haz clic en 'Exportar PDF'" -ForegroundColor White
Write-Host "  5. Deberías ver notificaciones toast y el PDF descargarse" -ForegroundColor White
Write-Host ""

# Abrir en modo incógnito para evitar caché
Start-Sleep -Seconds 1
Start-Process "chrome.exe" -ArgumentList "--incognito", "http://localhost:8080/pages/auth/login.html"

Write-Host "✨ ¡Listo! Navegador abierto en modo incógnito (sin caché)" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Si aún no funciona, copia los errores de la consola (F12)" -ForegroundColor Cyan
