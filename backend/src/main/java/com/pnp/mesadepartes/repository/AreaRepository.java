package com.pnp.mesadepartes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pnp.mesadepartes.model.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
}
