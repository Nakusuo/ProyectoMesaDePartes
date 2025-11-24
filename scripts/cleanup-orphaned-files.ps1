# ============================================
# Script de Limpieza de Archivos Huérfanos
# Sistema Mesa de Partes PNP
# Versión: 1.0
# ============================================
#
# FUNCIÓN: Detecta y elimina archivos PDF en /uploads
#          que no están referenciados en la base de datos
#
# USO:
#   .\cleanup-orphaned-files.ps1
#   .\cleanup-orphaned-files.ps1 -DryRun  (solo simular)
#
# PROGRAMAR: Task Scheduler - Ejecutar semanalmente
# ============================================

param(
    [switch]$DryRun = $false  # -DryRun no elimina, solo muestra
)

# Configuración
$uploadsPath = "..\backend\uploads\documentos"
$dbHost = "localhost"
$dbPort = "3306"
$dbName = "mesa_partes_db"
$dbUser = "root"
$dbPassword = "root"  # Cambiar por variable de entorno

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "🧹 LIMPIEZA DE ARCHIVOS HUÉRFANOS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

if ($DryRun) {
    Write-Host "⚠️  MODO SIMULACIÓN (DRY RUN)" -ForegroundColor Yellow
    Write-Host "   Los archivos NO serán eliminados" -ForegroundColor Yellow
    Write-Host ""
}

# Verificar que existe el directorio
if (-not (Test-Path $uploadsPath)) {
    Write-Host "❌ Error: No se encontró el directorio $uploadsPath" -ForegroundColor Red
    exit 1
}

# Obtener todos los archivos PDF
Write-Host "📂 Escaneando directorio: $uploadsPath" -ForegroundColor Green
$allFiles = Get-ChildItem -Path $uploadsPath -Filter "*.pdf" -File

if ($allFiles.Count -eq 0) {
    Write-Host "ℹ️  No hay archivos PDF en el directorio" -ForegroundColor Gray
    exit 0
}

Write-Host "📊 Total de archivos encontrados: $($allFiles.Count)" -ForegroundColor Green
Write-Host ""

# Conectar a MySQL y obtener archivos referenciados
Write-Host "🔍 Conectando a la base de datos..." -ForegroundColor Yellow

try {
    # Construir query MySQL
    $query = @"
SELECT DISTINCT 
    SUBSTRING_INDEX(archivo_url, '/', -1) AS filename
FROM documentos 
WHERE archivo_url IS NOT NULL
UNION
SELECT DISTINCT 
    SUBSTRING_INDEX(archivo_cargo_url, '/', -1) AS filename
FROM salidas_documento 
WHERE archivo_cargo_url IS NOT NULL;
"@

    # Ejecutar query (requiere MySQL CLI)
    $mysqlCmd = "mysql -h $dbHost -P $dbPort -u $dbUser -p$dbPassword -D $dbName -N -e `"$query`""
    $referencedFiles = & mysql -h $dbHost -P $dbPort -u $dbUser "-p$dbPassword" -D $dbName -N -e $query 2>$null
    
    if ($LASTEXITCODE -ne 0) {
        throw "Error al conectar con MySQL"
    }
    
    Write-Host "✅ Conexión exitosa" -ForegroundColor Green
    Write-Host "📊 Archivos referenciados en BD: $($referencedFiles.Count)" -ForegroundColor Green
    Write-Host ""
    
}
catch {
    Write-Host "❌ Error al consultar base de datos: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "⚠️  Asegúrate de que:" -ForegroundColor Yellow
    Write-Host "   1. MySQL está corriendo" -ForegroundColor Yellow
    Write-Host "   2. Las credenciales son correctas" -ForegroundColor Yellow
    Write-Host "   3. MySQL CLI está en el PATH" -ForegroundColor Yellow
    exit 1
}

# Comparar archivos
$orphanedFiles = @()
$filesKept = 0

foreach ($file in $allFiles) {
    $filename = $file.Name
    
    if ($referencedFiles -contains $filename) {
        # Archivo está referenciado
        $filesKept++
    }
    else {
        # Archivo huérfano
        $orphanedFiles += $file
    }
}

# Mostrar resultados
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "📊 RESULTADOS DEL ANÁLISIS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Archivos con referencia: $filesKept" -ForegroundColor Green
Write-Host "🔴 Archivos HUÉRFANOS: $($orphanedFiles.Count)" -ForegroundColor Red
Write-Host ""

if ($orphanedFiles.Count -eq 0) {
    Write-Host "🎉 ¡No hay archivos huérfanos! Todo está limpio." -ForegroundColor Green
    exit 0
}

# Listar archivos huérfanos
Write-Host "📋 Lista de archivos huérfanos:" -ForegroundColor Yellow
Write-Host ""

$totalSize = 0
foreach ($file in $orphanedFiles) {
    $sizeKB = [math]::Round($file.Length / 1KB, 2)
    $sizeMB = [math]::Round($file.Length / 1MB, 2)
    $totalSize += $file.Length
    
    if ($sizeMB -gt 1) {
        Write-Host "   • $($file.Name) - $sizeMB MB" -ForegroundColor Gray
    }
    else {
        Write-Host "   • $($file.Name) - $sizeKB KB" -ForegroundColor Gray
    }
}

$totalSizeMB = [math]::Round($totalSize / 1MB, 2)
Write-Host ""
Write-Host "💾 Espacio a liberar: $totalSizeMB MB" -ForegroundColor Magenta
Write-Host ""

# Eliminar archivos huérfanos
if ($DryRun) {
    Write-Host "⚠️  SIMULACIÓN: Los archivos NO fueron eliminados" -ForegroundColor Yellow
    Write-Host "   Ejecuta sin -DryRun para eliminar realmente" -ForegroundColor Yellow
}
else {
    # Confirmar eliminación
    Write-Host "⚠️  ¿Deseas eliminar estos $($orphanedFiles.Count) archivos? (S/N): " -ForegroundColor Yellow -NoNewline
    $confirmation = Read-Host
    
    if ($confirmation -eq 'S' -or $confirmation -eq 's') {
        Write-Host ""
        Write-Host "🗑️  Eliminando archivos..." -ForegroundColor Red
        
        $deletedCount = 0
        $errorCount = 0
        
        foreach ($file in $orphanedFiles) {
            try {
                Remove-Item $file.FullName -Force
                $deletedCount++
                Write-Host "   ✓ Eliminado: $($file.Name)" -ForegroundColor Gray
            }
            catch {
                $errorCount++
                Write-Host "   ✗ Error al eliminar: $($file.Name) - $_" -ForegroundColor Red
            }
        }
        
        Write-Host ""
        Write-Host "================================================" -ForegroundColor Green
        Write-Host "✅ LIMPIEZA COMPLETADA" -ForegroundColor Green
        Write-Host "================================================" -ForegroundColor Green
        Write-Host "   Archivos eliminados: $deletedCount" -ForegroundColor Green
        Write-Host "   Errores: $errorCount" -ForegroundColor $(if ($errorCount -gt 0) { "Red" } else { "Green" })
        Write-Host "   Espacio liberado: $totalSizeMB MB" -ForegroundColor Green
        Write-Host ""
    }
    else {
        Write-Host ""
        Write-Host "❌ Operación cancelada por el usuario" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "📅 Próxima ejecución: Programa este script semanalmente" -ForegroundColor Cyan
Write-Host "   con Task Scheduler para mantenimiento automático" -ForegroundColor Cyan
Write-Host ""
