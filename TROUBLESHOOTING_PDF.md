# 🔧 Troubleshooting: Botón PDF no funciona

## ✅ Cambios Aplicados

Se han corregido los siguientes archivos:

### 1. **bitacora.html** - Orden de scripts corregido
Ahora carga los scripts en el orden correcto:
```html
<script src="../../assets/js/core/config.js"></script>
<script src="../../assets/js/components/toast.js"></script>  <!-- NUEVO -->
<script src="../../assets/js/core/permissions.js"></script>
<script src="../../assets/js/components/sidebar.js"></script>
<script src="../../assets/js/core/auth.js"></script>
<script src="../../assets/js/modules/reportes-global.js"></script>
<script src="../../assets/js/pages/admin/bitacora.js"></script>
```

### 2. **bitacora.html** - CSS de toast agregado
```html
<link rel="stylesheet" href="../../assets/css/core/toast.css">
```

---

## 🧪 Cómo Verificar el Problema

### Método 1: Página de Test (Sin autenticación)

1. **Abre esta URL:**
   ```
   http://localhost:8080/test-boton-pdf.html
   ```

2. **Presiona F12** para abrir las Herramientas de Desarrollador

3. **Haz clic en "🔍 Verificar Dependencias"**
   - Deberías ver ✅ en todas las dependencias
   - Si ves ❌ en alguna, ese es el problema

4. **Haz clic en "📥 Descarga Directa (Test)"**
   - Si funciona: el problema está en autenticación/permisos
   - Si no funciona: el problema está en el backend o CORS

### Método 2: Página Real de Bitácora

1. **Cierra el navegador completamente** (todas las ventanas)

2. **Abre de nuevo:**
   ```
   http://localhost:8080/pages/auth/login.html
   ```

3. **Inicia sesión**

4. **Ve a Bitácora:**
   ```
   http://localhost:8080/pages/admin/bitacora.html
   ```

5. **Presiona F12** y ve a la pestaña "Console"

6. **Haz clic en el botón "📥 Exportar PDF"**

7. **Revisa la consola:**
   - ¿Hay errores en rojo?
   - ¿Qué dice el error?

---

## 🔍 Errores Comunes y Soluciones

### Error 1: `ReferenceError: showToast is not defined`
**Causa:** El archivo `toast.js` no se está cargando.

**Solución:**
1. Verifica que existe: `frontend/assets/js/components/toast.js`
2. Limpia la caché del navegador: `Ctrl + Shift + Delete`
3. Recarga con `Ctrl + F5` (recarga forzada)

### Error 2: `ReferenceError: API_URL is not defined`
**Causa:** La constante no está definida en `reportes-global.js`

**Solución:** Ya está corregido en el archivo, pero verifica:
1. Abre: `frontend/assets/js/modules/reportes-global.js`
2. La línea 6 debe tener: `const API_URL = window.API_URL || 'http://localhost:8080/api';`

### Error 3: `Failed to fetch` o `NetworkError`
**Causa:** El backend no está corriendo o hay un problema de CORS.

**Solución:**
1. Verifica que el backend esté corriendo:
   ```powershell
   curl http://localhost:8080/api/tipos-documento
   ```
   Debería responder con JSON.

2. Si no responde, reinicia el backend:
   ```powershell
   cd C:\Users\HP\Desktop\ProyectoMesaDePartes\ProyectoMesaDePartes\backend
   .\mvnw.cmd spring-boot:run
   ```

### Error 4: `401 Unauthorized`
**Causa:** El token JWT expiró o no es válido.

**Solución:**
1. Cierra sesión
2. Vuelve a iniciar sesión
3. Intenta de nuevo

### Error 5: El botón no hace nada (sin errores)
**Causa:** El evento `onclick` no está vinculado correctamente.

**Solución:**
1. Abre la consola (F12)
2. Escribe: `typeof generarReportePDF`
3. Debe responder: `"function"`
4. Si dice `"undefined"`, el archivo `reportes-global.js` no se cargó

---

## 📋 Checklist de Verificación

Marca lo que ya verificaste:

- [ ] Backend está corriendo (`http://localhost:8080` responde)
- [ ] Archivo `toast.js` existe en `frontend/assets/js/components/`
- [ ] Archivo `toast.css` existe en `frontend/assets/css/core/`
- [ ] Caché del navegador limpiada (`Ctrl + Shift + Delete`)
- [ ] Recarga forzada (`Ctrl + F5`)
- [ ] Consola sin errores rojos (F12 → Console)
- [ ] Sesión iniciada correctamente
- [ ] Token válido en localStorage

---

## 🔬 Debug Manual en la Consola

Abre la consola del navegador (F12) y ejecuta estos comandos uno por uno:

```javascript
// 1. Verificar que showToast existe
console.log('showToast:', typeof showToast);
// Debe mostrar: "function"

// 2. Verificar que API_URL existe
console.log('API_URL:', typeof API_URL !== 'undefined' ? API_URL : 'NO DEFINIDA');
// Debe mostrar: "http://localhost:8080/api"

// 3. Verificar que generarReportePDF existe
console.log('generarReportePDF:', typeof generarReportePDF);
// Debe mostrar: "function"

// 4. Verificar token
console.log('Token:', localStorage.getItem('token') ? 'EXISTE' : 'NO EXISTE');
// Debe mostrar: "EXISTE"

// 5. Probar showToast manualmente
showToast('Prueba de notificación', 'info');
// Debe aparecer una notificación en pantalla

// 6. Probar descarga manual
generarReportePDF();
// Debe iniciar la descarga del PDF
```

Si alguno de estos comandos falla, ese es el problema específico.

---

## 🎯 Test Rápido de 30 Segundos

**Ejecuta esto en PowerShell:**

```powershell
# Verificar backend
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/reportes/pdf" -UseBasicParsing
Write-Host "Backend responde: $($response.StatusCode)"

# Abrir test
Start-Process "http://localhost:8080/test-boton-pdf.html"
```

Si el backend responde `200`, el problema está en el frontend.
Si responde error, el problema está en el backend.

---

## 📞 Si Aún No Funciona

Envíame lo siguiente:

1. **Captura de la consola (F12 → Console)** cuando haces clic en el botón
2. **Captura de la pestaña Network (F12 → Network)** filtrando por "pdf"
3. **Resultado de estos comandos en la consola:**
   ```javascript
   console.log({
       showToast: typeof showToast,
       API_URL: typeof API_URL !== 'undefined' ? API_URL : 'undefined',
       generarReportePDF: typeof generarReportePDF,
       token: !!localStorage.getItem('token')
   });
   ```

Con esa información podré identificar exactamente qué está fallando.

---

*Última actualización: 11 de noviembre de 2025*
