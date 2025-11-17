package com.pnp.mesadepartes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Documento;
import com.pnp.mesadepartes.model.SalidaDocumento;

@Repository
public interface SalidaDocumentoRepository extends JpaRepository<SalidaDocumento, Long> {
    List<SalidaDocumento> findByDocumento(Documento documento);
    List<SalidaDocumento> findByDocumentoIdDocumentoOrderByFechaSalidaDesc(Long idDocumento);
}
