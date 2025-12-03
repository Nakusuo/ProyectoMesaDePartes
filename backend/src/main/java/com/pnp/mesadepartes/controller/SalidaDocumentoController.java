package com.pnp.mesadepartes.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.EstadoDocumento;
import com.pnp.mesadepartes.model.SalidaDocumento;
import com.pnp.mesadepartes.model.TipoDocumento;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.DocumentoRepository;
import com.pnp.mesadepartes.repository.SalidaDocumentoRepository;
import com.pnp.mesadepartes.repository.TipoDocumentoRepository;
import com.pnp.mesadepartes.repository.UsuarioRepository;
import com.pnp.mesadepartes.service.BitacoraService;
import com.pnp.mesadepartes.service.NotificacionService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/salidas")
public class SalidaDocumentoController {

    @Autowired
    private SalidaDocumentoRepository salidaDocumentoRepository;
    
    @Autowired
    private DocumentoRepository documentoRepository;
    
    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BitacoraService bitacoraService;
    
    @Autowired
    private NotificacionService notificacionService;


    @PostMapping("/registrar")
    public ResponseEntity<?> registrarSalida(@RequestBody Map<String, Object> request) {
        try {
            // Obtener datos del request
            Long idDocumento = Long.valueOf(request.get("idDocumento").toString());
            Long idTipoDocumento = request.get("idTipoDocumento") != null ? 
                Long.valueOf(request.get("idTipoDocumento").toString()) : null;
            Long idUsuarioSalida = request.get("idUsuarioSalida") != null ? 
                Long.valueOf(request.get("idUsuarioSalida").toString()) : null;
            
            String numeroDocumentoSalida = (String) request.get("numeroDocumentoSalida");
            String destinatarioSalida = (String) request.get("destinatarioSalida");
            String observacion = (String) request.get("observacion");
            String archivoCargoUrl = (String) request.get("archivoCargoUrl");
            
            // Validar que el documento existe
            Documento documento = documentoRepository.findById(idDocumento)
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
            
            // Crear la salida
            SalidaDocumento salida = new SalidaDocumento();
            salida.setDocumento(documento);
            salida.setNumeroDocumentoSalida(numeroDocumentoSalida);
            salida.setDestinatarioSalida(destinatarioSalida);
            salida.setObservacion(observacion);
            salida.setFechaSalida(LocalDateTime.now());
            salida.setArchivoCargoUrl(archivoCargoUrl);
            
            if (idTipoDocumento != null) {
                TipoDocumento tipoDoc = tipoDocumentoRepository.findById(idTipoDocumento)
                        .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));
                salida.setTipoDocumento(tipoDoc);
            }
            
            if (idUsuarioSalida != null) {
                Usuario usuario = usuarioRepository.findById(idUsuarioSalida)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                salida.setUsuarioSalida(usuario);
            }
            
            // Cambiar el estado del documento a "Salida"
            documento.setEstado(EstadoDocumento.Salida);
            documentoRepository.save(documento);
            
            // Guardar la salida
            SalidaDocumento salidaGuardada = salidaDocumentoRepository.save(salida);
            
            // Registrar en bitácora
            String nombreUsuario = null;
            if (idUsuarioSalida != null) {
                Usuario usuario = usuarioRepository.findById(idUsuarioSalida).orElse(null);
                if (usuario != null) {
                    nombreUsuario = usuario.getNombre() + " " + usuario.getApellido();
                }
            }
            
            bitacoraService.registrarSalida(
                documento.getIdDocumento(),
                documento.getCodigo(),
                documento.getTitulo(),
                documento.getTipoDocumento() != null ? documento.getTipoDocumento().getNombre() : "N/A",
                destinatarioSalida,
                numeroDocumentoSalida,
                observacion,
                archivoCargoUrl,
                idUsuarioSalida,
                nombreUsuario
            );
            
            // Crear notificaciones
            // 1. Notificar al usuario que registró la salida
            if (idUsuarioSalida != null) {
                String tituloNotif = "Salida Registrada - " + documento.getCodigo();
                String mensajeNotif = "Se ha registrado la salida del documento '" + documento.getTitulo() + 
                                     "' con destino a: " + destinatarioSalida;
                notificacionService.crearNotificacion(
                    idUsuarioSalida, 
                    documento.getIdDocumento(), 
                    tituloNotif, 
                    mensajeNotif, 
                    "DOCUMENTO_SALIDA"
                );
            }
            
            // 2. Notificar al usuario que originalmente registró el documento (si es diferente)
            if (documento.getUsuarioCreador() != null && 
                !documento.getUsuarioCreador().getIdUsuario().equals(idUsuarioSalida)) {
                String tituloNotif = "Documento Enviado - " + documento.getCodigo();
                String mensajeNotif = "El documento '" + documento.getTitulo() + 
                                     "' que usted registró ha sido enviado a: " + destinatarioSalida;
                notificacionService.crearNotificacion(
                    documento.getUsuarioCreador().getIdUsuario(), 
                    documento.getIdDocumento(), 
                    tituloNotif, 
                    mensajeNotif, 
                    "DOCUMENTO_SALIDA"
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Salida de documento registrada exitosamente");
            response.put("salida", salidaGuardada);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al registrar salida: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<SalidaDocumento>> getAllSalidas() {
        List<SalidaDocumento> salidas = salidaDocumentoRepository.findAll();
        return ResponseEntity.ok(salidas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getSalidaById(@PathVariable Long id) {
        return salidaDocumentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/documento/{idDocumento}")
    public ResponseEntity<?> getSalidasByDocumento(@PathVariable Long idDocumento) {
        try {
            Documento documento = documentoRepository.findById(idDocumento)
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
            
            List<SalidaDocumento> salidas = salidaDocumentoRepository.findByDocumento(documento);
            return ResponseEntity.ok(salidas);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener salidas: " + e.getMessage()));
        }
    }
    
    @PostMapping("/upload-cargo")
    public ResponseEntity<?> uploadCargo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
            }

            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten archivos PDF"));
            }

            String uploadDir = "uploads/cargos/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            long timestamp = System.currentTimeMillis();
            String fileName = timestamp + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/cargos/" + fileName;

            return ResponseEntity.ok(Map.of(
                "url", fileUrl,
                "message", "Archivo de cargo subido exitosamente"
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSalida(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            SalidaDocumento salida = salidaDocumentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Salida no encontrada"));
            
            // Actualizar campos si se proporcionan
            if (request.containsKey("numeroDocumentoSalida")) {
                salida.setNumeroDocumentoSalida((String) request.get("numeroDocumentoSalida"));
            }
            if (request.containsKey("destinatarioSalida")) {
                salida.setDestinatarioSalida((String) request.get("destinatarioSalida"));
            }
            if (request.containsKey("observacion")) {
                salida.setObservacion((String) request.get("observacion"));
            }
            if (request.containsKey("archivoCargoUrl")) {
                salida.setArchivoCargoUrl((String) request.get("archivoCargoUrl"));
            }
            
            SalidaDocumento salidaActualizada = salidaDocumentoRepository.save(salida);
            
            return ResponseEntity.ok(Map.of(
                "message", "Salida actualizada exitosamente",
                "salida", salidaActualizada
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al actualizar salida: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSalida(@PathVariable Long id) {
        try {
            salidaDocumentoRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Salida eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al eliminar salida: " + e.getMessage()));
        }
    }
    
    // Nuevos endpoints para la página de salida de documentos
    @PostMapping
    public ResponseEntity<?> crearSalida(@RequestBody SalidaDocumento salida) {
        try {
            // Validar que el documento existe
            Documento documento = documentoRepository.findById(salida.getDocumento().getIdDocumento())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
            
            salida.setDocumento(documento);
            
            SalidaDocumento nuevaSalida = salidaDocumentoRepository.save(salida);
            return ResponseEntity.ok(nuevaSalida);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear salida: " + e.getMessage()));
        }
    }
    
    @GetMapping("/documento/{documentoId}")
    public ResponseEntity<?> listarSalidasPorDocumento(@PathVariable Long documentoId) {
        try {
            List<SalidaDocumento> salidas = salidaDocumentoRepository.findByDocumentoIdDocumentoOrderByFechaSalidaDesc(documentoId);
            return ResponseEntity.ok(salidas);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al listar salidas: " + e.getMessage()));
        }
    }
}
