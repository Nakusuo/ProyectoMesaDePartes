package com.pnp.mesadepartes.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Derivacion;
import com.pnp.mesadepartes.model.Documento;

@Repository
public interface DerivacionRepository extends JpaRepository<Derivacion, Long> {
    
    List<Derivacion> findByDocumentoOrderByFechaDerivacionDesc(Documento documento);
    
    List<Derivacion> findByDocumentoIdDocumentoOrderByFechaDerivacionDesc(Long idDocumento);
    
    List<Derivacion> findByAreaDestinoIdAreaOrderByFechaDerivacionDesc(Long idArea);
    
    List<Derivacion> findByUsuarioRecibeIdUsuarioAndEstadoOrderByFechaDerivacionDesc(Long idUsuario, String estado);
    
    @Query("SELECT d FROM Derivacion d WHERE d.areaDestino.idArea = :idArea AND d.fechaDerivacion BETWEEN :fechaInicio AND :fechaFin")
    List<Derivacion> findByAreaAndFechaBetween(@Param("idArea") Long idArea, 
                                                 @Param("fechaInicio") LocalDateTime fechaInicio,
                                                 @Param("fechaFin") LocalDateTime fechaFin);
    
    Long countByAreaDestinoIdAreaAndEstado(Long idArea, String estado);
}
