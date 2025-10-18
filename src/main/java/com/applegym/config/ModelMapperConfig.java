package com.applegym.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de ModelMapper para conversiones DTO-Entity.
 * 
 * Configura el ModelMapper con estrategias de mapeo apropiadas
 * para la conversión entre DTOs y Entidades.
 * 
 * @author AppleGym Team
 * @version 1.0.0
 */
@Configuration
public class ModelMapperConfig {
    
    /**
     * Bean de ModelMapper configurado con estrategia estricta.
     * 
     * @return ModelMapper configurado
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configurar estrategia de mapeo estricta
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        return mapper;
    }
}