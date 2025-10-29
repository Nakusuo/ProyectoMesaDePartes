package com.pnp.mesadepartes.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.model.Rol;
import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.AreaRepository;
import com.pnp.mesadepartes.repository.RolRepository;
import com.pnp.mesadepartes.repository.UsuarioRepository;
import com.pnp.mesadepartes.security.jwt.JwtUtils;
import com.pnp.mesadepartes.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600) 
@RestController
@RequestMapping("/api/auth") 
public class AuthController {

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

    @GetMapping("/generate-hash")
    public ResponseEntity<?> generateHash() {
        String password = "123456";
        String hash = encoder.encode(password);
        return ResponseEntity.ok(new MessageResponse("Hash para '" + password + "': " + hash));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserInfoResponse(
                userDetails.getIdUsuario(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwt));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El username ya está en uso!"));
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso!"));
        }

        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().length() < 6) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: La contraseña debe tener al menos 6 caracteres!"));
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
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tipo de contrato inválido. Use: CAS, LOCADOR o PNP"));
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

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }
}