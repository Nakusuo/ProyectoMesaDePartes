package com.pnp.mesadepartes.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pnp.mesadepartes.model.Bitacora;
import com.pnp.mesadepartes.repository.BitacoraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BitacoraService {
    
    private final BitacoraRepository bitacoraRepository;
    
    /**
     * Obtener todas las entradas de bitácora con paginación
     */
    public Page<Bitacora> obtenerTodos(Pageable pageable) {
        return bitacoraRepository.findAllByOrderByFechaDesc(pageable);
    }
    
    /**
     * Buscar bitácora con entrada
     */
    public Page<Bitacora> buscarConEntrada(Pageable pageable) {
        return bitacoraRepository.findAllWithEntrada(pageable);
    }
    
    /**
     * Buscar bitácora con salida
     */
    public Page<Bitacora> buscarConSalida(Pageable pageable) {
        return bitacoraRepository.findAllWithSalida(pageable);
    }
    
    /**
     * Buscar bitácora sin salida (pendientes)
     */
    public Page<Bitacora> buscarSinSalida(Pageable pageable) {
        return bitacoraRepository.findAllSinSalida(pageable);
    }
    
    /**
     * Buscar bitácora por código de documento
     */
    public List<Bitacora> buscarPorCodigoDocumento(String codigoDocumento) {
        return bitacoraRepository.findByCodigoDocumento(codigoDocumento);
    }
    
    /**
     * Buscar bitácora por ID de documento
     */
    public Bitacora buscarPorIdDocumento(Long idDocumento) {
        return bitacoraRepository.findByIdDocumento(idDocumento);
    }
    
    /**
     * Buscar bitácora por rango de fechas
     */
    public Page<Bitacora> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return bitacoraRepository.findByFechaEntradaBetween(fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    public Page<Bitacora> buscarConFiltros(
            Boolean tieneEntrada,
            Boolean tieneSalida,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        return bitacoraRepository.buscarConFiltros(tieneEntrada, tieneSalida, fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Obtener bitácora por ID
     */
    public Bitacora obtenerPorId(Long id) {
        return bitacoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de bitácora no encontrado"));
    }
    
    /**
     * Registrar o actualizar salida de documento en bitácora
     * Este método busca el registro existente y actualiza la información de salida
     */
    public Bitacora registrarSalida(
            Long idDocumento,
            String codigoDocumento,
            String tituloDocumento,
            String tipoDocumento,
            String destinatario,
            String numeroDocumentoSalida,
            String observaciones,
            String archivoUrl,
            Long idUsuario,
            String nombreUsuario) {
        
        // Buscar registro existente
        Bitacora bitacora = bitacoraRepository.findByIdDocumento(idDocumento);
        
        if (bitacora != null) {
            // Actualizar con información de salida
            bitacora.setTieneSalida(true);
            bitacora.setDestinatario(destinatario);
            bitacora.setFechaSalida(LocalDateTime.now());
            bitacora.setUsuarioSalida(nombreUsuario);
            bitacora.setNumeroDocumentoSalida(numeroDocumentoSalida);
            bitacora.setObservacionesSalida(observaciones);
            bitacora.setArchivoSalidaUrl(archivoUrl);
        } else {
            // Crear nuevo registro solo con salida
            bitacora = new Bitacora();
            bitacora.setIdDocumento(idDocumento);
            bitacora.setCodigoDocumento(codigoDocumento);
            bitacora.setTituloDocumento(tituloDocumento);
            bitacora.setTipoDocumento(tipoDocumento);
            bitacora.setTieneEntrada(false);
            bitacora.setTieneSalida(true);
            bitacora.setDestinatario(destinatario);
            bitacora.setFechaSalida(LocalDateTime.now());
            bitacora.setUsuarioSalida(nombreUsuario);
            bitacora.setNumeroDocumentoSalida(numeroDocumentoSalida);
            bitacora.setObservacionesSalida(observaciones);
            bitacora.setArchivoSalidaUrl(archivoUrl);
        }
        
        return bitacoraRepository.save(bitacora);
    }
    
    /**
     * Exportar bitácora a PDF
     */
    public byte[] exportarPDF() {
        List<Bitacora> registros = bitacoraRepository.findAll();
        // Por ahora retornamos un mensaje, implementación completa de PDF requiere más dependencias
        String mensaje = "Exportación PDF - Total registros: " + registros.size();
        return mensaje.getBytes();
    }
    
    /**
     * Exportar bitácora a Excel
     */
    public byte[] exportarExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Bitácora");
            
            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columnas = {"Código", "Título", "Tipo", "Remitente", "Fecha Entrada", 
                                 "Usuario Entrada", "Destinatario", "Fecha Salida", "Usuario Salida", "Observaciones"};
            
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }
            
            // Obtener datos
            List<Bitacora> registros = bitacoraRepository.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            int rowNum = 1;
            for (Bitacora reg : registros) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(reg.getCodigoDocumento() != null ? reg.getCodigoDocumento() : "");
                row.createCell(1).setCellValue(reg.getTituloDocumento() != null ? reg.getTituloDocumento() : "");
                row.createCell(2).setCellValue(reg.getTipoDocumento() != null ? reg.getTipoDocumento() : "");
                row.createCell(3).setCellValue(reg.getRemitente() != null ? reg.getRemitente() : "");
                row.createCell(4).setCellValue(reg.getFechaEntrada() != null ? reg.getFechaEntrada().format(formatter) : "");
                row.createCell(5).setCellValue(reg.getUsuarioEntrada() != null ? reg.getUsuarioEntrada() : "");
                row.createCell(6).setCellValue(reg.getDestinatario() != null ? reg.getDestinatario() : "");
                row.createCell(7).setCellValue(reg.getFechaSalida() != null ? reg.getFechaSalida().format(formatter) : "");
                row.createCell(8).setCellValue(reg.getUsuarioSalida() != null ? reg.getUsuarioSalida() : "");
                row.createCell(9).setCellValue(reg.getObservacionesSalida() != null ? reg.getObservacionesSalida() : "");
            }
            
            workbook.write(out);
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }
}
