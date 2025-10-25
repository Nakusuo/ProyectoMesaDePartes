package com.pnp.mesadepartes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository; // Needed for findByDocumento
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.Tramite; // Needed for findByDocumento
import com.pnp.mesadepartes.model.Usuario;


@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    List<Tramite> findByUsuarioAsignado(Usuario usuario);
    // Added method needed by DocumentoController
    List<Tramite> findByDocumento(Documento documento); 
}
