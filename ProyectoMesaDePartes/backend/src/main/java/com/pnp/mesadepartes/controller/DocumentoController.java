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
import java.util.UUID; // <-- CORRECCIÓN: Faltaba este import

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

        // --- 1. Obtener entidades relacionadas ---
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioRegistrador = usuarioRepository.findById(userDetails.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Error: Usuario registrador no encontrado."));

        Usuario usuarioAsignado = usuarioRepository.findById(dto.getIdUsuarioAsignado())
                .orElseThrow(() -> new RuntimeException("Error: Usuario asignado no encontrado."));
        
        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));

        // --- 2. Crear y guardar el Documento ---
        Documento doc = new Documento();
        doc.setCodigo(UUID.randomUUID().toString().substring(0, 10).toUpperCase()); // Esta línea ya no dará error
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

        // --- 3. Crear y guardar la Hoja de Trámite (si aplica) ---
        if (dto.getNumeroHt() != null && !dto.getNumeroHt().isEmpty()) {
            HojaTramite ht = new HojaTramite();
            ht.setNumeroHt(dto.getNumeroHt());
            ht.setDocumento(docGuardado);
            hojaTramiteRepository.save(ht);
        }

        // --- 4. Crear y guardar el Trámite (la asignación) ---
        Tramite tramite = new Tramite();
        tramite.setDocumento(docGuardado);
        tramite.setUsuarioCreador(usuarioRegistrador); 
        tramite.setUsuarioAsignado(usuarioAsignado); 
        
        tramiteRepository.save(tramite);

        return ResponseEntity.ok(docGuardado);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Trabajador') or hasAuthority('Administrador') or hasAuthority('Jefatura')")
    public List<Documento> getAllDocumentos() {
        return documentoRepository.findAll();
    }
}