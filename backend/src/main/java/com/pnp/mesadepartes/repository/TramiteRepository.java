package com.pnp.mesadepartes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.Tramite;
import com.pnp.mesadepartes.model.Usuario;


@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    List<Tramite> findByUsuarioAsignado(Usuario usuario);
    List<Tramite> findByDocumento(Documento documento);
    List<Tramite> findByUsuarioAsignado_IdUsuario(Long idUsuario);
}
