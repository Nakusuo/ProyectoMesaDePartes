package com.pnp.mesadepartes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    
    // Para buscar por el código único
    Optional<Documento> findByCodigo(String codigo);
}