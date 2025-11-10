package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.dto.DocumentoRegistroDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TramiteRepository tramiteRepository;
    @Autowired private HojaTramiteRepository hojaTramiteRepository;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarDocumento(@RequestBody DocumentoRegistroDTO dto) {
        try {
            Usuario usuarioRegistrador = usuarioRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Error: Usuario registrador no encontrado."));

            Usuario usuarioAsignado = usuarioRepository.findById(dto.getIdUsuarioAsignado())
                    .orElseThrow(() -> new RuntimeException("Error: Usuario asignado no encontrado."));

            TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                    .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));

            // Generar código secuencial basado en el total de documentos
            long totalDocumentos = documentoRepository.count();
            String codigo = String.format("DOC-%06d", totalDocumentos + 1);

            Documento doc = new Documento();
            doc.setCodigo(codigo);
            doc.setTitulo(dto.getTitulo());
            doc.setDescripcion(dto.getDescripcion());
            doc.setRemitente(dto.getRemitente());
            doc.setNumeroDocumento(dto.getNumeroDocumento());
            doc.setFechaIngreso(LocalDateTime.now());
            doc.setEstado(EstadoDocumento.Asignado);
            doc.setTipoDocumento(tipoDoc);
            doc.setUsuarioRegistro(usuarioRegistrador);
            doc.setArchivoUrl(dto.getArchivoUrl());

            Documento docGuardado = documentoRepository.save(doc);

            if (dto.getNumeroHt() != null && !dto.getNumeroHt().isEmpty()) {
                HojaTramite ht = new HojaTramite();
                ht.setNumeroHt(dto.getNumeroHt());
                ht.setDocumento(docGuardado);
                hojaTramiteRepository.save(ht);
            }

            Tramite tramite = new Tramite();
            tramite.setDocumento(docGuardado);
            tramite.setUsuarioCreador(usuarioRegistrador);
            tramite.setUsuarioAsignado(usuarioAsignado);
            tramiteRepository.save(tramite);

            return ResponseEntity.ok(docGuardado);

        } catch (Exception e) {
            
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Error al registrar el documento");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Documento>> getAllDocumentos() {
        System.out.println("📋 Obteniendo todos los documentos");
        List<Documento> documentos = documentoRepository.findAll();
        System.out.println("✅ Total de documentos: " + documentos.size());
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/bitacora")
    public ResponseEntity<?> getDocumentosBitacora(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaIngreso") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            // Crear el objeto Pageable con ordenamiento
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Obtener página de documentos
            Page<Documento> documentosPage = documentoRepository.findAll(pageable);
            
            // Convertir a lista de mapas con información del usuario asignado
            List<Map<String, Object>> resultado = documentosPage.getContent().stream().map(doc -> {
                Map<String, Object> docInfo = new HashMap<>();
                docInfo.put("documento", doc);
                
                // Buscar el trámite asociado para obtener el usuario asignado
                List<Tramite> tramites = tramiteRepository.findByDocumento(doc);
                if (!tramites.isEmpty()) {
                    Tramite tramite = tramites.get(0);
                    Usuario asignado = tramite.getUsuarioAsignado();
                    if (asignado != null) {
                        docInfo.put("usuarioAsignado", asignado.getNombre() + " " + asignado.getApellido());
                        docInfo.put("idUsuarioAsignado", asignado.getIdUsuario());
                    } else {
                        docInfo.put("usuarioAsignado", "Sin asignar");
                        docInfo.put("idUsuarioAsignado", null);
                    }
                } else {
                    docInfo.put("usuarioAsignado", "Sin asignar");
                    docInfo.put("idUsuarioAsignado", null);
                }
                
                return docInfo;
            }).toList();
            
            // Crear respuesta con datos de paginación
            Map<String, Object> response = new HashMap<>();
            response.put("content", resultado);
            response.put("currentPage", documentosPage.getNumber());
            response.put("totalItems", documentosPage.getTotalElements());
            response.put("totalPages", documentosPage.getTotalPages());
            response.put("hasNext", documentosPage.hasNext());
            response.put("hasPrevious", documentosPage.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener bitácora: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        return documentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/asignados/{userId}")
    public ResponseEntity<?> getDocumentosAsignados(@PathVariable Long userId) {
        try {
            // Buscar trámites donde el usuario es el asignado
            List<Tramite> tramites = tramiteRepository.findByUsuarioAsignado_IdUsuario(userId);
            
            // Extraer los documentos de los trámites y convertir a mapas simples
            List<Map<String, Object>> documentos = tramites.stream()
                    .map(Tramite::getDocumento)
                    .distinct()
                    .map(doc -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("idDocumento", doc.getIdDocumento());
                        map.put("codigo", doc.getCodigo());
                        map.put("titulo", doc.getTitulo());
                        map.put("descripcion", doc.getDescripcion());
                        map.put("numeroDocumento", doc.getNumeroDocumento());
                        map.put("estado", doc.getEstado());
                        map.put("remitente", doc.getRemitente());
                        map.put("destinatario", doc.getDestinatario());
                        map.put("fechaIngreso", doc.getFechaIngreso());
                        map.put("archivoUrl", doc.getArchivoUrl());
                        
                        // Agregar tipo documento de forma segura
                        if (doc.getTipoDocumento() != null) {
                            Map<String, Object> tipo = new HashMap<>();
                            tipo.put("idTipoDocumento", doc.getTipoDocumento().getIdTipoDocumento());
                            tipo.put("nombre", doc.getTipoDocumento().getNombre());
                            map.put("tipoDocumento", tipo);
                        }
                        
                        return map;
                    })
                    .toList();
            
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener documentos asignados: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar/{codigo}")
    public ResponseEntity<?> buscarDocumentoPorCodigo(@PathVariable String codigo) {
        Optional<Documento> optDoc = documentoRepository.findByCodigo(codigo);
        if (!optDoc.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Documento documento = optDoc.get();

        Optional<Tramite> optTramite = tramiteRepository.findByDocumento(documento).stream().findFirst();
        String asignadoA = optTramite.map(t -> t.getUsuarioAsignado().getNombre() + " " + t.getUsuarioAsignado().getApellido())
                                     .orElse("No asignado");

        List<Object> historial = List.of();

        var respuesta = new HashMap<String, Object>();
        respuesta.put("documento", documento);
        respuesta.put("historial", historial);
        respuesta.put("asignadoA", asignadoA);

        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDocumentoPorNumeroRegistro(@RequestParam String numeroRegistro) {
        Optional<Documento> optDoc = documentoRepository.findByCodigo(numeroRegistro);
        if (!optDoc.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optDoc.get());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
            }

            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten archivos PDF"));
            }

            String uploadDir = "uploads/documentos/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único con timestamp
            long timestamp = System.currentTimeMillis();
            String fileName = timestamp + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/documentos/" + fileName;

            var response = new HashMap<String, String>();
            response.put("url", fileUrl);
            response.put("message", "Archivo subido exitosamente");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Optional<Documento> optDocumento = documentoRepository.findById(id);
            
            if (!optDocumento.isPresent()) {
                return ResponseEntity.status(404).body(Map.of("error", "Documento no encontrado"));
            }
            
            Documento documento = optDocumento.get();
            String nuevoEstado = request.get("estado");
            String observaciones = request.get("observaciones");
            
            // Validar que el estado sea válido
            try {
                // Convertir espacios a guiones bajos para coincidir con el ENUM
                EstadoDocumento estado = EstadoDocumento.valueOf(nuevoEstado.replace(" ", "_"));
                documento.setEstado(estado);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Estado no válido: " + nuevoEstado + ". Estados permitidos: Asignado, Recibido, En_Proceso, Observado, Finalizado, Salida"));
            }
            
            // Si hay observaciones, guardarlas en la descripción (por ahora)
            // TODO: Crear tabla separada para observaciones/informes
            if (observaciones != null && !observaciones.trim().isEmpty()) {
                String descripcionActual = documento.getDescripcion() != null ? documento.getDescripcion() : "";
                documento.setDescripcion(descripcionActual + "\n\n[INFORME - " + LocalDateTime.now() + "]\n" + observaciones);
            }
            
            documentoRepository.save(documento);
            
            // Devolver documento actualizado como Map (evitar serialización circular)
            Map<String, Object> response = new HashMap<>();
            response.put("idDocumento", documento.getIdDocumento());
            response.put("codigo", documento.getCodigo());
            response.put("titulo", documento.getTitulo());
            response.put("estado", documento.getEstado().name());
            response.put("message", "Estado actualizado correctamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al actualizar estado: " + e.getMessage()));
        }
    }

    // Endpoint para visualizar PDF
    @GetMapping("/ver-pdf")
    public ResponseEntity<?> verPdf(@RequestParam String archivo) {
        try {
            // Remover el slash inicial si existe
            String relativePath = archivo.startsWith("/") ? archivo.substring(1) : archivo;
            
            // Construir ruta completa del archivo
            Path filePath = Paths.get(relativePath).toAbsolutePath();
            
            System.out.println("🔍 Buscando archivo: " + filePath.toString());
            
            if (!Files.exists(filePath)) {
                System.err.println("❌ Archivo no encontrado: " + filePath.toString());
                return ResponseEntity.status(404).body(Map.of("error", "Archivo no encontrado: " + archivo));
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            System.out.println("✅ Archivo encontrado, sirviendo PDF");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            System.err.println("❌ Error al leer archivo: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error al leer el archivo: " + e.getMessage()));
        }
    }

    // Endpoint para actualizar documento completo (para jefatura)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDocumento(@PathVariable Long id, @RequestBody DocumentoRegistroDTO dto) {
        try {
            Optional<Documento> optDocumento = documentoRepository.findById(id);
            
            if (!optDocumento.isPresent()) {
                return ResponseEntity.status(404).body(Map.of("error", "Documento no encontrado"));
            }
            
            Documento documento = optDocumento.get();
            
            // Actualizar campos
            if (dto.getTitulo() != null) documento.setTitulo(dto.getTitulo());
            if (dto.getDescripcion() != null) documento.setDescripcion(dto.getDescripcion());
            if (dto.getRemitente() != null) documento.setRemitente(dto.getRemitente());
            if (dto.getNumeroDocumento() != null) documento.setNumeroDocumento(dto.getNumeroDocumento());
            
            // Actualizar tipo de documento si se proporciona
            if (dto.getIdTipoDocumento() != null) {
                TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                        .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));
                documento.setTipoDocumento(tipoDoc);
            }
            
            // Actualizar archivo si se proporciona
            if (dto.getArchivoUrl() != null) {
                documento.setArchivoUrl(dto.getArchivoUrl());
            }
            
            documentoRepository.save(documento);
            
            return ResponseEntity.ok(Map.of(
                "message", "Documento actualizado correctamente",
                "documento", documento
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al actualizar documento: " + e.getMessage()));
        }
    }
}

