# Script para actualizar todas las rutas en los archivos HTML despues de la reorganizacion

$frontendPath = "C:\Users\User\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\ProyectoMesaDePartes\frontend"

Write-Host "Iniciando actualizacion de rutas en archivos HTML..." -ForegroundColor Green

# Funcion para actualizar rutas en un archivo
function Update-HtmlPaths {
    param (
        [string]$filePath,
        [hashtable]$replacements
    )
    
    if (Test-Path $filePath) {
        $content = Get-Content $filePath -Raw -Encoding UTF8
        $modified = $false
        
        foreach ($key in $replacements.Keys) {
            if ($content -match [regex]::Escape($key)) {
                $content = $content -replace [regex]::Escape($key), $replacements[$key]
                $modified = $true
            }
        }
        
        if ($modified) {
            Set-Content $filePath -Value $content -Encoding UTF8 -NoNewline
            Write-Host "  [OK] Actualizado: $filePath" -ForegroundColor Cyan
            return $true
        }
    }
    return $false
}

# Actualizar bitacora.html (admin)
$bitacoraPath = Join-Path $frontendPath "pages\admin\bitacora.html"
$bitacoraReplacements = @{
    'href="assets/css/style.css"' = 'href="../../assets/css/core/style.css"'
    'href="assets/css/sidebar.css"' = 'href="../../assets/css/components/sidebar.css"'
    'href="assets/css/bitacora.css"' = 'href="../../assets/css/pages/admin/bitacora.css"'
    'src="assets/js/permissions.js"' = 'src="../../assets/js/core/permissions.js"'
    'src="assets/js/sidebar.js"' = 'src="../../assets/js/components/sidebar.js"'
    'src="assets/js/auth.js"' = 'src="../../assets/js/core/auth.js"'
    'src="assets/js/bitacora.js"' = 'src="../../assets/js/pages/admin/bitacora.js"'
}
Update-HtmlPaths -filePath $bitacoraPath -replacements $bitacoraReplacements

# Actualizar documentos.html (documents)
$documentosPath = Join-Path $frontendPath "pages\documents\documentos.html"
$documentosReplacements = @{
    'href="assets/css/style.css"' = 'href="../../assets/css/core/style.css"'
    'href="assets/css/sidebar.css"' = 'href="../../assets/css/components/sidebar.css"'
    'href="assets/css/dashboard.css"' = 'href="../../assets/css/pages/dashboard.css"'
    'src="assets/js/permissions.js"' = 'src="../../assets/js/core/permissions.js"'
    'src="assets/js/sidebar.js"' = 'src="../../assets/js/components/sidebar.js"'
    'src="assets/js/auth.js"' = 'src="../../assets/js/core/auth.js"'
    'src="assets/js/documentos.js"' = 'src="../../assets/js/pages/documents/documentos.js"'
}
Update-HtmlPaths -filePath $documentosPath -replacements $documentosReplacements

# Actualizar registro-usuario.html (documents)
$registroUsuarioPath = Join-Path $frontendPath "pages\documents\registro-usuario.html"
$registroUsuarioReplacements = @{
    'href="assets/css/style.css"' = 'href="../../assets/css/core/style.css"'
    'href="assets/css/login.css"' = 'href="../../assets/css/pages/auth/login.css"'
    'src="assets/js/registro.js"' = 'src="../../assets/js/pages/auth/registro.js"'
}
Update-HtmlPaths -filePath $registroUsuarioPath -replacements $registroUsuarioReplacements

# Actualizar salida-documento.html (documents)
$salidaDocumentoPath = Join-Path $frontendPath "pages\documents\salida-documento.html"
$salidaDocumentoReplacements = @{
    'href="assets/css/style.css"' = 'href="../../assets/css/core/style.css"'
    'href="assets/css/sidebar.css"' = 'href="../../assets/css/components/sidebar.css"'
    'href="assets/css/registro.css"' = 'href="../../assets/css/pages/auth/registro.css"'
    'href="assets/css/salida-documento.css"' = 'href="../../assets/css/pages/documents/salida-documento.css"'
    'href="assets/css/toast.css"' = 'href="../../assets/css/core/toast.css"'
    'src="assets/js/config.js"' = 'src="../../assets/js/core/config.js"'
    'src="assets/js/permissions.js"' = 'src="../../assets/js/core/permissions.js"'
    'src="assets/js/toast.js"' = 'src="../../assets/js/components/toast.js"'
    'src="assets/js/auth.js"' = 'src="../../assets/js/core/auth.js"'
    'src="assets/js/sidebar.js"' = 'src="../../assets/js/components/sidebar.js"'
}
Update-HtmlPaths -filePath $salidaDocumentoPath -replacements $salidaDocumentoReplacements

# Actualizar dashboard.html (common)
$dashboardPath = Join-Path $frontendPath "pages\common\dashboard.html"
$dashboardReplacements = @{
    'href="assets/css/style.css"' = 'href="../../assets/css/core/style.css"'
    'href="assets/css/sidebar.css"' = 'href="../../assets/css/components/sidebar.css"'
    'href="assets/css/dashboard.css"' = 'href="../../assets/css/pages/dashboard.css"'
    'src="assets/js/permissions.js"' = 'src="../../assets/js/core/permissions.js"'
    'src="assets/js/sidebar.js"' = 'src="../../assets/js/components/sidebar.js"'
    'src="assets/js/auth.js"' = 'src="../../assets/js/core/auth.js"'
    'src="assets/js/dashboard.js"' = 'src="../../assets/js/pages/dashboard.js"'
}
Update-HtmlPaths -filePath $dashboardPath -replacements $dashboardReplacements

# Actualizar index.html (common)
$indexPath = Join-Path $frontendPath "pages\common\index.html"
$indexReplacements = @{
    'href="assets/css/style.css"' = 'href="../../assets/css/core/style.css"'
    'href="assets/css/sidebar.css"' = 'href="../../assets/css/components/sidebar.css"'
    'href="assets/css/dashboard.css"' = 'href="../../assets/css/pages/dashboard.css"'
    'src="assets/js/permissions.js"' = 'src="../../assets/js/core/permissions.js"'
    'src="assets/js/sidebar.js"' = 'src="../../assets/js/components/sidebar.js"'
    'src="assets/js/auth.js"' = 'src="../../assets/js/core/auth.js"'
}
Update-HtmlPaths -filePath $indexPath -replacements $indexReplacements

# Actualizar sidebar.html (common)
$sidebarPath = Join-Path $frontendPath "pages\common\sidebar.html"
$sidebarReplacements = @{
    'href="assets/css/sidebar.css"' = 'href="../../assets/css/components/sidebar.css"'
    'src="assets/js/sidebar.js"' = 'src="../../assets/js/components/sidebar.js"'
}
Update-HtmlPaths -filePath $sidebarPath -replacements $sidebarReplacements

Write-Host ""
Write-Host "Actualizacion de rutas completada!" -ForegroundColor Green
Write-Host "Verifica que los archivos se carguen correctamente en el navegador." -ForegroundColor Yellow
