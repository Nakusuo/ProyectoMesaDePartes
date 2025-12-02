# 📁 Scripts SQL - Mesa de Partes PNP

## 📋 Archivos Disponibles

### 1. `mesa_partes_db_completa_actualizada.sql`
**Base de datos completa desde cero**
- Crea toda la estructura de la base de datos
- Incluye datos de ejemplo
- Incluye todos los triggers actualizados
- **Usar para:** Instalación nueva del sistema

### 2. `EJECUTAR_ACTUALIZACION_BITACORA.sql` ⭐
**Script de actualización para sistemas existentes**
- Agrega columnas de HT a la bitácora
- Actualiza triggers para usar usuario asignado
- Actualiza datos existentes
- **Usar para:** Actualizar sistema que ya está funcionando

## 🚀 ¿Cuál ejecutar?

### Instalación Nueva
```sql
-- Ejecuta este archivo:
mesa_partes_db_completa_actualizada.sql
```

### Sistema Ya Instalado (Actualización) ⭐
```sql
-- Ejecuta este archivo:
EJECUTAR_ACTUALIZACION_BITACORA.sql
```

## ⚡ Cómo Ejecutar

### Opción 1: MySQL Workbench
1. Abre MySQL Workbench
2. Conecta a tu servidor
3. File → Open SQL Script
4. Selecciona el archivo que necesitas
5. Click en el icono ⚡ (Execute)

### Opción 2: Línea de Comandos
```bash
# Para instalación nueva
mysql -u root -p < mesa_partes_db_completa_actualizada.sql

# Para actualización
mysql -u root -p < EJECUTAR_ACTUALIZACION_BITACORA.sql
```

### Opción 3: phpMyAdmin
1. Entra a phpMyAdmin
2. Selecciona la base de datos `mesa_partes_db`
3. Click en la pestaña "SQL"
4. Copia y pega el contenido del archivo
5. Click en "Continuar"

## ✅ Cambios en la Actualización

La actualización `EJECUTAR_ACTUALIZACION_BITACORA.sql` incluye:

1. **Usuario Asignado en Bitácora**
   - Ahora muestra quién trabaja el documento (usuario asignado)
   - No el usuario que lo registró

2. **Números de HT**
   - Columna `numero_ht_entrada` - HT al entrar
   - Columna `numero_ht_salida` - HT al salir
   - Muestra "S/N" cuando no hay HT

3. **Triggers Actualizados**
   - `trg_bitacora_entrada_documento` - Captura HT de entrada
   - `trg_bitacora_salida_documento` - Captura HT de salida

## 🔍 Verificar Instalación

Después de ejecutar cualquier script:

```sql
-- Ver estructura de bitácora
DESCRIBE bitacora;

-- Ver últimos registros
SELECT * FROM bitacora ORDER BY fecha_entrada DESC LIMIT 5;

-- Verificar triggers
SHOW TRIGGERS LIKE 'bitacora';
```

## ⚠️ Importante

- **Haz backup** antes de ejecutar actualizaciones
- Los scripts son seguros y NO borran datos
- Solo agregan columnas y actualizan triggers
- Después de ejecutar, reinicia el backend si estaba corriendo
- Recarga el frontend con Ctrl+Shift+R

## 💡 Después de la Actualización

1. Reinicia el backend: `mvnw.cmd spring-boot:run`
2. Recarga la página de bitácora con `Ctrl+Shift+R`
3. Verifica que se muestren los HT y usuario asignado
4. El menú "Exportar" ahora debe funcionar correctamente

## 📞 Soporte

Si tienes problemas:
1. Revisa los errores en la consola SQL
2. Verifica que la base de datos `mesa_partes_db` existe
3. Asegúrate de tener permisos de administrador
4. Verifica que MySQL esté corriendo
