package com.pnp.mesadepartes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Tramite;
import com.pnp.mesadepartes.model.Usuario;

@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    // Para la bandeja de un trabajador
    List<Tramite> findByUsuarioAsignado(Usuario usuario);
}