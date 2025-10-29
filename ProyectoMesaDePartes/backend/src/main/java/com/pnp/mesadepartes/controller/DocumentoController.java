package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.dto.DocumentoRegistroDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            System.out.println("=== Iniciando registro de documento ===");
            System.out.println("DTO recibido: " + dto);

            Usuario usuarioRegistrador = usuarioRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Error: Usuario registrador no encontrado."));
            System.out.println("Usuario registrador: " + usuarioRegistrador.getNombre());

            Usuario usuarioAsignado = usuarioRepository.findById(dto.getIdUsuarioAsignado())
                    .orElseThrow(() -> new RuntimeException("Error: Usuario asignado no encontrado."));
            System.out.println("Usuario asignado: " + usuarioAsignado.getNombre());

            TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                    .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));
            System.out.println("Tipo documento: " + tipoDoc.getNombre());

            // Generar código secuencial basado en el total de documentos
            long totalDocumentos = documentoRepository.count();
            String codigo = String.format("DOC-%06d", totalDocumentos + 1);
            System.out.println("Código generado: " + codigo);

            Documento doc = new Documento();
            doc.setCodigo(codigo);
            doc.setTitulo(dto.getTitulo());
            doc.setDescripcion(dto.getDescripcion());
            doc.setRemitente(dto.getRemitente());
            doc.setNumeroDocumento(dto.getNumeroDocumento());
            doc.setFechaIngreso(LocalDateTime.now());
            doc.setEstado(EstadoDocumento.Registrado);
            doc.setTipoDocumento(tipoDoc);
            doc.setUsuarioRegistro(usuarioRegistrador);
            doc.setArchivoUrl(dto.getArchivoUrl());

            System.out.println("Guardando documento...");
            Documento docGuardado = documentoRepository.save(doc);
            System.out.println("Documento guardado con ID: " + docGuardado.getIdDocumento());

            if (dto.getNumeroHt() != null && !dto.getNumeroHt().isEmpty()) {
                System.out.println("Guardando hoja de trámite...");
                HojaTramite ht = new HojaTramite();
                ht.setNumeroHt(dto.getNumeroHt());
                ht.setDocumento(docGuardado);
                hojaTramiteRepository.save(ht);
            }

            System.out.println("Creando trámite...");
            Tramite tramite = new Tramite();
            tramite.setDocumento(docGuardado);
            tramite.setUsuarioCreador(usuarioRegistrador);
            tramite.setUsuarioAsignado(usuarioAsignado);
            tramiteRepository.save(tramite);

            System.out.println("=== Registro completado exitosamente ===");
            return ResponseEntity.ok(docGuardado);

        } catch (Exception e) {
            System.err.println("ERROR al registrar documento: " + e.getMessage());
            e.printStackTrace();
            
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
    public ResponseEntity<List<Map<String, Object>>> getDocumentosBitacora() {
        System.out.println("📋 Obteniendo documentos con información de asignación");
        List<Documento> documentos = documentoRepository.findAll();
        
        List<Map<String, Object>> resultado = documentos.stream().map(doc -> {
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
        
        System.out.println("✅ Total de documentos procesados: " + resultado.size());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        return documentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/asignados/{userId}")
    public ResponseEntity<List<Documento>> getDocumentosAsignados(@PathVariable Long userId) {
        System.out.println("📋 Obteniendo documentos asignados al usuario ID: " + userId);
        
        // Buscar trámites donde el usuario es el asignado
        List<Tramite> tramites = tramiteRepository.findByUsuarioAsignado_IdUsuario(userId);
        
        // Extraer los documentos de los trámites
        List<Documento> documentos = tramites.stream()
                .map(Tramite::getDocumento)
                .distinct()
                .toList();
        
        System.out.println("✅ Total de documentos asignados: " + documentos.size());
        return ResponseEntity.ok(documentos);
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
            System.err.println("Error al subir archivo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }
}
