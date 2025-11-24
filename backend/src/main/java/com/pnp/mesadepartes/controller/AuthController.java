package com.pnp.mesadepartes.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.dto.LoginRequest;
import com.pnp.mesadepartes.dto.MessageResponse;
import com.pnp.mesadepartes.dto.SignupRequest;
import com.pnp.mesadepartes.dto.UserInfoResponse;
import com.pnp.mesadepartes.exception.ValidationException;
import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.model.Rol;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.AreaRepository;
import com.pnp.mesadepartes.repository.RolRepository;
import com.pnp.mesadepartes.repository.UsuarioRepository;
import com.pnp.mesadepartes.security.jwt.JwtUtils;
import com.pnp.mesadepartes.security.services.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador de autenticación y registro de usuarios
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Autenticación", description = "API para autenticación y gestión de sesiones")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Obtiene la información del usuario autenticado actualmente
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Retorna la información del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<?> getCurrentUser() {
        logger.info("Solicitando información del usuario actual");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Intento de acceso sin autenticación");
            return ResponseEntity.status(401).body(new MessageResponse("No autenticado"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Usuario usuario = usuarioRepository.findById(userDetails.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        logger.info("Usuario autenticado: {} con roles: {}", usuario.getUsername(), roles);
        
        // Crear respuesta con toda la información del usuario
        return ResponseEntity.ok(new UserInfoResponse(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEmail(),
                roles,
                null, // No devolver el token aquí
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getArea() != null ? usuario.getArea().getNombre() : null
        ));
    }

    /**
     * Autentica un usuario y genera un token JWT
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Intento de autenticación para usuario: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        logger.info("Autenticación exitosa para usuario: {} con roles: {}", loginRequest.getUsername(), roles);

        return ResponseEntity.ok(new UserInfoResponse(
                userDetails.getIdUsuario(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwt));
    }

    /**
     * Registra un nuevo usuario en el sistema
     */
    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("Intento de registro de nuevo usuario: {}", signUpRequest.getUsername());
        
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.warn("Intento de registro con username duplicado: {}", signUpRequest.getUsername());
            throw new ValidationException("El username ya está en uso");
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Intento de registro con email duplicado: {}", signUpRequest.getEmail());
            throw new ValidationException("El email ya está en uso");
        }

        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().length() < 6) {
            logger.warn("Intento de registro con contraseña débil");
            throw new ValidationException("La contraseña debe tener al menos 6 caracteres");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setApellido(signUpRequest.getApellido());
        usuario.setUsername(signUpRequest.getUsername());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setTelefono(signUpRequest.getTelefono());
        usuario.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        
        try {
            usuario.setTipoContrato(com.pnp.mesadepartes.model.TipoContrato.valueOf(signUpRequest.getTipoContrato()));
        } catch (IllegalArgumentException e) {
            logger.warn("Tipo de contrato inválido: {}", signUpRequest.getTipoContrato());
            throw new ValidationException("Tipo de contrato inválido. Use: CAS, LOCADOR o PNP");
        }

        if (signUpRequest.getIdArea() != null) {
            Area area = areaRepository.findById(signUpRequest.getIdArea())
                    .orElseThrow(() -> new RuntimeException("Error: Área no encontrada."));
            usuario.setArea(area);
        }

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Rol trabajadorRole = rolRepository.findByNombre("Trabajador")
                    .orElseThrow(() -> new RuntimeException("Error: Rol Trabajador no encontrado."));
            roles.add(trabajadorRole);
        } else {
            strRoles.forEach(role -> {
                Rol foundRole = rolRepository.findByNombre(role)
                        .orElseThrow(() -> new RuntimeException("Error: Rol " + role + " no encontrado."));
                roles.add(foundRole);
            });
        }

        usuario.setRoles(roles);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        logger.info("Usuario registrado exitosamente: {} con roles: {}", usuario.getUsername(), strRoles);
        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }
}