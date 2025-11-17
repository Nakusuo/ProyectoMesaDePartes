package com.pnp.mesadepartes.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private Long idUsuario;
    private String username;
    private String email;
    private List<String> roles;
    private String token;
    private String nombre;
    private String apellido;
    private String area;

    // Constructor para login (con token)
    public UserInfoResponse(Long idUsuario, String username, String email, List<String> roles, String token) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.token = token;
    }

    // Constructor sin token (para endpoint /me)
    public UserInfoResponse(Long idUsuario, String username, String email, List<String> roles) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
