package com.pnp.mesadepartes.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private Long idUsuario;
    private String username;
    private String email;
    private List<String> roles;
    private String token;

    public UserInfoResponse(Long idUsuario, String username, String email, List<String> roles) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
