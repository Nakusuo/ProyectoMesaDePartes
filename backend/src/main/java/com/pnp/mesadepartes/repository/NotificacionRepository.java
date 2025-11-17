package com.pnp.mesadepartes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
    
    List<Notificacion> findByUsuarioIdUsuarioAndLeidaOrderByFechaCreacionDesc(Long idUsuario, Boolean leida);
    
    Long countByUsuarioIdUsuarioAndLeida(Long idUsuario, Boolean leida);
    
    List<Notificacion> findTop10ByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
}
