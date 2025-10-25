package com.pnp.mesadepartes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.model.TipoDocumento;
import com.pnp.mesadepartes.repository.TipoDocumentoRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tipos-documento")
public class TipoDocumentoController {

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TipoDocumento> getAllTiposDocumento() {
        return tipoDocumentoRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<TipoDocumento> createTipoDocumento(@RequestBody TipoDocumento tipoDocumento) {
         if (tipoDocumento.getNombre() == null || tipoDocumento.getNombre().isEmpty()) {
             return ResponseEntity.badRequest().build();
        }
        TipoDocumento nuevoTipo = tipoDocumentoRepository.save(tipoDocumento);
        return ResponseEntity.ok(nuevoTipo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<TipoDocumento> updateTipoDocumento(@PathVariable Long id, @RequestBody TipoDocumento tipoDetails) {
        return tipoDocumentoRepository.findById(id)
                .map(tipo -> {
                    tipo.setNombre(tipoDetails.getNombre());
                    TipoDocumento updatedTipo = tipoDocumentoRepository.save(tipo);
                    return ResponseEntity.ok(updatedTipo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<?> deleteTipoDocumento(@PathVariable Long id) {
        return tipoDocumentoRepository.findById(id)
                .map(tipo -> {
                    tipoDocumentoRepository.delete(tipo);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
