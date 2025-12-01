package com.pnp.mesadepartes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.HojaTramite;

@Repository
public interface HojaTramiteRepository extends JpaRepository<HojaTramite, Long> {
    @Query("SELECT h FROM HojaTramite h WHERE h.documento.idDocumento = :idDocumento")
    List<HojaTramite> findByIdDocumento(@Param("idDocumento") Long idDocumento);
    
    @Query("SELECT h FROM HojaTramite h WHERE h.documento.idDocumento = :idDocumento")
    Optional<HojaTramite> findFirstByIdDocumento(@Param("idDocumento") Long idDocumento);
}
