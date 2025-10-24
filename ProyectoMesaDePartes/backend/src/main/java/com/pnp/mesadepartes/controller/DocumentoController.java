package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.dto.DocumentoRegistroDTO;
import com.pnp.mesadepartes.model.*;
import com.pnp.mesadepartes.repository.*;
import com.pnp.mesadepartes.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap; // Added import for HashMap

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
    @PreAuthorize("hasAuthority('Mesa de Partes') or hasAuthority('Administrador')")
    public ResponseEntity<?> registrarDocumento(@RequestBody DocumentoRegistroDTO dto) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioRegistrador = usuarioRepository.findById(userDetails.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Error: Usuario registrador no encontrado."));

        Usuario usuarioAsignado = usuarioRepository.findById(dto.getIdUsuarioAsignado())
                .orElseThrow(() -> new RuntimeException("Error: Usuario asignado no encontrado."));

        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));

        Documento doc = new Documento();
        doc.setCodigo(UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        doc.setTitulo(dto.getTitulo());
        doc.setDescripcion(dto.getDescripcion());
        doc.setRemitente(dto.getRemitente());
        doc.setNumeroDocumento(dto.getNumeroDocumento());
        doc.setFechaIngreso(LocalDateTime.now());
        doc.setEstado(EstadoDocumento.Registrado);
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
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Administrador') or hasAuthority('Jefatura')")
    public List<Documento> getAllDocumentos() {
        return documentoRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        return documentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/{codigo}")
    @PreAuthorize("isAuthenticated()")
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
}
