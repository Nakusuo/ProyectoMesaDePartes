package com.pnp.mesadepartes.security.jwt;

import com.pnp.mesadepartes.security.services.UserDetailsImpl;

// Imports de JWT
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

// Imports de SLF4J (para el Logger)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Imports de Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

// Imports de Java
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    // Declaración del Logger (necesita org.slf4j.Logger y LoggerFactory)
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Estos valores deberías ponerlos en tu 'application.properties'
    @Value("${mesadepartes.app.jwtSecret}")
    private String jwtSecret;

    @Value("${mesadepartes.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Genera un token para el usuario que inició sesión
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Lee el username desde un token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Valida si el token es correcto
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Claims de JWT vacíos: {}", e.getMessage());
        }
        return false;
    }
}