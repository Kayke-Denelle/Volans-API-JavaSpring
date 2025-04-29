package com.example.backend.Security.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir origens específicas
        config.addAllowedOrigin("https://volans-interface.vercel.app");
        config.addAllowedOrigin("http://localhost:3000");
        
        // Permitir métodos HTTP
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // Permitir headers
        config.addAllowedHeader("*");
        
        // Expor headers específicos
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        
        // Permitir credenciais
        config.setAllowCredentials(true);
        
        // Configurar tempo de cache
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}