package com.pnp.mesadepartes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuración personalizadas de la aplicación.
 * Evita warnings de propiedades no reconocidas en application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "mesadepartes.app")
public class AppProperties {
    
    private String jwtSecret;
    private Long jwtExpirationMs;
    private String allowedOrigins;
    private String emailFrom;
    private String uploadDir;

    // Getters y Setters
    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public void setJwtExpirationMs(Long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
