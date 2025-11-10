package com.pnp.mesadepartes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos uploads (documentos)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // Servir archivos estáticos del frontend (HTML, CSS, JS)
        // La ruta es relativa al directorio backend
        registry.addResourceHandler("/**")
                .addResourceLocations("file:../frontend/")
                .setCachePeriod(0); // Sin cache para desarrollo
    }
}
