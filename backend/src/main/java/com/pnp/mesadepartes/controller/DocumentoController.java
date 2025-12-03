package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.dto.DocumentoRegistroDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;
import com.pnp.mesadepartes.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Controlador REST para la gestión de Documentos
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Documentos", description = "API para gestión de documentos y expedientes")
public class DocumentoController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentoController.class);

    @Autowired private DocumentoRepository documentoRepository;
    @Autowired private TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TramiteRepository tramiteRepository;
    @Autowired private HojaTramiteRepository hojaTramiteRepository;

    // =====================================================
    // ENDPOINTS PARA HOJAS DE TRÁMITE
    // =====================================================
    
    /**
     * Obtiene la hoja de trámite de un documento
     */
    @GetMapping("/hojas-tramite/documento/{idDocumento}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerHojaTramitePorDocumento(@PathVariable Long idDocumento) {
        try {
            logger.info("Obteniendo hoja de trámite para documento ID: {}", idDocumento);
            List<HojaTramite> hojasTramite = hojaTramiteRepository.findByIdDocumento(idDocumento);
            logger.info("Hojas de trámite encontradas: {}", hojasTramite.size());
            return ResponseEntity.ok(hojasTramite);
        } catch (Exception e) {
            logger.error("Error al obtener hoja de trámite para documento {}: {}", idDocumento, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener hoja de trámite: " + e.getMessage()));
        }
    }
    
    /**
     * Crea una nueva hoja de trámite
     */
    @PostMapping("/hojas-tramite")
    public ResponseEntity<?> crearHojaTramite(@RequestBody Map<String, Object> payload) {
        try {
            Long idDocumento = Long.valueOf(payload.get("idDocumento").toString());
            String numeroHt = payload.get("numeroHt").toString();
            
            // Buscar el documento
            Optional<Documento> docOpt = documentoRepository.findById(idDocumento);
            if (docOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Documento no encontrado"));
            }
            
            HojaTramite hojaTramite = new HojaTramite();
            hojaTramite.setDocumento(docOpt.get());
            hojaTramite.setNumeroHt(numeroHt);
            
            HojaTramite htGuardada = hojaTramiteRepository.save(hojaTramite);
            return ResponseEntity.status(HttpStatus.CREATED).body(htGuardada);
        } catch (Exception e) {
            logger.error("Error al crear hoja de trámite: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear hoja de trámite: " + e.getMessage()));
        }
    }
    
    /**
     * Actualiza una hoja de trámite existente
     */
    @PutMapping("/hojas-tramite/{id}")
    public ResponseEntity<?> actualizarHojaTramite(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            Optional<HojaTramite> htExistente = hojaTramiteRepository.findById(id);
            if (htExistente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Hoja de trámite no encontrada"));
            }
            
            HojaTramite ht = htExistente.get();
            ht.setNumeroHt(payload.get("numeroHt").toString());
            
            // Actualizar documento si se proporciona
            if (payload.containsKey("idDocumento")) {
                Long idDocumento = Long.valueOf(payload.get("idDocumento").toString());
                Optional<Documento> docOpt = documentoRepository.findById(idDocumento);
                if (docOpt.isPresent()) {
                    ht.setDocumento(docOpt.get());
                }
            }
            
            HojaTramite htActualizada = hojaTramiteRepository.save(ht);
            return ResponseEntity.ok(htActualizada);
        } catch (Exception e) {
            logger.error("Error al actualizar hoja de trámite: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar hoja de trámite: " + e.getMessage()));
        }
    }

    /**
     * Registra un nuevo documento en el sistema
     * 
     * @param dto Datos del documento a registrar
     * @return ResponseEntity con el documento creado o error
     */
    @PostMapping("/registrar")
    @Operation(summary = "Registrar documento", description = "Registra un nuevo documento en el sistema con sus datos y asignación inicial")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en el registro")
    })
    public ResponseEntity<?> registrarDocumento(@Valid @RequestBody @Parameter(description = "Datos del documento") DocumentoRegistroDTO dto) {
        try {
            logger.info("Registrando nuevo documento: {}", dto.getTitulo());
            
            // Obtener usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            Usuario usuarioRegistrador = usuarioRepository.findById(userDetails.getIdUsuario())
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
            logger.info("Documento guardado exitosamente con código: {}", docGuardado.getCodigo());

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
            logger.info("Trámite creado y asignado a usuario: {}", usuarioAsignado.getUsername());

            return ResponseEntity.ok(docGuardado);

        } catch (Exception e) {
            logger.error("Error al registrar documento: {}", e.getMessage(), e);
            
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Error al registrar el documento");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Obtiene todos los documentos del sistema
     * 
     * @return Lista de todos los documentos
     */
    @GetMapping
    @Operation(summary = "Listar documentos", description = "Obtiene la lista completa de documentos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de documentos obtenida exitosamente")
    public ResponseEntity<List<Documento>> getAllDocumentos() {
        logger.info("Obteniendo todos los documentos");
        List<Documento> documentos = documentoRepository.findAll();
        logger.info("Total de documentos encontrados: {}", documentos.size());
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene documentos con paginación para la bitácora
     * 
     * @param page Número de página (default 0)
     * @param size Tamaño de página (default 10)
     * @param sortBy Campo para ordenar (default fechaIngreso)
     * @param sortDir Dirección de ordenamiento (asc/desc, default desc)
     * @return Página de documentos con información de paginación
     */
    @GetMapping("/bitacora")
    @Operation(summary = "Obtener bitácora de documentos", description = "Obtiene documentos paginados con información de asignación")
    @ApiResponse(responseCode = "200", description = "Bitácora obtenida exitosamente")
    public ResponseEntity<?> getDocumentosBitacora(
            @RequestParam(defaultValue = "0") @Parameter(description = "Número de página") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Tamaño de página") int size,
            @RequestParam(defaultValue = "fechaIngreso") @Parameter(description = "Campo de ordenamiento") String sortBy,
            @RequestParam(defaultValue = "desc") @Parameter(description = "Dirección de ordenamiento") String sortDir) {
        
        logger.info("Obteniendo bitácora de documentos - Página: {}, Tamaño: {}", page, size);
        
        try {
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
            
            logger.info("Bitácora obtenida exitosamente: {} documentos en página {}", resultado.size(), page);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener bitácora: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener bitácora: " + e.getMessage()));
        }
    }

    /**
     * Obtiene un documento por su ID
     * 
     * @param id ID del documento
     * @return Documento encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener documento por ID", description = "Busca y retorna un documento específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento encontrado"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<Documento> getDocumentoById(@PathVariable @Parameter(description = "ID del documento") Long id) {
        logger.info("Buscando documento con ID: {}", id);
        return documentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene los documentos asignados a un usuario específico
     * 
     * @param userId ID del usuario
     * @return Lista de documentos asignados al usuario
     */
    @GetMapping("/asignados/{userId}")
    @Operation(summary = "Obtener documentos asignados", description = "Retorna todos los documentos asignados a un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de documentos asignados obtenida exitosamente")
    public ResponseEntity<?> getDocumentosAsignados(@PathVariable @Parameter(description = "ID del usuario") Long userId) {
        logger.info("Obteniendo documentos asignados al usuario: {}", userId);
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
            
            logger.info("Encontrados {} documentos asignados al usuario {}", documentos.size(), userId);
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            logger.error("Error al obtener documentos asignados: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener documentos asignados: " + e.getMessage()));
        }
    }

    /**
     * Busca un documento por su código
     * 
     * @param codigo Código del documento
     * @return Documento con historial y asignación
     */
    @GetMapping("/buscar/{codigo}")
    @Operation(summary = "Buscar documento por código", description = "Busca un documento específico usando su código único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento encontrado"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<?> buscarDocumentoPorCodigo(@PathVariable @Parameter(description = "Código del documento") String codigo) {
        logger.info("Buscando documento con código: {}", codigo);
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
    
    /**
     * Busca un documento por número de registro
     * 
     * @param numeroRegistro Número de registro del documento
     * @return Documento encontrado o 404
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar por número de registro", description = "Busca documento usando número de registro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento encontrado"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<?> buscarDocumentoPorNumeroRegistro(@RequestParam @Parameter(description = "Número de registro") String numeroRegistro) {
        logger.info("Buscando documento por número de registro: {}", numeroRegistro);
        Optional<Documento> optDoc = documentoRepository.findByCodigo(numeroRegistro);
        if (!optDoc.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optDoc.get());
    }

    /**
     * Sube un archivo PDF asociado a un documento
     * 
     * @param file Archivo PDF a subir
     * @return URL del archivo subido
     */
    @PostMapping("/upload")
    @Operation(summary = "Subir archivo PDF", description = "Sube un archivo PDF al servidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Archivo subido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido")
    })
    public ResponseEntity<?> uploadFile(@RequestParam("file") @Parameter(description = "Archivo PDF") MultipartFile file) {
        logger.info("Intentando subir archivo: {}", file.getOriginalFilename());
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

            logger.info("Archivo subido exitosamente: {}", fileName);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Error al subir archivo: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }

    /**
     * Actualiza el estado de un documento
     * 
     * @param id ID del documento
     * @param request Mapa con el nuevo estado y observaciones opcionales
     * @return Documento actualizado
     */
    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de documento", description = "Cambia el estado de un documento y opcionalmente agrega observaciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<?> actualizarEstado(
            @PathVariable @Parameter(description = "ID del documento") Long id, 
            @RequestBody @Parameter(description = "Estado y observaciones") Map<String, String> request) {
        logger.info("Actualizando estado del documento ID: {}", id);
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
            logger.info("Estado del documento {} actualizado a: {}", documento.getCodigo(), documento.getEstado());
            
            // Devolver documento actualizado como Map (evitar serialización circular)
            Map<String, Object> response = new HashMap<>();
            response.put("idDocumento", documento.getIdDocumento());
            response.put("codigo", documento.getCodigo());
            response.put("titulo", documento.getTitulo());
            response.put("estado", documento.getEstado().name());
            response.put("message", "Estado actualizado correctamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar estado: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al actualizar estado: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para visualizar archivos PDF
     * 
     * @param archivo Ruta del archivo PDF
     * @return Recurso PDF para visualización
     */
    @GetMapping("/ver-pdf")
    @Operation(summary = "Visualizar PDF", description = "Sirve un archivo PDF para visualización en el navegador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF servido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    public ResponseEntity<?> verPdf(@RequestParam @Parameter(description = "Ruta del archivo") String archivo) {
        logger.info("Solicitando visualización de PDF: {}", archivo);
        try {
            // Remover el slash inicial si existe
            String relativePath = archivo.startsWith("/") ? archivo.substring(1) : archivo;
            
            // Construir ruta completa del archivo
            Path filePath = Paths.get(relativePath).toAbsolutePath();
            
            logger.debug("Buscando archivo en: {}", filePath.toString());
            
            if (!Files.exists(filePath)) {
                logger.warn("Archivo no encontrado: {}", filePath.toString());
                return ResponseEntity.status(404).body(Map.of("error", "Archivo no encontrado: " + archivo));
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            logger.info("Archivo encontrado, sirviendo PDF: {}", filePath.getFileName());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            logger.error("Error al leer archivo: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error al leer el archivo: " + e.getMessage()));
        }
    }

    /**
     * Actualiza los datos completos de un documento
     * 
     * @param id ID del documento
     * @param dto Nuevos datos del documento
     * @return Documento actualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar documento completo", description = "Actualiza todos los campos de un documento existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<?> actualizarDocumento(
            @PathVariable @Parameter(description = "ID del documento") Long id, 
            @Valid @RequestBody @Parameter(description = "Nuevos datos del documento") DocumentoRegistroDTO dto) {
        logger.info("Actualizando documento con ID: {}", id);
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
            logger.info("Documento actualizado exitosamente: {}", documento.getCodigo());
            
            return ResponseEntity.ok(Map.of(
                "message", "Documento actualizado correctamente",
                "documento", documento
            ));
            
        } catch (Exception e) {
            logger.error("Error al actualizar documento: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al actualizar documento: " + e.getMessage()));
        }
    }
}

