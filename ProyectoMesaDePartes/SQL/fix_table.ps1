# Script para arreglar la tabla documentos eliminando columnas antiguas

Write-Host "=== Arreglando tabla documentos ===" -ForegroundColor Cyan

# Comandos SQL para ejecutar
$sqlCommands = @"
USE mesa_partes_db;

-- Eliminar columna 'codigo' si existe
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = 'mesa_partes_db'
   AND TABLE_NAME = 'documentos'
   AND COLUMN_NAME = 'codigo') > 0,
  'ALTER TABLE documentos DROP COLUMN codigo;',
  'SELECT ''Columna codigo no existe'' AS resultado;'));
PREPARE alterStatement FROM @preparedStatement;
EXECUTE alterStatement;
DEALLOCATE PREPARE alterStatement;

-- Eliminar columna 'titulo' si existe
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = 'mesa_partes_db'
   AND TABLE_NAME = 'documentos'
   AND COLUMN_NAME = 'titulo') > 0,
  'ALTER TABLE documentos DROP COLUMN titulo;',
  'SELECT ''Columna titulo no existe'' AS resultado;'));
PREPARE alterStatement FROM @preparedStatement;
EXECUTE alterStatement;
DEALLOCATE PREPARE alterStatement;

-- Eliminar columna 'descripcion' si existe
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = 'mesa_partes_db'
   AND TABLE_NAME = 'documentos'
   AND COLUMN_NAME = 'descripcion') > 0,
  'ALTER TABLE documentos DROP COLUMN descripcion;',
  'SELECT ''Columna descripcion no existe'' AS resultado;'));
PREPARE alterStatement FROM @preparedStatement;
EXECUTE alterStatement;
DEALLOCATE PREPARE alterStatement;

-- Verificar estructura final
DESCRIBE documentos;

SELECT '✅ Tabla actualizada correctamente!' AS mensaje;
"@

# Guardar comandos en archivo temporal
$tempFile = [System.IO.Path]::GetTempFileName() + ".sql"
$sqlCommands | Out-File -FilePath $tempFile -Encoding UTF8

Write-Host "Ejecutando comandos SQL..." -ForegroundColor Yellow

# Buscar MySQL
$mysqlPaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe",
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\wamp64\bin\mysql\mysql8.0.31\bin\mysql.exe",
    "mysql.exe"  # Si está en PATH
)

$mysqlExe = $null
foreach ($path in $mysqlPaths) {
    if (Test-Path $path -ErrorAction SilentlyContinue) {
        $mysqlExe = $path
        break
    }
}

if (-not $mysqlExe) {
    # Intentar encontrar usando Get-Command
    try {
        $mysqlExe = (Get-Command mysql.exe -ErrorAction Stop).Source
    } catch {
        Write-Host "❌ No se encontró MySQL. Por favor, ejecuta manualmente:" -ForegroundColor Red
        Write-Host "   mysql -u root mesa_partes_db < $tempFile" -ForegroundColor Yellow
        exit 1
    }
}

Write-Host "Usando MySQL en: $mysqlExe" -ForegroundColor Green

# Ejecutar comandos
Get-Content $tempFile | & $mysqlExe -u root mesa_partes_db

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Tabla actualizada exitosamente!" -ForegroundColor Green
} else {
    Write-Host "❌ Error al ejecutar SQL (código: $LASTEXITCODE)" -ForegroundColor Red
}

# Limpiar archivo temporal
Remove-Item $tempFile -ErrorAction SilentlyContinue

Write-Host "`nPresiona cualquier tecla para continuar..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
