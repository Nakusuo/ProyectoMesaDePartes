package com.pnp.mesadepartes.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generar hashes para las contraseñas comunes
        System.out.println("=== Password Hashes BCrypt ===");
        System.out.println("Contraseña: 12345");
        System.out.println("Hash: " + encoder.encode("12345"));
        System.out.println();
        System.out.println("Contraseña: 123456");
        System.out.println("Hash: " + encoder.encode("123456"));
    }
}
