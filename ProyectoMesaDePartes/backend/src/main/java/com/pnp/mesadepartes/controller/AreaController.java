package com.pnp.mesadepartes.controller;

import com.pnp.mesadepartes.model.Area;
import com.pnp.mesadepartes.repository.AreaRepository; // Asegúrate de crear este repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/areas") // Coincide con tu fetch de React
public class AreaController {

    @Autowired
    private AreaRepository areaRepository; // Necesitarás crear este archivo

    @GetMapping
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }
}