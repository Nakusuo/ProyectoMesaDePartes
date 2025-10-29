# Solución: Campo `tipo` Faltante en Modelo Area

## Problema Detectado

Al implementar el filtrado de áreas por tipo (`DEPARTAMENTO_PNP` vs `AREA_TRABAJO`), los dropdowns no mostraban ninguna opción porque:

**❌ El modelo Java `Area.java` NO tenía el campo `tipo` definido**

Aunque la base de datos sí tenía la columna `tipo` con su ENUM:

```sql
CREATE TABLE areas (
    ...
    tipo ENUM('DEPARTAMENTO_PNP','AREA_TRABAJO') DEFAULT 'DEPARTAMENTO_PNP'
);
```

El modelo JPA no estaba mapeando esta columna, por lo que cuando el endpoint `/api/areas` devolvía los datos JSON, **el campo `tipo` no estaba presente**.

## Solución Implementada

Se modificó el modelo `Area.java` para incluir el campo `tipo` con su correspondiente ENUM:

### Archivo: `backend/src/main/java/com/pnp/mesadepartes/model/Area.java`

```java
package com.pnp.mesadepartes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_area")
    private Long idArea;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(unique = true, length = 20)
    private String sigla;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArea tipo = TipoArea.DEPARTAMENTO_PNP;
    
    public enum TipoArea {
        DEPARTAMENTO_PNP,  // Áreas oficiales de la PNP (para documentos)
        AREA_TRABAJO       // Áreas de trabajo del sistema (para usuarios)
    }
}
```

### Cambios Realizados:

1. ✅ Agregado campo `tipo` con anotación `@Enumerated(EnumType.STRING)`
2. ✅ Creado ENUM interno `TipoArea` con dos valores:
   - `DEPARTAMENTO_PNP`: Para departamentos oficiales de la PNP (usado en documentos)
   - `AREA_TRABAJO`: Para áreas de trabajo del sistema (usado para asignar usuarios)
3. ✅ Valor por defecto: `DEPARTAMENTO_PNP` (coincide con la base de datos)
4. ✅ Import agregado: `jakarta.persistence.EnumType` y `jakarta.persistence.Enumerated`

## Comportamiento Esperado

Ahora cuando el frontend llame a `/api/areas`, el JSON incluirá el campo `tipo`:

```json
[
  {
    "idArea": 1,
    "nombre": "Mesa de Partes",
    "sigla": "MDP",
    "tipo": "AREA_TRABAJO"
  },
  {
    "idArea": 6,
    "nombre": "Comandancia General de la PNP",
    "sigla": "COMGEN",
    "tipo": "DEPARTAMENTO_PNP"
  }
]
```

Y los filtros en JavaScript funcionarán correctamente:

```javascript
// ✅ En registrar-interno.js (documentos)
const departamentosPNP = areas.filter(area => area.tipo === 'DEPARTAMENTO_PNP');

// ✅ En registro.js y gestion-usuarios.js (usuarios)
const areasTrabajo = areas.filter(area => area.tipo === 'AREA_TRABAJO');
```

## Resultado

✅ **Dropdown de Documentos**: Muestra 34 departamentos PNP (DIRTIC, DIRANDRO, etc.)  
✅ **Dropdown de Usuarios**: Muestra 5 áreas de trabajo (MDP, SIS, DEV, RED, ST)  
✅ **Separación Clara**: Departamentos PNP y áreas de trabajo están correctamente separados

## Fecha de Corrección

29 de octubre de 2025 - 13:49 hrs
