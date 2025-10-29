# Filtrado de Áreas por Tipo

## Problema Identificado

Las **áreas de trabajo** del sistema (DEV, SIS, RED, ST, MDP) estaban apareciendo en el formulario de registro de documentos en el campo "Área Remitente", cuando solo deberían mostrarse los **departamentos PNP** (DIRTIC, DIRANDRO, IGPNP, etc.).

## Solución Implementada

Se implementó un filtrado basado en el campo `tipo` de la tabla `areas`:

### Tipos de Áreas en la Base de Datos

```sql
areas.tipo ENUM('DEPARTAMENTO_PNP', 'AREA_TRABAJO')
```

- **AREA_TRABAJO**: Áreas internas del sistema para asignar usuarios (5 áreas)
  - MDP - Mesa de Partes
  - SIS - Sistemas
  - DEV - Desarrollo
  - RED - Redes
  - ST - Soporte Técnico

- **DEPARTAMENTO_PNP**: Departamentos de la Policía Nacional (34 departamentos)
  - DIRTIC, DIRANDRO, IGPNP, DIRAVPOL, etc.

## Archivos Modificados

### 1. `frontend/assets/js/registrar-interno.js` (Registro de Documentos)

**Cambio**: Filtrar solo DEPARTAMENTO_PNP para el dropdown de "Área Remitente"

```javascript
async function cargarAreas() {
  try {
    const response = await fetch(`${API_URL}/areas`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar áreas');
    
    const areas = await response.json();
    // ✅ NUEVO: Filtrar solo departamentos PNP
    const departamentosPNP = areas.filter(area => area.tipo === 'DEPARTAMENTO_PNP');
    
    remitenteSelect.innerHTML = '<option value="">Seleccione un área</option>';
    departamentosPNP.forEach(area => {
      const textoCompleto = area.sigla ? `${area.sigla} - ${area.nombre}` : area.nombre;
      remitenteSelect.innerHTML += `<option value="${textoCompleto}">${textoCompleto}</option>`;
    });
  } catch (error) {
    console.error(error);
    remitenteSelect.innerHTML = '<option value="">Error al cargar áreas</option>';
  }
}
```

**Resultado**: El dropdown "Área Remitente" ahora muestra solo DIRTIC, DIRANDRO, IGPNP, etc.

---

### 2. `frontend/assets/js/registro.js` (Registro de Usuarios)

**Cambio**: Filtrar solo AREA_TRABAJO para el dropdown de área del usuario

```javascript
async function cargarAreas() {
  try {
    const response = await fetch(`${API_URL}/areas`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar áreas');
    
    const areas = await response.json();
    // ✅ NUEVO: Filtrar solo áreas de trabajo
    const areasTrabajo = areas.filter(area => area.tipo === 'AREA_TRABAJO');
    
    areaSelect.innerHTML = '<option value="">-- Opcional --</option>';
    areasTrabajo.forEach(area => {
      areaSelect.innerHTML += `<option value="${area.idArea}">${area.nombre} (${area.sigla})</option>`;
    });
  } catch (error) {
    console.error(error);
    areaSelect.innerHTML = '<option value="">Error al cargar áreas</option>';
  }
}
```

**Resultado**: El dropdown de área de usuario ahora muestra solo MDP, SIS, DEV, RED, ST

---

### 3. `frontend/assets/js/gestion-usuarios.js` (Gestión de Usuarios)

**Cambio**: Filtrar solo AREA_TRABAJO para el dropdown de área en el modal de edición

```javascript
// Carga las Áreas en el <select> del modal (solo áreas de trabajo)
async function cargarAreas() {
  try {
    const response = await fetch(`${API_URL}/areas`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Error al cargar áreas');
    
    const areas = await response.json();
    // ✅ NUEVO: Filtrar solo áreas de trabajo para asignación de usuarios
    const areasTrabajo = areas.filter(area => area.tipo === 'AREA_TRABAJO');
    
    areaSelect.innerHTML = '<option value="">Seleccione un área</option>';
    areasTrabajo.forEach(area => {
      areaSelect.innerHTML += `<option value="${area.idArea}">${area.nombre} (${area.sigla})</option>`;
    });
  } catch (error) {
    console.error(error);
    areaSelect.innerHTML = '<option value="">Error al cargar áreas</option>';
  }
}
```

**Resultado**: El modal de edición de usuario ahora muestra solo MDP, SIS, DEV, RED, ST

## Resumen de Cambios

| Archivo | Función | Filtro Aplicado | Elementos Mostrados |
|---------|---------|-----------------|---------------------|
| `registrar-interno.js` | Registro de documentos | `DEPARTAMENTO_PNP` | 34 departamentos PNP |
| `registro.js` | Registro de usuarios | `AREA_TRABAJO` | 5 áreas de trabajo |
| `gestion-usuarios.js` | Gestión de usuarios | `AREA_TRABAJO` | 5 áreas de trabajo |

## Validación

✅ **Registro de Documentos**: Solo muestra departamentos PNP (DIRTIC, DIRANDRO, etc.)  
✅ **Registro de Usuarios**: Solo muestra áreas de trabajo (MDP, SIS, DEV, RED, ST)  
✅ **Gestión de Usuarios**: Solo muestra áreas de trabajo (MDP, SIS, DEV, RED, ST)  

## Fecha de Implementación

29 de octubre de 2025 - 13:40 hrs
