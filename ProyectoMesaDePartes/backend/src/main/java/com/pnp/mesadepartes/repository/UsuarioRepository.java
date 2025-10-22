package com.pnp.mesadepartes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data JPA entenderá este nombre de método y creará la consulta:
    // "SELECT * FROM usuarios WHERE username = ?"
    Optional<Usuario> findByUsername(String username);

    // "SELECT * FROM usuarios WHERE email = ?"
    Optional<Usuario> findByEmail(String email);

    // "SELECT * FROM usuarios WHERE telefono = ?"
    Optional<Usuario> findByTelefono(String telefono);

    // "SELECT EXISTS(SELECT 1 FROM usuarios WHERE username = ?)"
    Boolean existsByUsername(String username);

    // "SELECT EXISTS(SELECT 1 FROM usuarios WHERE email = ?)"
    Boolean existsByEmail(String email);
}