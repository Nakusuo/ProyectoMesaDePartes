# 🚀 INICIO RÁPIDO - Railway

## Si Git NO está instalado:

### Opción 1: Instalar Git (5 minutos)
1. Descarga: https://git-scm.com/download/win
2. Instala Git for Windows
3. Ejecuta: `preparar-railway.bat`

### Opción 2: Usar GitHub Desktop (Más fácil)
1. Descarga: https://desktop.github.com
2. Instala GitHub Desktop
3. Abre tu proyecto en GitHub Desktop
4. Click "Commit to main"
5. Click "Push origin"

### Opción 3: Subir manualmente (Sin Git)
1. Ve a: https://github.com/Nakusuo/ProyectoMesaDePartes
2. Click "Upload files"
3. Arrastra estos archivos nuevos:
   - `Dockerfile`
   - `.dockerignore`
   - `railway.json`
   - `.env.railway`
   - `RAILWAY_DEPLOY.md`
4. Commit changes

---

## Si Git YA está instalado:

### Ejecuta el script automático:
```cmd
preparar-railway.bat
```

O manualmente:
```cmd
git add .
git commit -m "Preparar para Railway"
git push origin main
```

---

## Después de subir a GitHub:

### 🚂 Railway Setup (10 minutos)

1. **Ir a Railway:**
   - https://railway.app
   - Login with GitHub

2. **Crear proyecto:**
   - "New Project"
   - "Deploy from GitHub repo"
   - Selecciona: `ProyectoMesaDePartes`

3. **Agregar MySQL:**
   - Click "+ New"
   - "Database" → "Add MySQL"

4. **Configurar variables:**
   - Click en tu servicio backend
   - Tab "Variables"
   - Click "Raw Editor"
   - Pega:
   ```env
   DB_HOST=${{MySQL.MYSQL_HOST}}
   DB_PORT=${{MySQL.MYSQL_PORT}}
   DB_NAME=${{MySQL.MYSQL_DATABASE}}
   DB_USERNAME=${{MySQL.MYSQL_USER}}
   DB_PASSWORD=${{MySQL.MYSQL_PASSWORD}}
   JWT_SECRET=<GENERA_UNO_NUEVO>
   ALLOWED_ORIGINS=https://${{RAILWAY_PUBLIC_DOMAIN}}
   ```

5. **Generar JWT_SECRET:**
   ```powershell
   [Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256}))
   ```
   Copia el resultado y reemplaza `<GENERA_UNO_NUEVO>`

6. **Esperar deploy:**
   - Railway hace el deploy automático
   - Toma 5-8 minutos la primera vez

7. **Importar base de datos:**
   - Ver `RAILWAY_DEPLOY.md` Paso 7

---

## 🆘 ¿Problemas?

- Ver guía completa: `RAILWAY_DEPLOY.md`
- Troubleshooting completo incluido
- O pregúntame directamente

---

## ✅ Checklist

- [ ] Git instalado (o usar alternativa)
- [ ] Archivos subidos a GitHub
- [ ] Cuenta Railway creada
- [ ] Proyecto creado en Railway
- [ ] MySQL agregado
- [ ] Variables configuradas
- [ ] Deploy exitoso
- [ ] Base de datos importada

---

¡Listo para deployar! 🎉
