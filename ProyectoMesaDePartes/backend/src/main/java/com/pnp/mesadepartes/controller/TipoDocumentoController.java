package com.pnp.mesadepartes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pnp.mesadepartes.model.TipoDocumento;
import com.pnp.mesadepartes.repository.TipoDocumentoRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tipos-documento") // Coincide con tu fetch de React
public class TipoDocumentoController {

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @GetMapping
    public List<TipoDocumento> getAllTiposDocumento() {
        return tipoDocumentoRepository.findAll();
    }
}