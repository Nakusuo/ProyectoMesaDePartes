package com.pnp.mesadepartes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Area;

/**
 * Repositorio para la entidad Área
 * Proporciona métodos de acceso a datos para áreas/dependencias
 * 
 * @author Sistema Mesa de Partes PNP
 * @version 3.1
 * @since 2025-11-21
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    
    /**
     * Busca un área por su nombre exacto
     * @param nombre Nombre del área
     * @return Optional con el área si existe
     */
    Optional<Area> findByNombre(String nombre);
    
    /**
     * Busca un área por su sigla
     * @param sigla Sigla del área
     * @return Optional con el área si existe
     */
    Optional<Area> findBySigla(String sigla);
    
    /**
     * Busca áreas que contengan el texto en su nombre (case-insensitive)
     * @param nombre Texto a buscar en el nombre
     * @return Lista de áreas que coinciden
     */
    @Query("SELECT a FROM Area a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Area> buscarPorNombreContiene(@Param("nombre") String nombre);
    
    /**
     * Verifica si existe un área con el nombre especificado
     * @param nombre Nombre a verificar
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Verifica si existe un área con la sigla especificada
     * @param sigla Sigla a verificar
     * @return true si existe, false si no
     */
    boolean existsBySigla(String sigla);
}

