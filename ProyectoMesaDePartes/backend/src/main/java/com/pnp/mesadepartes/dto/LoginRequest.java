package com.pnp.mesadepartes.dto;

import lombok.Data;

// Lombok @Data crea getters y setters
@Data
public class LoginRequest {
    private String username;
    private String password;
}