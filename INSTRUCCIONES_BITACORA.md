# 🔧 Instrucciones para Completar la Funcionalidad de Bitácora

## 📋 **Estado Actual:**

✅ **Frontend completado:**
- Menú desplegable con opciones PDF y Excel funcionando
- Funciones `exportarBitacoraPDF()` y `exportarBitacoraExcel()` implementadas
- UI moderna y profesional

⚠️ **Backend pendiente:**
- El `ReporteService.java` NO tiene soporte para tipo "BITACORA"
- Necesita agregar métodos para generar reportes de bitácora

---

## 🔨 **Solución:**

### Opción 1: Agregar al ReporteService.java

Agrega este método después de la línea 222:

```java
private void generarReporteBitacoraExcel(Sheet sheet, CellStyle headerStyle, ReporteDTO reporteDTO) {
    // Encabezados
    Row headerRow = sheet.createRow(0);
    String[] columnas = {"Código", "Título", "Tipo", "Remitente/Destinatario", "Estado", "Fecha Ingreso", "Usuario"};
    
    for (int i = 0; i < columnas.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columnas[i]);
        cell.setCellStyle(headerStyle);
    }

    // Obtener datos
    List<Documento> documentos = filtrarDocumentos(reporteDTO);

    // Llenar datos
    int rowNum = 1;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    for (Documento doc : documentos) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(doc.getCodigo());
        row.createCell(1).setCellValue(doc.getTitulo());
        row.createCell(2).setCellValue(doc.getTipoDocumento() != null ? doc.getTipoDocumento().getNombre() : "N/A");
        
        String participantes = "";
        if (doc.getRemitente() != null) participantes += "R: " + doc.getRemitente();
        if (doc.getDestinatario() != null) participantes += (participantes.isEmpty() ? "" : " | ") + "D: " + doc.getDestinatario();
        row.createCell(3).setCellValue(participantes.isEmpty() ? "N/A" : participantes);
        
        row.createCell(4).setCellValue(doc.getEstado().toString());
        row.createCell(5).setCellValue(doc.getFechaIngreso().format(formatter));
        row.createCell(6).setCellValue(doc.getUsuarioRegistro() != null ? doc.getUsuarioRegistro().getNombre() : "N/A");
    }

    // Autoajustar columnas
    for (int i = 0; i < columnas.length; i++) {
        sheet.autoSizeColumn(i);
    }
}
```

Y este método después de línea 373:

```java
private void generarReporteBitacoraPDF(Document document, ReporteDTO reporteDTO) {
    List<Documento> documentos = filtrarDocumentos(reporteDTO);
    
    Table table = new Table(new float[]{1.5f, 3f, 2f, 3f, 1.5f, 2f, 2f});
    table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

    // Encabezados
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Código").setBold()));
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Título").setBold()));
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Tipo").setBold()));
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Participantes").setBold()));
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Estado").setBold()));
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Fecha").setBold()));
    table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Usuario").setBold()));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    for (Documento doc : documentos) {
        table.addCell(doc.getCodigo());
        table.addCell(doc.getTitulo());
        table.addCell(doc.getTipoDocumento() != null ? doc.getTipoDocumento().getNombre() : "N/A");
        
        String participantes = "";
        if (doc.getRemitente() != null) participantes += "R: " + doc.getRemitente();
        if (doc.getDestinatario() != null) participantes += (participantes.isEmpty() ? "" : "\nD: ") + doc.getDestinatario();
        table.addCell(participantes.isEmpty() ? "N/A" : participantes);
        
        table.addCell(doc.getEstado().toString());
        table.addCell(doc.getFechaIngreso().format(formatter));
        table.addCell(doc.getUsuarioRegistro() != null ? doc.getUsuarioRegistro().getNombre() : "N/A");
    }

    document.add(table);
    document.add(new Paragraph("\nTotal de documentos: " + documentos.size()).setBold());
}
```

También agrega en el switch de línea 247 (método generarReportePDF):

```java
case "BITACORA":
    generarReporteBitacoraPDF(document, reporteDTO);
    break;
```

### Opción 2: Solución Temporal (Más Rápida)

Cambia el frontend para usar "DOCUMENTOS" en lugar de "BITACORA":

En `bitacora.js`, líneas 416 y 456, cambia:
```javascript
tipoReporte: 'DOCUMENTOS',  // En lugar de 'BITACORA'
```

---

## 🎨 ** Para Mejorar el CSS (Hacerlo más profesional y único):**

Reemplaza el archivo `style.css` con un diseño más premium. Te genero un archivo separado con los estilos mejorados.

---

##  **Pasos Inmediatos:**

1. ✅ Restaura `ReporteService.java` desde git si se dañó
2. ✅ Agrega los métodos `generarReporteBitacoraExcel()` y `generarReporteBitacoraPDF()`
3. ✅ Actualiza el switch para incluir case "BITACORA"
4. ✅ Prueba los reportes
5. ✅ (Opcional) Mejora el CSS según el nuevo diseño

¿Quieres que haga alguna de estas opciones automáticamente?
