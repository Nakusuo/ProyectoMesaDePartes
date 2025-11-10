package com.pnp.mesadepartes.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pnp.mesadepartes.dto.ReporteDTO;
import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.Tramite;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.DocumentoRepository;
import com.pnp.mesadepartes.repository.TramiteRepository;
import com.pnp.mesadepartes.service.ReporteService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;
    
    @Autowired
    private DocumentoRepository documentoRepository;
    
    @Autowired
    private TramiteRepository tramiteRepository;

    @PostMapping("/generar")
    public ResponseEntity<?> generarReporte(@RequestBody ReporteDTO reporteDTO) {
        try {
            byte[] reporte = reporteService.generarReporte(reporteDTO);
            
            HttpHeaders headers = new HttpHeaders();
            String filename = "reporte_" + reporteDTO.getTipoReporte() + "_" + 
                            System.currentTimeMillis();
            
            if ("EXCEL".equalsIgnoreCase(reporteDTO.getFormato())) {
                headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDispositionFormData("attachment", filename + ".xlsx");
            } else {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", filename + ".pdf");
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reporte);
                    
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar reporte");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Nuevo endpoint para generar PDF simple
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generarReportePDF() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate()); // Horizontal
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Título
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("REPORTE GENERAL DE DOCUMENTOS - MESA DE PARTES PNP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Fecha
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            Paragraph fecha = new Paragraph("Fecha de generación: " + LocalDateTime.now().format(formatter), dateFont);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            fecha.setSpacingAfter(15);
            document.add(fecha);
            
            // Obtener documentos
            List<Documento> documentos = documentoRepository.findAll();
            
            // Total
            Font statsFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Paragraph stats = new Paragraph("Total de documentos: " + documentos.size(), statsFont);
            stats.setSpacingAfter(10);
            document.add(stats);
            
            // Crear tabla
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            
            float[] columnWidths = {1.5f, 2.5f, 3f, 2.5f, 1.5f, 2f, 2f, 2f};
            table.setWidths(columnWidths);
            
            // Encabezados
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
            BaseColor headerColor = new BaseColor(0, 100, 46); // Verde PNP
            
            String[] headers = {"Código", "Nro. Doc", "Título", "Remitente", "Tipo", "Estado", "Fecha Ingreso", "Asignado a"};
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }
            
            // Datos
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Documento doc : documentos) {
                table.addCell(new Phrase(doc.getCodigo() != null ? doc.getCodigo() : "-", cellFont));
                table.addCell(new Phrase(doc.getNumeroDocumento() != null ? doc.getNumeroDocumento() : "-", cellFont));
                
                String titulo = doc.getTitulo() != null ? doc.getTitulo() : "-";
                if (titulo.length() > 40) titulo = titulo.substring(0, 37) + "...";
                table.addCell(new Phrase(titulo, cellFont));
                
                String remitente = doc.getRemitente() != null ? doc.getRemitente() : "-";
                if (remitente.length() > 35) remitente = remitente.substring(0, 32) + "...";
                table.addCell(new Phrase(remitente, cellFont));
                
                String tipo = doc.getTipoDocumento() != null ? doc.getTipoDocumento().getNombre() : "-";
                table.addCell(new Phrase(tipo, cellFont));
                
                String estado = doc.getEstado() != null ? doc.getEstado().name().replace("_", " ") : "-";
                table.addCell(new Phrase(estado, cellFont));
                
                String fechaIngreso = doc.getFechaIngreso() != null ? doc.getFechaIngreso().format(dateFormatter) : "-";
                table.addCell(new Phrase(fechaIngreso, cellFont));
                
                List<Tramite> tramites = tramiteRepository.findByDocumento(doc);
                String asignado = "-";
                if (!tramites.isEmpty() && tramites.get(0).getUsuarioAsignado() != null) {
                    Usuario user = tramites.get(0).getUsuarioAsignado();
                    asignado = user.getNombre() + " " + user.getApellido();
                }
                table.addCell(new Phrase(asignado, cellFont));
            }
            
            document.add(table);
            
            // Pie
            Paragraph footer = new Paragraph("\nReporte generado por Sistema Mesa de Partes - PNP", 
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_PDF);
            headers2.setContentDispositionFormData("attachment", "reporte_documentos.pdf");
            
            return new ResponseEntity<>(baos.toByteArray(), headers2, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> estadisticas = reporteService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }
}
