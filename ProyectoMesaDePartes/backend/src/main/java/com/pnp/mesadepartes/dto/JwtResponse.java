package com.pnp.mesadepartes.dto;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long idUsuario;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long idUsuario, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}