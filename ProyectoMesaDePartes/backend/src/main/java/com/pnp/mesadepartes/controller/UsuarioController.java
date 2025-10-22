package com.pnp.mesadepartes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.model.Usuario;
import com.pnp.mesadepartes.repository.UsuarioRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/usuarios") // Tu React lo llama "/api/users", puedes ajustarlo
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        // Ocultar passwords antes de enviar!
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> u.setPasswordHash(null)); 
        return usuarios;
    }
}