package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.model.Rol;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.AreaRepository;
import com.pnp.mesadepartes.repository.RolRepository;
import com.pnp.mesadepartes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('Administrador') or hasAuthority('Jefatura')")
    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> {
            u.setPasswordHash(null);
        });
        return usuarios;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador') or hasAuthority('Jefatura')")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setPasswordHash(null);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioDetails.getNombre());
                    usuario.setApellido(usuarioDetails.getApellido());
                    usuario.setUsername(usuarioDetails.getUsername());
                    usuario.setEmail(usuarioDetails.getEmail());
                    usuario.setTelefono(usuarioDetails.getTelefono());
                    usuario.setActivo(usuarioDetails.isActivo());
                    usuario.setTipoContrato(usuarioDetails.getTipoContrato());
                    usuario.setAvatarUrl(usuarioDetails.getAvatarUrl());

                    if (usuarioDetails.getPasswordHash() != null && !usuarioDetails.getPasswordHash().isEmpty()) {
                        usuario.setPasswordHash(passwordEncoder.encode(usuarioDetails.getPasswordHash()));
                    }

                    if (usuarioDetails.getArea() != null && usuarioDetails.getArea().getIdArea() != null) {
                        Area area = areaRepository.findById(usuarioDetails.getArea().getIdArea())
                                        .orElseThrow(() -> new RuntimeException("Área no encontrada"));
                        usuario.setArea(area);
                    } else {
                        usuario.setArea(null);
                    }

                    if (usuarioDetails.getRoles() != null && !usuarioDetails.getRoles().isEmpty()) {
                        Set<Rol> roles = usuarioDetails.getRoles().stream()
                            .map(rolDto -> rolRepository.findById(rolDto.getIdRol())
                                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolDto.getIdRol())))
                            .collect(Collectors.toSet());
                        usuario.setRoles(roles);
                    } else {
                        usuario.setRoles(Set.of());
                    }

                    Usuario updatedUsuario = usuarioRepository.save(usuario);
                    updatedUsuario.setPasswordHash(null);
                    return ResponseEntity.ok(updatedUsuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                     usuarioRepository.delete(usuario);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}