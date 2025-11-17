package com.pnp.mesadepartes.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos uploads (documentos)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // Obtener la ruta absoluta del frontend
        String currentDir = System.getProperty("user.dir");
        String frontendPath = Paths.get(currentDir).getParent().resolve("frontend").toUri().toString();
        
        System.out.println("========================================");
        System.out.println("Frontend path configured: " + frontendPath);
        System.out.println("========================================");
        
        // Servir archivos estáticos del frontend (HTML, CSS, JS)
        registry.addResourceHandler("/**")
                .addResourceLocations(frontendPath)
                .setCachePeriod(0); // Sin cache para desarrollo
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redireccionar raíz al login
        registry.addViewController("/").setViewName("forward:/pages/auth/login.html");
    }
}
