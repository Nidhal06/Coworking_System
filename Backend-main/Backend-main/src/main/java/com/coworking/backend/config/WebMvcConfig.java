package com.coworking.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration MVC personnalisée pour :
 * - CORS
 * - Gestion des ressources statiques (uploads, swagger-ui)
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${facture.storage-dir}")
    private String factureStorageDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // Autoriser uniquement le frontend Angular
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Authorization") // Exposer le header Authorization
                .allowCredentials(true) // Autoriser les credentials
                .maxAge(3600); // Cache des pré-vérifications CORS (1 heure)
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuration Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .resourceChain(false);
                
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
                
        // Configuration des uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
                
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        
        // Configuration des factures
        registry.addResourceHandler("/factures/**")
                .addResourceLocations("file:" + factureStorageDir + "/");
    }
}