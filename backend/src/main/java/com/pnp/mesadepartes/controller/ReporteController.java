package com.pnp.mesadepartes.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pnp.mesadepartes.dto.ReporteDTO;
import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.Tramite;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.DocumentoRepository;
import com.pnp.mesadepartes.repository.TramiteRepository;
import com.pnp.mesadepartes.service.ReporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la generación de reportes
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "API para generación de reportes PDF y Excel")
public class ReporteController {

    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);

    @Autowired
    private ReporteService reporteService;
    
    @Autowired
    private DocumentoRepository documentoRepository;
    
    @Autowired
    private TramiteRepository tramiteRepository;

    /**
     * Genera un reporte en formato PDF o Excel
     * 
     * @param reporteDTO Datos del reporte a generar
     * @return Archivo de reporte en el formato solicitado
     */
    @PostMapping("/generar")
    @Operation(summary = "Generar reporte", description = "Genera un reporte personalizado en formato PDF o Excel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al generar el reporte")
    })
    public ResponseEntity<?> generarReporte(@Valid @RequestBody @Parameter(description = "Configuración del reporte") ReporteDTO reporteDTO) {
        logger.info("Generando reporte tipo: {}, formato: {}", reporteDTO.getTipoReporte(), reporteDTO.getFormato());
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
            
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            logger.info("Reporte generado exitosamente: {}", filename);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reporte);
                    
        } catch (Exception e) {
            logger.error("Error al generar reporte: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar reporte");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Genera un reporte PDF general de todos los documentos
     * 
     * @return Archivo PDF con el reporte general
     */
    @GetMapping("/pdf")
    @Operation(summary = "Generar PDF general", description = "Genera un PDF con el listado completo de documentos")
    @ApiResponse(responseCode = "200", description = "PDF generado exitosamente")
    public ResponseEntity<byte[]> generarReportePDF() {
        logger.info("Generando reporte PDF general de documentos");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, com.itextpdf.kernel.geom.PageSize.A4.rotate());
            
            // Título
            Paragraph title = new Paragraph("REPORTE GENERAL DE DOCUMENTOS - MESA DE PARTES PNP")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);
            
            // Fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            Paragraph fecha = new Paragraph("Fecha de generación: " + LocalDateTime.now().format(formatter))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(15);
            document.add(fecha);
            
            // Obtener documentos
            List<Documento> documentos = documentoRepository.findAll();
            
            // Total
            Paragraph stats = new Paragraph("Total de documentos: " + documentos.size())
                .setFontSize(11)
                .setBold()
                .setMarginBottom(10);
            document.add(stats);
            
            // Crear tabla con 8 columnas
            float[] columnWidths = {1.5f, 2.5f, 3f, 2.5f, 1.5f, 2f, 2f, 2f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Encabezados
            String[] headers = {"Código", "Nro. Doc", "Título", "Remitente", "Tipo", "Estado", "Fecha Ingreso", "Asignado a"};
            
            for (String header : headers) {
                Cell cell = new Cell()
                    .add(new Paragraph(header).setFontSize(9).setBold())
                    .setBackgroundColor(ColorConstants.GREEN)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
                table.addHeaderCell(cell);
            }
            
            // Datos
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Documento doc : documentos) {
                table.addCell(new Cell().add(new Paragraph(
                    doc.getCodigo() != null ? doc.getCodigo() : "-").setFontSize(8)));
                
                table.addCell(new Cell().add(new Paragraph(
                    doc.getNumeroDocumento() != null ? doc.getNumeroDocumento() : "-").setFontSize(8)));
                
                String titulo = doc.getTitulo() != null ? doc.getTitulo() : "-";
                if (titulo.length() > 40) titulo = titulo.substring(0, 37) + "...";
                table.addCell(new Cell().add(new Paragraph(titulo).setFontSize(8)));
                
                String remitente = doc.getRemitente() != null ? doc.getRemitente() : "-";
                if (remitente.length() > 35) remitente = remitente.substring(0, 32) + "...";
                table.addCell(new Cell().add(new Paragraph(remitente).setFontSize(8)));
                
                String tipo = doc.getTipoDocumento() != null ? doc.getTipoDocumento().getNombre() : "-";
                table.addCell(new Cell().add(new Paragraph(tipo).setFontSize(8)));
                
                String estado = doc.getEstado() != null ? doc.getEstado().name().replace("_", " ") : "-";
                table.addCell(new Cell().add(new Paragraph(estado).setFontSize(8)));
                
                String fechaIngreso = doc.getFechaIngreso() != null ? 
                    doc.getFechaIngreso().format(dateFormatter) : "-";
                table.addCell(new Cell().add(new Paragraph(fechaIngreso).setFontSize(8)));
                
                List<Tramite> tramites = tramiteRepository.findByDocumento(doc);
                String asignado = "-";
                if (!tramites.isEmpty() && tramites.get(0).getUsuarioAsignado() != null) {
                    Usuario user = tramites.get(0).getUsuarioAsignado();
                    asignado = user.getNombre() + " " + user.getApellido();
                }
                table.addCell(new Cell().add(new Paragraph(asignado).setFontSize(8)));
            }
            
            document.add(table);
            
            // Pie
            Paragraph footer = new Paragraph("\nReporte generado por Sistema Mesa de Partes - PNP")
                .setFontSize(8)
                .setItalic()
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
            document.add(footer);
            
            document.close();
            
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_PDF);
            headers2.setContentDispositionFormData("attachment", "reporte_documentos.pdf");
            headers2.setCacheControl("no-cache, no-store, must-revalidate");
            headers2.setPragma("no-cache");
            headers2.setExpires(0);
            
            logger.info("Reporte PDF general generado exitosamente con {} documentos", documentos.size());
            return new ResponseEntity<>(baos.toByteArray(), headers2, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error generando PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene estadísticas generales del sistema
     * 
     * @return Mapa con estadísticas del sistema
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas", description = "Retorna estadísticas generales del sistema de documentos")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas generales del sistema");
        Map<String, Object> estadisticas = reporteService.obtenerEstadisticasGenerales();
        logger.info("Estadísticas obtenidas exitosamente");
        return ResponseEntity.ok(estadisticas);
    }
}
