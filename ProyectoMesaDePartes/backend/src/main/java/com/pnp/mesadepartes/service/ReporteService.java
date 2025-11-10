package com.pnp.mesadepartes.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.pnp.mesadepartes.dto.ReporteDTO;
import com.pnp.mesadepartes.model.Derivacion;
import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.repository.DerivacionRepository;
import com.pnp.mesadepartes.repository.DocumentoRepository;

@Service
public class ReporteService {

    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private DerivacionRepository derivacionRepository;

    public byte[] generarReporte(ReporteDTO reporteDTO) throws IOException {
        if ("EXCEL".equalsIgnoreCase(reporteDTO.getFormato())) {
            return generarReporteExcel(reporteDTO);
        } else if ("PDF".equalsIgnoreCase(reporteDTO.getFormato())) {
            return generarReportePDF(reporteDTO);
        }
        throw new IllegalArgumentException("Formato no soportado: " + reporteDTO.getFormato());
    }

    private byte[] generarReporteExcel(ReporteDTO reporteDTO) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte");

        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        switch (reporteDTO.getTipoReporte()) {
            case "DOCUMENTOS":
                generarReporteDocumentosExcel(sheet, headerStyle, reporteDTO);
                break;
            case "TIEMPOS":
                generarReporteTiemposExcel(sheet, headerStyle, reporteDTO);
                break;
            case "AREAS":
                generarReporteAreasExcel(sheet, headerStyle, reporteDTO);
                break;
            default:
                generarReporteDocumentosExcel(sheet, headerStyle, reporteDTO);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void generarReporteDocumentosExcel(Sheet sheet, CellStyle headerStyle, ReporteDTO reporteDTO) {
        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] columnas = {"Código", "Título", "Remitente", "Estado", "Tipo Documento", "Fecha Ingreso"};
        
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
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
            row.createCell(2).setCellValue(doc.getRemitente());
            row.createCell(3).setCellValue(doc.getEstado().toString());
            row.createCell(4).setCellValue(doc.getTipoDocumento() != null ? 
                    doc.getTipoDocumento().getNombre() : "N/A");
            row.createCell(5).setCellValue(doc.getFechaIngreso().format(formatter));
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void generarReporteTiemposExcel(Sheet sheet, CellStyle headerStyle, ReporteDTO reporteDTO) {
        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] columnas = {"Código", "Título", "Fecha Ingreso", "Tiempo Total (horas)", 
                             "Total Derivaciones", "Estado Actual"};
        
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
            row.createCell(2).setCellValue(doc.getFechaIngreso().format(formatter));
            
            // Calcular tiempo total
            long horas = Duration.between(doc.getFechaIngreso(), LocalDateTime.now()).toHours();
            row.createCell(3).setCellValue(horas);
            
            // Total derivaciones
            List<Derivacion> derivaciones = derivacionRepository
                    .findByDocumentoIdDocumentoOrderByFechaDerivacionDesc(doc.getIdDocumento());
            row.createCell(4).setCellValue(derivaciones.size());
            
            row.createCell(5).setCellValue(doc.getEstado().toString());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void generarReporteAreasExcel(Sheet sheet, CellStyle headerStyle, ReporteDTO reporteDTO) {
        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] columnas = {"Área", "Total Derivaciones", "Pendientes", "Recibidos", "Tiempo Promedio (horas)"};
        
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Obtener todas las derivaciones
        List<Derivacion> derivaciones;
        if (reporteDTO.getFechaInicio() != null && reporteDTO.getFechaFin() != null && reporteDTO.getIdArea() != null) {
            derivaciones = derivacionRepository.findByAreaAndFechaBetween(
                    reporteDTO.getIdArea(), 
                    reporteDTO.getFechaInicio(), 
                    reporteDTO.getFechaFin());
        } else {
            derivaciones = derivacionRepository.findAll();
        }

        // Agrupar por área
        Map<String, List<Derivacion>> derivacionesPorArea = derivaciones.stream()
                .collect(Collectors.groupingBy(d -> d.getAreaDestino().getNombre()));

        // Llenar datos
        int rowNum = 1;
        for (Map.Entry<String, List<Derivacion>> entry : derivacionesPorArea.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            String nombreArea = entry.getKey();
            List<Derivacion> derivacionesArea = entry.getValue();
            
            row.createCell(0).setCellValue(nombreArea);
            row.createCell(1).setCellValue(derivacionesArea.size());
            
            long pendientes = derivacionesArea.stream()
                    .filter(d -> "PENDIENTE".equals(d.getEstado()))
                    .count();
            row.createCell(2).setCellValue(pendientes);
            
            long recibidos = derivacionesArea.stream()
                    .filter(d -> "RECIBIDO".equals(d.getEstado()))
                    .count();
            row.createCell(3).setCellValue(recibidos);
            
            // Calcular tiempo promedio
            double tiempoPromedio = derivacionesArea.stream()
                    .filter(d -> d.getFechaRecepcion() != null)
                    .mapToLong(d -> Duration.between(d.getFechaDerivacion(), d.getFechaRecepcion()).toHours())
                    .average()
                    .orElse(0.0);
            row.createCell(4).setCellValue(String.format("%.2f", tiempoPromedio));
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] generarReportePDF(ReporteDTO reporteDTO) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        document.add(new Paragraph("REPORTE DE " + reporteDTO.getTipoReporte())
                .setBold()
                .setFontSize(18));
        
        document.add(new Paragraph("Fecha de generación: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFontSize(10));
        
        document.add(new Paragraph("\n"));

        switch (reporteDTO.getTipoReporte()) {
            case "DOCUMENTOS":
                generarReporteDocumentosPDF(document, reporteDTO);
                break;
            case "TIEMPOS":
                generarReporteTiemposPDF(document, reporteDTO);
                break;
            case "AREAS":
                generarReporteAreasPDF(document, reporteDTO);
                break;
            default:
                generarReporteDocumentosPDF(document, reporteDTO);
        }

        document.close();
        return outputStream.toByteArray();
    }

    private void generarReporteDocumentosPDF(Document document, ReporteDTO reporteDTO) {
        List<Documento> documentos = filtrarDocumentos(reporteDTO);
        
        Table table = new Table(new float[]{2, 4, 3, 2, 2, 3});
        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Código").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Título").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Remitente").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tipo Doc").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha Ingreso").setBold()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Documento doc : documentos) {
            table.addCell(doc.getCodigo());
            table.addCell(doc.getTitulo());
            table.addCell(doc.getRemitente());
            table.addCell(doc.getEstado().toString());
            table.addCell(doc.getTipoDocumento() != null ? doc.getTipoDocumento().getNombre() : "N/A");
            table.addCell(doc.getFechaIngreso().format(formatter));
        }

        document.add(table);
        document.add(new Paragraph("\nTotal de documentos: " + documentos.size()).setBold());
    }

    private void generarReporteTiemposPDF(Document document, ReporteDTO reporteDTO) {
        List<Documento> documentos = filtrarDocumentos(reporteDTO);
        
        Table table = new Table(new float[]{2, 4, 3, 2, 2, 2});
        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Código").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Título").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha Ingreso").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tiempo (hrs)").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Derivaciones").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado").setBold()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Documento doc : documentos) {
            table.addCell(doc.getCodigo());
            table.addCell(doc.getTitulo());
            table.addCell(doc.getFechaIngreso().format(formatter));
            
            long horas = Duration.between(doc.getFechaIngreso(), LocalDateTime.now()).toHours();
            table.addCell(String.valueOf(horas));
            
            List<Derivacion> derivaciones = derivacionRepository
                    .findByDocumentoIdDocumentoOrderByFechaDerivacionDesc(doc.getIdDocumento());
            table.addCell(String.valueOf(derivaciones.size()));
            
            table.addCell(doc.getEstado().toString());
        }

        document.add(table);
        document.add(new Paragraph("\nTotal de documentos: " + documentos.size()).setBold());
    }

    private void generarReporteAreasPDF(Document document, ReporteDTO reporteDTO) {
        List<Derivacion> derivaciones;
        if (reporteDTO.getFechaInicio() != null && reporteDTO.getFechaFin() != null && reporteDTO.getIdArea() != null) {
            derivaciones = derivacionRepository.findByAreaAndFechaBetween(
                    reporteDTO.getIdArea(), 
                    reporteDTO.getFechaInicio(), 
                    reporteDTO.getFechaFin());
        } else {
            derivaciones = derivacionRepository.findAll();
        }

        Map<String, List<Derivacion>> derivacionesPorArea = derivaciones.stream()
                .collect(Collectors.groupingBy(d -> d.getAreaDestino().getNombre()));

        Table table = new Table(new float[]{3, 2, 2, 2, 2});
        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Área").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Pendientes").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Recibidos").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tiempo Prom (hrs)").setBold()));

        for (Map.Entry<String, List<Derivacion>> entry : derivacionesPorArea.entrySet()) {
            String nombreArea = entry.getKey();
            List<Derivacion> derivacionesArea = entry.getValue();
            
            table.addCell(nombreArea);
            table.addCell(String.valueOf(derivacionesArea.size()));
            
            long pendientes = derivacionesArea.stream()
                    .filter(d -> "PENDIENTE".equals(d.getEstado()))
                    .count();
            table.addCell(String.valueOf(pendientes));
            
            long recibidos = derivacionesArea.stream()
                    .filter(d -> "RECIBIDO".equals(d.getEstado()))
                    .count();
            table.addCell(String.valueOf(recibidos));
            
            double tiempoPromedio = derivacionesArea.stream()
                    .filter(d -> d.getFechaRecepcion() != null)
                    .mapToLong(d -> Duration.between(d.getFechaDerivacion(), d.getFechaRecepcion()).toHours())
                    .average()
                    .orElse(0.0);
            table.addCell(String.format("%.2f", tiempoPromedio));
        }

        document.add(table);
    }

    private List<Documento> filtrarDocumentos(ReporteDTO reporteDTO) {
        List<Documento> documentos = documentoRepository.findAll();
        
        // Filtrar por fechas si se proporcionan
        if (reporteDTO.getFechaInicio() != null && reporteDTO.getFechaFin() != null) {
            documentos = documentos.stream()
                    .filter(d -> !d.getFechaIngreso().isBefore(reporteDTO.getFechaInicio()) &&
                                 !d.getFechaIngreso().isAfter(reporteDTO.getFechaFin()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por estado si se proporciona
        if (reporteDTO.getEstado() != null && !reporteDTO.getEstado().isEmpty()) {
            documentos = documentos.stream()
                    .filter(d -> d.getEstado().toString().equalsIgnoreCase(reporteDTO.getEstado()))
                    .collect(Collectors.toList());
        }
        
        return documentos;
    }

    public Map<String, Object> obtenerEstadisticasGenerales() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        long totalDocumentos = documentoRepository.count();
        estadisticas.put("totalDocumentos", totalDocumentos);
        
        long totalDerivaciones = derivacionRepository.count();
        estadisticas.put("totalDerivaciones", totalDerivaciones);
        
        // Documentos por estado
        List<Documento> documentos = documentoRepository.findAll();
        Map<String, Long> documentosPorEstado = documentos.stream()
                .collect(Collectors.groupingBy(d -> d.getEstado().toString(), Collectors.counting()));
        estadisticas.put("documentosPorEstado", documentosPorEstado);
        
        // Tiempo promedio de atención
        double tiempoPromedio = documentos.stream()
                .mapToLong(d -> Duration.between(d.getFechaIngreso(), LocalDateTime.now()).toHours())
                .average()
                .orElse(0.0);
        estadisticas.put("tiempoPromedioHoras", tiempoPromedio);
        
        return estadisticas;
    }
}
