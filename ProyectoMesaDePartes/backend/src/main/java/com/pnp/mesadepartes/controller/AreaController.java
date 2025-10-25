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

import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.repository.AreaRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/areas")
public class AreaController {

    @Autowired
    private AreaRepository areaRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Area> createArea(@RequestBody Area area) {
        if (area.getNombre() == null || area.getNombre().isEmpty()) {
             return ResponseEntity.badRequest().build();
        }
        Area nuevaArea = areaRepository.save(area);
        return ResponseEntity.ok(nuevaArea);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Area> updateArea(@PathVariable Long id, @RequestBody Area areaDetails) {
        return areaRepository.findById(id)
                .map(area -> {
                    area.setNombre(areaDetails.getNombre());
                    area.setSigla(areaDetails.getSigla());
                    Area updatedArea = areaRepository.save(area);
                    return ResponseEntity.ok(updatedArea);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<?> deleteArea(@PathVariable Long id) {
        return areaRepository.findById(id)
                .map(area -> {
                    areaRepository.delete(area);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

