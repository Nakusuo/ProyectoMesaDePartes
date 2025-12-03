package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.exception.ValidationException;
import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.model.Rol;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.AreaRepository;
import com.pnp.mesadepartes.repository.RolRepository;
import com.pnp.mesadepartes.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de Usuarios
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    /**
     * Obtiene la lista de todos los usuarios
     * 
     * @return Lista de usuarios (sin contraseñas)
     */
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios del sistema sin sus contraseñas")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public List<Usuario> getAllUsuarios() {
        logger.info("Obteniendo lista de todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> {
            u.setPasswordHash(null);
        });
        logger.info("Se encontraron {} usuarios", usuarios.size());
        return usuarios;
    }

    /**
     * Obtiene un usuario por su ID
     * 
     * @param id ID del usuario
     * @return Usuario encontrado (sin contraseña) o 404
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable @Parameter(description = "ID del usuario") Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setPasswordHash(null);
                    logger.info("Usuario encontrado: {}", usuario.getUsername());
                    return ResponseEntity.ok(usuario);
                })
                .orElseGet(() -> {
                    logger.warn("Usuario no encontrado con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Crea un nuevo usuario en el sistema
     * 
     * @param usuarioDetails Datos del nuevo usuario
     * @return Usuario creado (sin contraseña)
     */
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody @Parameter(description = "Datos del nuevo usuario") Usuario usuarioDetails) {
        logger.info("Creando nuevo usuario: {}", usuarioDetails.getUsername());
        
        try {
            // Verificar si ya existe el username
            if (usuarioRepository.existsByUsername(usuarioDetails.getUsername())) {
                throw new ValidationException("El nombre de usuario ya existe");
            }
            
            // Verificar si ya existe el email
            if (usuarioDetails.getEmail() != null && usuarioRepository.existsByEmail(usuarioDetails.getEmail())) {
                throw new ValidationException("El email ya está registrado");
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setApellido(usuarioDetails.getApellido());
            usuario.setUsername(usuarioDetails.getUsername());
            usuario.setEmail(usuarioDetails.getEmail());
            usuario.setTelefono(usuarioDetails.getTelefono());
            usuario.setActivo(usuarioDetails.isActivo());
            usuario.setTipoContrato(usuarioDetails.getTipoContrato());
            
            // Encriptar contraseña
            if (usuarioDetails.getPasswordHash() != null && !usuarioDetails.getPasswordHash().isEmpty()) {
                usuario.setPasswordHash(passwordEncoder.encode(usuarioDetails.getPasswordHash()));
            } else {
                throw new ValidationException("La contraseña es obligatoria");
            }
            
            // Asignar área
            if (usuarioDetails.getArea() != null && usuarioDetails.getArea().getIdArea() != null) {
                Area area = areaRepository.findById(usuarioDetails.getArea().getIdArea())
                                .orElseThrow(() -> new ValidationException("Área no encontrada"));
                usuario.setArea(area);
            }
            
            // Asignar roles
            if (usuarioDetails.getRoles() != null && !usuarioDetails.getRoles().isEmpty()) {
                Set<Rol> roles = usuarioDetails.getRoles().stream()
                    .map(rolDto -> rolRepository.findById(rolDto.getIdRol())
                                    .orElseThrow(() -> new ValidationException("Rol no encontrado: " + rolDto.getIdRol())))
                    .collect(Collectors.toSet());
                usuario.setRoles(roles);
            }
            
            Usuario nuevoUsuario = usuarioRepository.save(usuario);
            nuevoUsuario.setPasswordHash(null);
            logger.info("Usuario creado exitosamente: {}", nuevoUsuario.getUsername());
            return ResponseEntity.ok(nuevoUsuario);
            
        } catch (ValidationException e) {
            logger.error("Error de validación al crear usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error al crear usuario", e);
            throw new ValidationException("Error al crear usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un usuario existente
     * 
     * @param id ID del usuario a actualizar
     * @param usuarioDetails Nuevos datos del usuario
     * @return Usuario actualizado o 404
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Usuario> updateUsuario(
            @PathVariable @Parameter(description = "ID del usuario") Long id, 
            @Valid @RequestBody @Parameter(description = "Nuevos datos del usuario") Usuario usuarioDetails) {
        logger.info("Actualizando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioDetails.getNombre());
                    usuario.setApellido(usuarioDetails.getApellido());
                    usuario.setUsername(usuarioDetails.getUsername());
                    usuario.setEmail(usuarioDetails.getEmail());
                    usuario.setTelefono(usuarioDetails.getTelefono());
                    usuario.setActivo(usuarioDetails.isActivo());
                    usuario.setTipoContrato(usuarioDetails.getTipoContrato());

                    if (usuarioDetails.getPasswordHash() != null && !usuarioDetails.getPasswordHash().isEmpty()) {
                        usuario.setPasswordHash(passwordEncoder.encode(usuarioDetails.getPasswordHash()));
                    }

                    if (usuarioDetails.getArea() != null && usuarioDetails.getArea().getIdArea() != null) {
                        Area area = areaRepository.findById(usuarioDetails.getArea().getIdArea())
                                        .orElseThrow(() -> new ValidationException("Área no encontrada"));
                        usuario.setArea(area);
                    } else {
                        usuario.setArea(null);
                    }

                    if (usuarioDetails.getRoles() != null && !usuarioDetails.getRoles().isEmpty()) {
                        Set<Rol> roles = usuarioDetails.getRoles().stream()
                            .map(rolDto -> rolRepository.findById(rolDto.getIdRol())
                                            .orElseThrow(() -> new ValidationException("Rol no encontrado: " + rolDto.getIdRol())))
                            .collect(Collectors.toSet());
                        usuario.setRoles(roles);
                    } else {
                        usuario.setRoles(Set.of());
                    }

                    Usuario updatedUsuario = usuarioRepository.save(usuario);
                    updatedUsuario.setPasswordHash(null);
                    logger.info("Usuario actualizado exitosamente: {}", updatedUsuario.getUsername());
                    return ResponseEntity.ok(updatedUsuario);
                })
                .orElseGet(() -> {
                    logger.warn("Usuario no encontrado con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Elimina un usuario del sistema
     * 
     * @param id ID del usuario a eliminar
     * @return Respuesta vacía 200 OK o 404
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> deleteUsuario(@PathVariable @Parameter(description = "ID del usuario") Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioRepository.delete(usuario);
                    logger.info("Usuario eliminado exitosamente: {}", usuario.getUsername());
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> {
                    logger.warn("Usuario no encontrado con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}