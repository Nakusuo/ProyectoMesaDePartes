package com.pnp.mesadepartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.SalidaDocumento;

@Repository
public interface SalidaDocumentoRepository extends JpaRepository<SalidaDocumento, Long> {
}