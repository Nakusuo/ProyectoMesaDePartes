# Script para eliminar @CrossOrigin de todos los controladores
# Ejecutar desde la raíz del proyecto

$controllerPath = "backend\src\main\java\com\pnp\mesadepartes\controller"
$controllers = Get-ChildItem -Path $controllerPath -Filter "*Controller.java"

foreach ($controller in $controllers) {
    $filePath = $controller.FullName
    $content = Get-Content $filePath -Raw
    
    # Eliminar @CrossOrigin(origins = "*", maxAge = 3600)
    $content = $content -replace '@CrossOrigin\(origins\s*=\s*"\*",\s*maxAge\s*=\s*\d+\)\r?\n', ''
    
    # Eliminar @CrossOrigin(origins = "*")
    $content = $content -replace '@CrossOrigin\(origins\s*=\s*"\*"\)\r?\n', ''
    
    # Eliminar import de CrossOrigin si no se usa
    if ($content -notmatch '@CrossOrigin') {
        $content = $content -replace 'import org\.springframework\.web\.bind\.annotation\.CrossOrigin;\r?\n', ''
    }
    
    Set-Content -Path $filePath -Value $content -NoNewline
    Write-Host "✅ Procesado: $($controller.Name)"
}

Write-Host "`n🎉 Limpieza completada. @CrossOrigin eliminado de todos los controladores."
