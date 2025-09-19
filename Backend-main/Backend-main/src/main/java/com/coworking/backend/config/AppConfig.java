package com.coworking.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration générale de l'application.
 * Définit les beans partagés.
 */
@Configuration
public class AppConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // Configurer le ModelMapper ici si nécessaire
        return mapper;
    }
}