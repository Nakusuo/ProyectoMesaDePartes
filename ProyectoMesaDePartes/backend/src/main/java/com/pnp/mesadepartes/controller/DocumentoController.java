package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.dto.DocumentoRegistroDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

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

    @PersistenceContext
    private EntityManager entityManager;

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

            // Generar número de registro automático (obtener el máximo + 1)
            Integer ultimoNumeroRegistro = documentoRepository.findAll()
                    .stream()
                    .map(Documento::getNumeroRegistro)
                    .max(Integer::compare)
                    .orElse(0);
            
            Integer nuevoNumeroRegistro = ultimoNumeroRegistro + 1;
            System.out.println("Número de registro generado: " + nuevoNumeroRegistro);

            // Combinar titulo y descripcion en asunto
            String asunto = dto.getTitulo();
            if (dto.getDescripcion() != null && !dto.getDescripcion().trim().isEmpty()) {
                asunto = dto.getTitulo() + "\n\n" + dto.getDescripcion();
            }

            Documento doc = new Documento();
            doc.setNumeroRegistro(nuevoNumeroRegistro);
            doc.setFechaDocumento(LocalDateTime.now());
            doc.setAsunto(asunto);
            doc.setRemitente(dto.getRemitente());
            doc.setNumeroDocumento(dto.getNumeroDocumento());
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

    @GetMapping("/{id}")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        return documentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/{numeroRegistro}")
    public ResponseEntity<?> buscarDocumentoPorNumeroRegistro(@PathVariable Integer numeroRegistro) {
        Optional<Documento> optDoc = documentoRepository.findByNumeroRegistro(numeroRegistro);
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

            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
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
    
    // Endpoint temporal para arreglar la estructura de la tabla (REMOVER EN PRODUCCIÓN)
    @GetMapping("/admin/fix-table")
    @Transactional
    public ResponseEntity<?> fixTableStructure() {
        try {
            // Eliminar columnas antiguas que no existen en el modelo
            entityManager.createNativeQuery("ALTER TABLE documentos DROP COLUMN IF EXISTS codigo").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE documentos DROP COLUMN IF EXISTS titulo").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE documentos DROP COLUMN IF EXISTS descripcion").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE documentos DROP COLUMN IF EXISTS fecha_ingreso").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE documentos DROP COLUMN IF EXISTS destinatario").executeUpdate();
            
            return ResponseEntity.ok(Map.of(
                "message", "Tabla documentos actualizada correctamente",
                "status", "success"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "Error al actualizar la tabla",
                "error", e.getMessage(),
                "status", "error"
            ));
        }
    }
}
